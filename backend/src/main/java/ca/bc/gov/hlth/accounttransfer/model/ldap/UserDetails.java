package ca.bc.gov.hlth.accounttransfer.model.ldap;

public class UserDetails {
	private String org;

	private String uid;

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String id) {
		this.uid = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		result = prime * result + ((org == null) ? 0 : org.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDetails other = (UserDetails) obj;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		if (org == null) {
			if (other.org != null)
				return false;
		} else if (!org.equals(other.org))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "oldLdapId [o=" + org + ", uid=" + uid + "]";
	}

}
