package ca.bc.gov.hlth.accounttransfer.model.ldap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LdapRequest {

    @JsonProperty("userName")
    private String username;
    private String password;

    public LdapRequest() {
    }

    public LdapRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LdapRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
