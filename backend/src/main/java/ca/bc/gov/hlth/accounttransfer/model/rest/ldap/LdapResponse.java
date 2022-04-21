package ca.bc.gov.hlth.accounttransfer.model.rest.ldap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LdapResponse {

    private boolean authenticated;
    private boolean unlocked;

    @JsonProperty("userName")
    private String username;

    @JsonProperty("hlthregbusinessusergroup")
    private String mspDirectRole;

    @JsonProperty("org_details")
    private Object orgDetails;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean getUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public String getMspDirectRole() {
        return mspDirectRole;
    }

    public void setMspDirectRole(String mspDirectRole) {
        this.mspDirectRole = mspDirectRole;
    }

    public Object getOrgDetails() {
        return orgDetails;
    }

    public void setOrgDetails(Object orgDetails) {
        this.orgDetails = orgDetails;
    }

    @Override
    public String toString() {
        return "LdapResponse{" +
                "username='" + username + '\'' +
                ", authenticated='" + authenticated + '\'' +
                ", unlocked='" + unlocked + '\'' +
                ", hlthregbusinessusergroup='" + mspDirectRole + '\'' +
                ", orgDetails='" + orgDetails + '\'' +
                '}';
    }
}
