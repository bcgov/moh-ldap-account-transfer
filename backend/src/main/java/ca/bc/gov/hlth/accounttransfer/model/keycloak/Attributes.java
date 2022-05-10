package ca.bc.gov.hlth.accounttransfer.model.keycloak;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Attributes {

	private List<String> phone;

	@JsonProperty("org_details")
	private List<String> orgDetails = new ArrayList<>();

	@JsonProperty("access_team_notes")
	private List<String> accessTeamNotes = new ArrayList<>();

	public List<String> getPhone() {
		return phone;
	}

	public void setPhone(List<String> phone) {
		this.phone = phone;
	}

	public List<String> getOrgDetails() {
		return orgDetails;
	}

	public void setOrgDetails(List<String> orgDetails) {
		this.orgDetails = orgDetails;
	}

	public List<String> getAccessTeamNotes() {
		return accessTeamNotes;
	}

	public void setAccessTeamNotes(List<String> accessTeamNotes) {
		this.accessTeamNotes = accessTeamNotes;
	}

	@Override
	public String toString() {
		return "Attributes [phone=" + phone + ", orgDetails=" + orgDetails + ", accessTeamNotes=" + accessTeamNotes + "]";
	}

}
