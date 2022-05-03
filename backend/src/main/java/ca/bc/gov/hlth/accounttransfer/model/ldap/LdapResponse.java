package ca.bc.gov.hlth.accounttransfer.model.ldap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LdapResponse {

	private boolean authenticated;
	private boolean unlocked;

	private String userName;

	@JsonProperty("hlthregbusinessusergroup")
	private String mspDirectRole;

	@JsonProperty("org_details")
	private Object orgDetails;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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
		return "LdapResponse{" + "username='" + userName + '\'' + ", authenticated='" + authenticated + '\'' + ", unlocked='" + unlocked
				+ '\'' + ", hlthregbusinessusergroup='" + mspDirectRole + '\'' + ", orgDetails='" + orgDetails + '\'' + '}';
	}
}
