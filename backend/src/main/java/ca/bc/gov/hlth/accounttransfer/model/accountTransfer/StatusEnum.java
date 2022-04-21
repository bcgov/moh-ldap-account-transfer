package ca.bc.gov.hlth.accounttransfer.model.accountTransfer;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StatusEnum {
    @JsonProperty("error") ERROR,
    @JsonProperty("success") SUCCESS,
    @JsonProperty("warning") WARNING
}
