package ca.bc.gov.hlth.accounttransfer.webclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Configuration
public class WebClientConfig {

    private final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${ldap-api.url}")
    private String ldapApiUrl;

    @Value("${user-management.url}")
    private String userManagementApiUrl;

    @Bean("ldapWebClient")
    public WebClient ldapWebClient() {

        return WebClient.builder()
                .baseUrl(ldapApiUrl)
                .filter(logRequest())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService clientService) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, clientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    @Bean("userManagementWebClient")
    public WebClient userManagementWebClient(OAuth2AuthorizedClientManager authorizedClientManager) {

        String registrationId = "keycloak";

        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth.setDefaultClientRegistrationId(registrationId);

        return WebClient.builder()
                .baseUrl(userManagementApiUrl)
                .apply(oauth.oauth2Configuration())
                .filter(logRequest())
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
