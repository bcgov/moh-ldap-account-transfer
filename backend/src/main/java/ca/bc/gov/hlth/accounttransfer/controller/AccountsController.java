package ca.bc.gov.hlth.accounttransfer.controller;

import javax.validation.Valid;

import ca.bc.gov.hlth.accounttransfer.model.accountTransfer.AccountTransferResponse;
import ca.bc.gov.hlth.accounttransfer.model.accountTransfer.StatusEnum;
import ca.bc.gov.hlth.accounttransfer.util.keycloak.ClientsLookup;
import ca.bc.gov.hlth.accounttransfer.model.ldap.LdapRequest;
import ca.bc.gov.hlth.accounttransfer.model.ldap.LdapResponse;
import ca.bc.gov.hlth.accounttransfer.service.KeycloakUserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.hlth.accounttransfer.model.accountTransfer.AccountTransferRequest;
import ca.bc.gov.hlth.accounttransfer.service.LdapService;

/**
 * Handle requests related to Account Transfers.
 * 
 */
@RestController
public class AccountsController {

	private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

	private static final String MSPDIRECT = "MSPDIRECT-SERVICE";
	private static final String SUB_CLAIM = "sub";

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

		// TODO if ldapResponse.username == '' -> error username doesn't exist
		// TODO if ldapResponse.authenticated == false -> error user is locked
		// TODO if ldapResponse.locked == true -> error account is locked
		// TODO if ldapResponse.mspDirectRole = '' -> error user doesn't actually have the role
		// TODO if ldapResponse.mspDirectRole is not supported in the new MSP Direct -> role is invalid

		// Authentication success, account unlocked, user has a role for the mspdirect
		// Add the role to the user in Keycloak
		if (accountTransferRequest.getApplication().equals(MSPDIRECT) && !ldapResponse.getMspDirectRole().equals("")) {

			logger.debug("User has role {}", ldapResponse.getMspDirectRole().toUpperCase());

			String idOfClient = clientsLookup.getClients().get(MSPDIRECT).getId();
			// TODO VISARESIDENT role with return null here because it's now called VISARESIDENTS
			Object roleRepresentation = clientsLookup.getClients().get(MSPDIRECT).getRoles().get(ldapResponse.getMspDirectRole().toUpperCase());
			// Keycloak expects the representation of roles to add in an array
			Object[] rolesToSend = { roleRepresentation };

			keycloakUserManagementService.addRoleToUser(userId, idOfClient, rolesToSend);

			String responseMessage = "Account transfer succeeded for user " + accountTransferRequest.getUsername() + " for the application " + MSPDIRECT + " with the role " + ldapResponse.getMspDirectRole().toUpperCase();
			AccountTransferResponse response = new AccountTransferResponse(StatusEnum.SUCCESS, responseMessage, ldapResponse.getMspDirectRole().toUpperCase());

			return ResponseEntity.ok(response);
		}

		// TODO Once error handling is in place we should always return something specific
		return null;

	}

}
