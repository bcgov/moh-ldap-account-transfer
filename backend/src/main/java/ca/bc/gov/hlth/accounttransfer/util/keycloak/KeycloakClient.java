package ca.bc.gov.hlth.accounttransfer.util.keycloak;

import java.util.HashMap;

public class KeycloakClient {

    private String id;
    // role name and role details
    private HashMap<String, Object> roles = new HashMap<>();

    public KeycloakClient(String id) {
        this.id = id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, Object> getRoles() {
        return roles;
    }

    public void setRoles(HashMap<String, Object> roles) {
        this.roles = roles;
    }
}
