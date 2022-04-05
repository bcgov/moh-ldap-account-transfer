package ca.bc.gov.hlth.accounttransfer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Sample service
 *
 */
@Service
public class AccountsService {

	private static final Logger logger = LoggerFactory.getLogger(AccountsService.class);

	/**
	 * Sample service method
	 * 
	 * @param name
	 * @return
	 */
	public String getAccountId(String name) {
		logger.debug("getAccount {}", name);
		return "1234567890";
	}	
}