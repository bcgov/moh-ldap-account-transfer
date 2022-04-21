package ca.bc.gov.hlth.accounttransfer.controller;

import javax.validation.Valid;

import ca.bc.gov.hlth.accounttransfer.applications.ClientsLookup;
import ca.bc.gov.hlth.accounttransfer.applications.KeycloakClient;
import ca.bc.gov.hlth.accounttransfer.model.rest.StatusEnum;
import ca.bc.gov.hlth.accounttransfer.model.rest.accountTransfer.AccountResponse;
import ca.bc.gov.hlth.accounttransfer.model.rest.ldap.LdapRequest;
import ca.bc.gov.hlth.accounttransfer.model.rest.ldap.LdapResponse;
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

import ca.bc.gov.hlth.accounttransfer.model.rest.accountTransfer.AccountTransferRequest;
import ca.bc.gov.hlth.accounttransfer.service.LdapService;

import java.util.Arrays;

/**
 * Handle requests related to Account Transfers.
 * 
 */
@RestController
public class AccountsController {

	private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

	private static final String MSPDIRECT = "MSPDIRECT-SERVICE";
	private static final String subClaim = "sub";

	@Autowired
	private ClientsLookup clientsLookup;

	@Autowired
	private LdapService ldapService;

	@Autowired
	private KeycloakUserManagementService keycloakUserManagementService;

	
	/**
	 * Sample post endpoint for the application 
	 *  
	 * @param accountTransferRequest account transfer request
	 * @return The result of the operation
	 */
	@PostMapping("/accountTransfer")
	public ResponseEntity<AccountResponse> transferAccount(@Valid @RequestBody AccountTransferRequest accountTransferRequest) {

		Jwt authToken = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId = authToken.getClaimAsString(subClaim);

		LdapRequest ldapRequest = new LdapRequest(accountTransferRequest.getUsername(), accountTransferRequest.getPassword());
		LdapResponse ldapResponse = ldapService.checkLdapAccount(ldapRequest);

		// todo if ldapResponse.username == '' -> error username doesn't exist
		// todo if ldapResponse.authenticated == false -> error user is locked
		// todo if ldapResponse.locked == true -> error account is locked
		// todo if ldapResponse.mspDirectRole = '' -> error user doesn't actually have the role

		// Authentication success, account unlocked, user has a role for the application
		// Add the role to the user in Keycloak
		if (accountTransferRequest.getApplication().equals(MSPDIRECT) && !ldapResponse.getMspDirectRole().equals("")) {

			String idOfClient = clientsLookup.getClients().get(MSPDIRECT).getId();
			Object roleRepresentation = clientsLookup.getClients().get(MSPDIRECT).getRoles().get(ldapResponse.getMspDirectRole().toUpperCase());
			// Keycloak expects the representation of roles to add in an array
			Object[] rolesToSend = { roleRepresentation };

			keycloakUserManagementService.addRoleToUser(userId, idOfClient, rolesToSend);

			AccountResponse response = new AccountResponse();
			response.setStatus(StatusEnum.SUCCESS);
			response.setMessage("Account transfer succeeded for user " + ldapResponse.getUsername() + " with application " + MSPDIRECT + " and role " + ldapResponse.getMspDirectRole());
			ResponseEntity<AccountResponse> responseEntity = ResponseEntity.ok(response);
			return responseEntity;
		}
		return null;


	}

}
