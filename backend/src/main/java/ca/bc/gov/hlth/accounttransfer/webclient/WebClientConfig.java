package ca.bc.gov.hlth.accounttransfer.webclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Configuration
public class WebClientConfig {

    private final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${ldap-api.url}")
    private String ldapApiUrl;

    @Bean("ldapWebClient")
    public WebClient ldapWebClient() {

        return WebClient.builder()
                .baseUrl(ldapApiUrl)
                .filter(logRequest())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Log request details for the downstream web service calls.
     */
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(c -> {
            logger.debug("Request: {} {}", c.method(), c.url());
            c.headers().forEach((n, v) -> logger.debug("request header: {}={}", n, v));
            return Mono.just(c);
        });
    }
}
