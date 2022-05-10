package ca.bc.gov.hlth.accounttransfer.model.keycloak;

import java.util.ArrayList;
import java.util.List;

public class User {

	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private Attributes attributes = new Attributes();
	private List<Identity> federatedIdentities = new ArrayList<>();

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public List<Identity> getFederatedIdentities() {
		return federatedIdentities;
	}

	public void setFederatedIdentities(List<Identity> federatedIdentities) {
		this.federatedIdentities = federatedIdentities;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", attributes="
				+ attributes + ", federatedIdentities=" + federatedIdentities + "]";
	}

}
