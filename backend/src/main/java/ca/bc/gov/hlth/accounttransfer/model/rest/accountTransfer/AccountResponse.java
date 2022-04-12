package ca.bc.gov.hlth.accounttransfer.model.rest.accountTransfer;

import ca.bc.gov.hlth.accounttransfer.model.rest.BaseResponse;

public class AccountResponse extends BaseResponse {

	private String id;

	private String message;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
