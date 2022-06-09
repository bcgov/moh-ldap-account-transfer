package ca.bc.gov.hlth.accounttransfer.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.bc.gov.hlth.accounttransfer.exception.AccountTransferException;
import ca.bc.gov.hlth.accounttransfer.model.accountTransfer.AccountTransferRequest;
import ca.bc.gov.hlth.accounttransfer.model.accountTransfer.AccountTransferResponse;
import ca.bc.gov.hlth.accounttransfer.model.accountTransfer.StatusEnum;
import ca.bc.gov.hlth.accounttransfer.model.keycloak.User;
import ca.bc.gov.hlth.accounttransfer.model.ldap.LdapRequest;
import ca.bc.gov.hlth.accounttransfer.model.ldap.LdapResponse;
import ca.bc.gov.hlth.accounttransfer.model.ldap.OrgDetails;
import ca.bc.gov.hlth.accounttransfer.service.KeycloakUserManagementService;
import ca.bc.gov.hlth.accounttransfer.service.LdapService;
import ca.bc.gov.hlth.accounttransfer.util.keycloak.ClientsLookup;

/**
 * Handle requests related to Account Transfers.
 * 
 */
@RestController
public class AccountsController {

	private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

	private static final String MSPDIRECT = "MSPDIRECT-SERVICE";
	private static final String SUB_CLAIM = "sub";
	private static final String ATTRIBUTE_ORG_DETAILS = "org_details";
	private static final String ATTRIBUTE_ROLES = "roles";
	private static final String CLAIM_RESOURCE_ACCESS = "resource_access";

	private static final String ERROR_INVALID_USER_PASS = "Invalid Username or Password";
	private static final String ERROR_ACCOUNT_LOCKED = "Account is locked";
	private static final String ERROR_NO_ROLE = "User has no role";

	@Autowired
	private ClientsLookup clientsLookup;

	@Autowired
	private LdapService ldapService;

	@Autowired
	private KeycloakUserManagementService keycloakUserManagementService;

	/**
	 * Accepts an account transfer request and, if the credentials are valid,
	 * transfers the specified application roles from ldap to Keycloak
	 * 
	 * @param accountTransferRequest account transfer request
	 * @return The result of the operation
	 */
	@PostMapping("/accountTransfer")
	public ResponseEntity<AccountTransferResponse> transferAccount(
			@Valid @RequestBody AccountTransferRequest accountTransferRequest) {

		Jwt authToken = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId = authToken.getClaimAsString(SUB_CLAIM);
		List<String> kcRole = loadRoles(authToken);
		boolean isRoleAlreadyTransferred = false;
		boolean isOrgAlreadyTranseferred = false;

		logger.debug("Attempting account transfer for User {} on Application {}", accountTransferRequest.getUsername(),
				accountTransferRequest.getApplication());

		LdapRequest ldapRequest = new LdapRequest(accountTransferRequest.getUsername(),
				accountTransferRequest.getPassword());
		LdapResponse ldapResponse = ldapService.checkLdapAccount(ldapRequest);

		String errorMessage = validateLdapResponse(ldapResponse);
		if (StringUtils.isNotBlank(errorMessage)) {
			AccountTransferResponse response = new AccountTransferResponse(StatusEnum.ERROR, errorMessage);
			return ResponseEntity.ok(response);
		}

		// Authentication success, account unlocked, user has a role for the mspdirect
		// Add the role to the user in Keycloak
		if (StringUtils.equals(accountTransferRequest.getApplication(), MSPDIRECT)) {

			logger.debug("User has role {}", ldapResponse.getMspDirectRole().toUpperCase());

			// Assign only if role not exists in KC for the user
			if (!roleExists(kcRole, ldapResponse.getMspDirectRole().toUpperCase())) {

				String idOfClient = clientsLookup.getClients().get(MSPDIRECT).getId();
				Object roleRepresentation = clientsLookup.getClients().get(MSPDIRECT).getRoles()
						.get(ldapResponse.getMspDirectRole().toUpperCase());

				// If ldapResponse.mspDirectRole is not supported in the new MSP Direct -> role
				// is invalid
				if (roleRepresentation == null) {
					AccountTransferResponse response = new AccountTransferResponse(StatusEnum.ERROR, String.format(
							"%s is not a valid %s role", ldapResponse.getMspDirectRole().toUpperCase(), MSPDIRECT));
					return ResponseEntity.ok(response);
				}
				// Keycloak expects the representation of roles to add in an array
				Object[] rolesToSend = { roleRepresentation };

				ResponseEntity<String> kcResponse = keycloakUserManagementService.addRoleToUser(userId, idOfClient,
						rolesToSend);
				if (kcResponse.getStatusCode() != HttpStatus.NO_CONTENT) {
					AccountTransferResponse response = new AccountTransferResponse(StatusEnum.ERROR,
							String.format("Error transferring role %s for application %s",
									ldapResponse.getMspDirectRole().toUpperCase(), MSPDIRECT));
					return ResponseEntity.ok(response);
				}
			} else {
				isRoleAlreadyTransferred = true;
				logger.info(String.format("%s role is already assigned to user ",
						ldapResponse.getMspDirectRole().toUpperCase()));
			}

			// If the role transfer was successful, transfer the Organization as well
			Boolean transferError = Boolean.FALSE;
			try {
				if (!transferOrganization(userId, ldapResponse.getOrgDetails())) {
					isOrgAlreadyTranseferred = true;
				} else {
					isOrgAlreadyTranseferred = false;
				}
			} catch (AccountTransferException e) {
				logger.error(e.getMessage());
				transferError = Boolean.TRUE;
			}

			if (Boolean.TRUE.equals(isRoleAlreadyTransferred && Boolean.TRUE.equals(isOrgAlreadyTranseferred))) {
				AccountTransferResponse response = new AccountTransferResponse(StatusEnum.SUCCESS,
						String.format("Account already transferred for the role %s for application %s",
								ldapResponse.getMspDirectRole().toUpperCase(), MSPDIRECT));
				return ResponseEntity.ok(response);
			} else if (Boolean.TRUE.equals(isRoleAlreadyTransferred || Boolean.TRUE.equals(isOrgAlreadyTranseferred))) {
				AccountTransferResponse response = new AccountTransferResponse(StatusEnum.SUCCESS,
						String.format("Account partially transeferred for the role %s for application %s",
								ldapResponse.getMspDirectRole().toUpperCase(), MSPDIRECT));
				return ResponseEntity.ok(response);
			}

			StringBuilder sb = new StringBuilder();
			sb.append(String.format("Account transfer successful for user %s for the application %s with the role %s",
					accountTransferRequest.getUsername(), MSPDIRECT, ldapResponse.getMspDirectRole().toUpperCase()));
			if (Boolean.TRUE.equals(transferError)) {
				sb.append(
						"\nNote: Organization transfer failed. Please contact support to have your organization set up manually.");
			}
			AccountTransferResponse response = new AccountTransferResponse(StatusEnum.SUCCESS, sb.toString(),
					ldapResponse.getMspDirectRole().toUpperCase());

			return ResponseEntity.ok(response);
		}

		// Unknown application. Only MSPDIRECT is currently supported
		AccountTransferResponse response = new AccountTransferResponse(StatusEnum.ERROR,
				String.format("Account transfer for %s is not supported", accountTransferRequest.getApplication()));
		return ResponseEntity.ok(response);
	}

	/**
	 * Transfers the users Organization from LDAP to Keycloak.
	 * 
	 * @param userId  The Keycloak userId
	 * @param ldapOrg The organization in LDAP.
	 * @throws AccountTransferException
	 */
	private boolean transferOrganization(String userId, OrgDetails ldapOrg) throws AccountTransferException {
		// Look up the user in Keycloak/UMS
		ResponseEntity<User> getUserResponse = keycloakUserManagementService.getUser(userId);

		if (getUserResponse.getStatusCode() != HttpStatus.OK) {
			throw new AccountTransferException(String.format("Could not get User info for user %s", userId));
		}
		User user = getUserResponse.getBody();

		// Check if the org is already assigned
		List<String> orgDetails = loadOrgDetails(user);
		if (organizationExists(orgDetails, ldapOrg)) {
			logger.debug("Organization {} is already assigned to User {}", ldapOrg.getId(), user.getUsername());
			return false;
		}

		// Transfer the organization
		ObjectMapper mapper = new ObjectMapper();
		try {
			String newOrg = mapper.writeValueAsString(ldapOrg);
			orgDetails.add(newOrg);
		} catch (JsonProcessingException e) {
			throw new AccountTransferException("Could not convert LDAP Organization", e);
		}

		// Update the user in Keycloak/UMS
		ResponseEntity<String> updateUserResponse = keycloakUserManagementService.updateUser(userId, user);
		if (updateUserResponse.getStatusCode() != HttpStatus.NO_CONTENT) {
			throw new AccountTransferException(
					String.format("Error adding organization %s to user %s", ldapOrg.getId(), user.getUsername()));
		}

		logger.debug("Organization {} assigned to User {}", ldapOrg.getId(), user.getUsername());
		return true;
	}

	
	@SuppressWarnings("unchecked")
	private List<String> loadOrgDetails(User user) {
		if (user.getAttributes() == null) {
			HashMap<String, Object> attributes = new LinkedHashMap<>();
			user.setAttributes(attributes);
		}

		// Check if the org is already assigned
		HashMap<String, Object> attributes = (HashMap<String, Object>) user.getAttributes();
		List<String> orgDetails = (List<String>) attributes.get(ATTRIBUTE_ORG_DETAILS);
		if (orgDetails == null) {
			orgDetails = new ArrayList<>();
			attributes.put(ATTRIBUTE_ORG_DETAILS, orgDetails);
		}

		return orgDetails;
	}

	@SuppressWarnings("unchecked")
	private static List<String> loadRoles(Jwt jwt) {
		List<String> permissions = new ArrayList<>();

		Map<String, Object> resourceAccesses = (Map<String, Object>) jwt.getClaims().get(CLAIM_RESOURCE_ACCESS);

		if (resourceAccesses == null) {
			return permissions;
		}

		Map<String, Object> resource = (Map<String, Object>) resourceAccesses.get(MSPDIRECT);
		if (resource == null) {
			return permissions;
		}

		return (List<String>) resource.get(ATTRIBUTE_ROLES);
	}

	/**
	 * Checks if the LDAP role exists on the Keycloak user.
	 * 
	 * @param kcRole   The role from Keycloak
	 * @param ldapRole The LDAP role
	 * @return True if the role already exists
	 */
	private Boolean roleExists(List<String> kcRole, String ldapRole) {
		return kcRole.contains(ldapRole);

	}

	/**
	 * Checks if the LDAP organization exists on the Keycloak user.
	 * 
	 * @param kcOrgs  The orgs from Keycloak
	 * @param ldapOrg The LDAP organization
	 * @return True if the organization already exists
	 */
	private Boolean organizationExists(List<String> kcOrgs, OrgDetails ldapOrg) {
		ObjectMapper mapper = new ObjectMapper();
		for (String org : kcOrgs) {
			try {
				OrgDetails kcOrg = mapper.readValue(org, OrgDetails.class);
				if (kcOrg.equals(ldapOrg)) {
					return Boolean.TRUE;
				}
			} catch (JsonProcessingException e) {
				// This is unlikely. Just log and move on.
				logger.error(e.getMessage());
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * Validates that the LDAP response doesn't have any error conditions.
	 * 
	 * @param ldapResponse The response from LDAP
	 * @return An error message if an error ocurred
	 */
	private String validateLdapResponse(LdapResponse ldapResponse) {
		// If ldapResponse.username == '' -> error username doesn't exist
		if (StringUtils.isEmpty(ldapResponse.getUserName())) {
			return ERROR_INVALID_USER_PASS;
		}
		// If ldapResponse.authenticated == false -> error user put in the wrong
		// password
		if (!ldapResponse.getAuthenticated()) {
			if (ldapResponse.getUnlocked()) {
				return ERROR_INVALID_USER_PASS;
			} else {
				// If ldapResponse.locked == true -> error account is locked
				return ERROR_ACCOUNT_LOCKED;
			}
		}
		// If ldapResponse.mspDirectRole = '' -> error user doesn't actually have the
		// role
		if (StringUtils.isBlank(ldapResponse.getMspDirectRole())) {
			return ERROR_NO_ROLE;
		}

		return null;
	}

}
