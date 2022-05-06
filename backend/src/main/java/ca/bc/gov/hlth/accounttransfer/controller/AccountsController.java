package ca.bc.gov.hlth.accounttransfer.controller;

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

import ca.bc.gov.hlth.accounttransfer.model.accountTransfer.AccountTransferRequest;
import ca.bc.gov.hlth.accounttransfer.model.accountTransfer.AccountTransferResponse;
import ca.bc.gov.hlth.accounttransfer.model.accountTransfer.StatusEnum;
import ca.bc.gov.hlth.accounttransfer.model.ldap.LdapRequest;
import ca.bc.gov.hlth.accounttransfer.model.ldap.LdapResponse;
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
	
	private static final String ERROR_INVALID_USER_PASS = "Invalid Username or Password";
	private static final String ERROR_ACCOUNT_LOCKED = "Account is locked";
	private static final String ERROR_NO_ROLE = "User has no role";
	
	@Autowired
	private ClientsLookup clientsLookup;

	@Autowired
	private LdapService ldapService;

	@Autowired
	private KeycloakUserManagementService keycloakUserManagementService;

	public AccountsController() {

	}
	
	/**
	 * Accepts an account transfer request and, if the credentials are valid, transfers the specified application roles
	 * from ldap to Keycloak
	 *  
	 * @param accountTransferRequest account transfer request
	 * @return The result of the operation
	 */
	@PostMapping("/accountTransfer")
	public ResponseEntity<AccountTransferResponse> transferAccount(@Valid @RequestBody AccountTransferRequest accountTransferRequest) {

		Jwt authToken = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId = authToken.getClaimAsString(SUB_CLAIM);

		logger.debug("Attempting account transfer for User {} on Application {}", accountTransferRequest.getUsername(), accountTransferRequest.getApplication());

		LdapRequest ldapRequest = new LdapRequest(accountTransferRequest.getUsername(), accountTransferRequest.getPassword());
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

			String idOfClient = clientsLookup.getClients().get(MSPDIRECT).getId();
			// TODO VISARESIDENT role with return null here because it's now called VISARESIDENTS
			Object roleRepresentation = clientsLookup.getClients().get(MSPDIRECT).getRoles().get(ldapResponse.getMspDirectRole().toUpperCase());
			
			// If ldapResponse.mspDirectRole is not supported in the new MSP Direct -> role is invalid
			if (roleRepresentation == null) {
				AccountTransferResponse response = new AccountTransferResponse(StatusEnum.ERROR, String.format("%s is not a valid %s role", ldapResponse.getMspDirectRole().toUpperCase(), MSPDIRECT));
				return ResponseEntity.ok(response);		
			}
			
			// Keycloak expects the representation of roles to add in an array
			Object[] rolesToSend = { roleRepresentation };

			ResponseEntity<String> kcResponse = keycloakUserManagementService.addRoleToUser(userId, idOfClient, rolesToSend);
			if (kcResponse.getStatusCode() != HttpStatus.NO_CONTENT) {
				AccountTransferResponse response = new AccountTransferResponse(StatusEnum.ERROR, String.format("Error transferring role %s for application %s", ldapResponse.getMspDirectRole().toUpperCase(), MSPDIRECT));
				return ResponseEntity.ok(response);
			}

			String responseMessage = String.format("Account transfer successful for user %s for the application %s with the role %s", accountTransferRequest.getUsername(), MSPDIRECT, ldapResponse.getMspDirectRole().toUpperCase());
			AccountTransferResponse response = new AccountTransferResponse(StatusEnum.SUCCESS, responseMessage, ldapResponse.getMspDirectRole().toUpperCase());

			return ResponseEntity.ok(response);
		}

		// Unknown application. Only MSPDIRECT is currently supported
		AccountTransferResponse response = new AccountTransferResponse(StatusEnum.ERROR, String.format("Account transfer for %s is not supported", accountTransferRequest.getApplication()));
		return ResponseEntity.ok(response);
	}
	
	private String validateLdapResponse(LdapResponse ldapResponse) {
		// If ldapResponse.username == '' -> error username doesn't exist
		if (StringUtils.isEmpty(ldapResponse.getUserName())) {
			return ERROR_INVALID_USER_PASS;
		}
		// If ldapResponse.authenticated == false -> error user put in the wrong password
		if (!ldapResponse.getAuthenticated()) {
			if (ldapResponse.getUnlocked()) {
				return ERROR_INVALID_USER_PASS;
			} else {
				// If ldapResponse.locked == true -> error account is locked
				return ERROR_ACCOUNT_LOCKED;
			}
		}
		// If ldapResponse.mspDirectRole = '' -> error user doesn't actually have the role
		if (StringUtils.isBlank(ldapResponse.getMspDirectRole())) {
			return ERROR_NO_ROLE;
		}
		
		return null;
	}

}
