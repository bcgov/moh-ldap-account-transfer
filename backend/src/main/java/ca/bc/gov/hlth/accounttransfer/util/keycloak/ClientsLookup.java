package ca.bc.gov.hlth.accounttransfer.util.keycloak;

import ca.bc.gov.hlth.accounttransfer.service.KeycloakUserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Lookup and store id's for the clients and roles that this application has access to manage
 */
@Component
public class ClientsLookup {

    private static final Logger logger = LoggerFactory.getLogger(ClientsLookup.class);

    @Autowired
    private KeycloakUserManagementService keycloakUserManagementService;

    private HashMap<String, KeycloakClient> clients = new HashMap<>();

    @PostConstruct
    private void lookupClientIds() {

        Object[] clientsWithPermissionToView = keycloakUserManagementService.getClients();

        // ClientId from Keycloak is the name of the client
        Arrays.stream(clientsWithPermissionToView).forEach(c -> clients.put(((HashMap) c).get("clientId").toString(), new KeycloakClient(((HashMap) c).get("id").toString())));

        for (KeycloakClient client : clients.values()) {
            Object[] clientRoles = keycloakUserManagementService.getClientRoles(client.getId());
            Arrays.stream(clientRoles).forEach(role -> client.getRoles().put(((HashMap) role).get("name").toString(), role));
        }

        logger.info("Application starting up with permissions for the following clients: {}", clients.keySet());

    }

    public HashMap<String, KeycloakClient> getClients() {
        return clients;
    }

    public void setClients(HashMap<String, KeycloakClient> clients) {
        this.clients = clients;
    }
}
