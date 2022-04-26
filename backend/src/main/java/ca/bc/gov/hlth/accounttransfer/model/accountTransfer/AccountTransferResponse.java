package ca.bc.gov.hlth.accounttransfer.model.accountTransfer;

public class AccountTransferResponse {

    private StatusEnum status;
    private String message;
    private String roleAdded;

    public AccountTransferResponse(StatusEnum status, String message, String roleAdded) {
        this.status = status;
        this.message = message;
        this.roleAdded = roleAdded;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRoleAdded() {
        return roleAdded;
    }

    public void setRoleAdded(String roleAdded) {
        this.roleAdded = roleAdded;
    }
}
