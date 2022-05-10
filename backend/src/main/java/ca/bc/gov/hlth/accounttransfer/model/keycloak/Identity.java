package ca.bc.gov.hlth.accounttransfer.model.keycloak;

public class Identity {
	private String identityProvider;
	private String userId;
	private String userName;

	public String getIdentityProvider() {
		return identityProvider;
	}

	public void setIdentityProvider(String identityProvider) {
		this.identityProvider = identityProvider;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "Identity [identityProvider=" + identityProvider + ", userId=" + userId + ", userName=" + userName + "]";
	}

}
