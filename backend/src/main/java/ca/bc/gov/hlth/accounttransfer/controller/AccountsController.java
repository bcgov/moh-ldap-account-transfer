package ca.bc.gov.hlth.accounttransfer.controller;

import javax.validation.Valid;

import ca.bc.gov.hlth.accounttransfer.model.rest.ldap.LdapRequest;
import ca.bc.gov.hlth.accounttransfer.model.rest.ldap.LdapResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.hlth.accounttransfer.model.rest.accountTransfer.AccountTransferRequest;
import ca.bc.gov.hlth.accounttransfer.service.LdapService;

/**
 * Handle requests related to Account Transfers.
 * 
 */
@RestController
public class AccountsController {

	private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

	@Autowired
	private LdapService ldapService;
	
	/**
	 * Sample post endpoint for the application 
	 *  
	 * @param accountTransferRequest account transfer request
	 * @return The result of the operation
	 */
	@PostMapping("/accountTransfer")
	public ResponseEntity<LdapResponse> transferAccount(@Valid @RequestBody AccountTransferRequest accountTransferRequest) {

		LdapRequest ldapRequest = new LdapRequest(accountTransferRequest.getUsername(), accountTransferRequest.getPassword());
		LdapResponse ldapResponse = ldapService.checkLdapAccount(ldapRequest);

		ResponseEntity<LdapResponse> response = ResponseEntity.ok(ldapResponse);
		
		return response;	
	}

}
