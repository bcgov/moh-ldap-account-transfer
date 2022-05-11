package ca.bc.gov.hlth.accounttransfer.exception;

public class AccountTransferException extends Exception {

	private static final long serialVersionUID = 1L;

	public AccountTransferException() {
		super();
	}

	public AccountTransferException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccountTransferException(String message) {
		super(message);
	}

	public AccountTransferException(Throwable cause) {
		super(cause);
	}


}
