package ca.bc.gov.hlth.accounttransfer.service;

import ca.bc.gov.hlth.accounttransfer.model.rest.ldap.LdapRequest;
import ca.bc.gov.hlth.accounttransfer.model.rest.ldap.LdapResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Service to call the LDAP API
 *
 */
@Service
public class LdapService {

	@Autowired
	private WebClient ldapWebClient;

	public LdapResponse checkLdapAccount(LdapRequest userInfo) {
		return ldapWebClient
				.post()
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(userInfo)
				.retrieve()
				.bodyToMono(LdapResponse.class)
				.block();
	}

}