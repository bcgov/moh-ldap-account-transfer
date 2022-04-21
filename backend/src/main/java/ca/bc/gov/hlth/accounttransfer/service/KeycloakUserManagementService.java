package ca.bc.gov.hlth.accounttransfer.service;

import ca.bc.gov.hlth.accounttransfer.applications.ClientsLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class KeycloakUserManagementService {

    private static final String clientsPath = "/clients";
    private static final String usersPath = "/users";
    private static final String userClientRoleMappingPath = "/role-mappings/clients";

    @Autowired
    private WebClient userManagementWebClient;

    public Object[] getClients() {
        return userManagementWebClient
                .get()
                .uri(uri -> uri.path(clientsPath).build())
                .retrieve()
                .bodyToMono(Object[].class)
                .block();
    }

    public Object[] getClientRoles(String idOfClient) {
        String path = String.format("%s/%s/roles", clientsPath, idOfClient);
        return userManagementWebClient
                .get()
                .uri(uri -> uri.path(path).build())
                .retrieve()
                .bodyToMono(Object[].class)
                .block();
    }

    public ResponseEntity<String> addRoleToUser(String userId, String idOfClient, Object[] rolesToSend) {

        String path = String.format("%s/%s/%s/%s", usersPath, userId, userClientRoleMappingPath, idOfClient);

        return userManagementWebClient
                .post()
                .uri(uri -> uri.path(path).build())
                .bodyValue(rolesToSend)
                .retrieve()
                .toEntity(String.class)
                .block();
    }
}
