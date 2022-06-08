package ca.bc.gov.hlth.accounttransfer;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import ca.bc.gov.hlth.accounttransfer.service.KeycloakUserManagementService;
import ca.bc.gov.hlth.accounttransfer.util.keycloak.ClientsLookup;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
class AccountTransferApiApplicationTests {

	protected static MockWebServer mockBackEnd;

	/**
	 * Override the WebClient userManagementWebClient for tests as even with a mocked server the oauth still gets called
	 * and so fails as actual credentials cannot be provided in test. This builds a similar base WebClient for userManagementWebClient
	 * but without applying the oauth. Logging has also been omitted.
	 * The URL used by the endpoint is set using the MockWebServer port as this port is created dynamically on start up to ensure it uses an available 
	 * port so it is not known before then. 
	 *
	 */
	@TestConfiguration
    public static class WebClientConfiguration {
		
		@Bean("userManagementWebClient")
	    public WebClient userManagementWebClient() {
	        return WebClient.builder()
	                .baseUrl(String.format("http://localhost:%s", mockBackEnd.getPort()))
	                .build();
	    }
	}
	
    /**
     *  Set up a MockServer for handling WebClient calls. A mock response is also added here as it is required each time the application starts. This is because it is consumed by 
     *  the {@link KeycloakUserManagementService#getClients getClients} method called by {@link ClientsLookup#lookupClientIds() lookupClientIds} method which is labeled with '@PostConstruct'. 
     *  So the response must be added here in the static 'BeforeAll' method which is invoked before the {@link ClientsLookup#lookupClientIds() lookupClientIds}.
     */
	@BeforeAll
    static void setUp() throws IOException {		
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(0);

        mockBackEnd.enqueue(new MockResponse()
        		.setBody("[]")
        	    .addHeader(CONTENT_TYPE, MediaType.ALL.toString()));
	}
	
    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

	@Test
	void contextLoads() {
	}

}
