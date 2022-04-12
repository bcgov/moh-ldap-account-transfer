package ca.bc.gov.hlth.accounttransfer.controller;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.hlth.accounttransfer.model.rest.StatusEnum;
import ca.bc.gov.hlth.accounttransfer.model.rest.accounts.AccountRequest;
import ca.bc.gov.hlth.accounttransfer.model.rest.accounts.AccountResponse;
import ca.bc.gov.hlth.accounttransfer.service.AccountsService;

/**
 * Sample controller, handle requests related to Account Transfers.
 * TODO (dbarrett) Remove this example and associated code when actual controllers have been added.
 * 
 */
@RequestMapping("/accounts")
@RestController
public class AccountsController {

	private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

	@Autowired
	private AccountsService accountsService;
	
	/**
	 * Sample post endpoint for the application 
	 *  
	 * @param the accountRequest
	 * @return The sample response
	 */
	@PostMapping("/get-account")
	public ResponseEntity<AccountResponse> getAccount(@Valid @RequestBody AccountRequest accountRequest) {
		logger.debug("getAccount request: {} ", accountRequest.getName());
		
		String accountId = accountsService.getAccountId(accountRequest.getName());				
		AccountResponse accountResponse = new AccountResponse();
		accountResponse.setId(accountId);
		accountResponse.setMessage("Successful");
		accountResponse.setStatus(StatusEnum.SUCCESS);
		
		ResponseEntity<AccountResponse> response = ResponseEntity.ok(accountResponse);
		logger.debug("getAccount response: {} ", accountResponse.getId());
		
		return response;	
	}

	/**
	 * Sample get endpoint for the application 
	 *  
	 * @param the name of the account
	 * @return The sample response
	 */
	@GetMapping("/{name}")
	public ResponseEntity<AccountResponse> getAccount(@PathVariable("name") String name) {
		logger.debug("getAccount name param: {} ", name);
		
		String accountId = accountsService.getAccountId(name);
		
		AccountResponse accountResponse = buildSampleResponse(name, accountId);
		
		ResponseEntity<AccountResponse> response = ResponseEntity.ok(accountResponse);
		logger.debug("getAccount response: {} ", accountResponse.getId());
		
		return response;	
	}

	private AccountResponse buildSampleResponse(String name, String accountId) {
		AccountResponse accountResponse = new AccountResponse();
		accountResponse.setId(accountId);		
		if (StringUtils.equals("b", name)) {
			accountResponse.setMessage("Unsuccessful");
			accountResponse.setStatus(StatusEnum.ERROR);
		} else if (StringUtils.equals("c", name)) {
			accountResponse.setMessage("Success with warning");
			accountResponse.setStatus(StatusEnum.WARNING);
		} else {
			accountResponse.setMessage("Successful");
			accountResponse.setStatus(StatusEnum.SUCCESS);
		}
		return accountResponse;
	}

}
