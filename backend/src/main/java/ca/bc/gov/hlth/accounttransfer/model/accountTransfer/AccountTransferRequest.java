package ca.bc.gov.hlth.accounttransfer.model.accountTransfer;

public class AccountTransferRequest {

	private String username;
	private String password;
	private String application;

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

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}
}
