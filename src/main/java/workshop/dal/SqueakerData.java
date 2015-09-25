package workshop.dal;

import java.util.Set;

import workshop.dal.datamodel.SqueakInfo;

public class SqueakerData {
	String email;
	boolean isFollowing;
	Set<SqueakInfo> squeaks;

	
	public SqueakerData(String requesterEmail, String email,
			Set<SqueakInfo> squeaks, boolean isFollowing) {
		super();
		this.email = email;
		this.squeaks = squeaks;
		this.isFollowing = isFollowing;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<SqueakInfo> getSqueaks() {
		return squeaks;
	}

	public void setSqueaks(Set<SqueakInfo> squeaks) {
		this.squeaks = squeaks;
	}

	public boolean isFollowing() {
		return isFollowing;
	}

	public void setFollowing(boolean isFollowing) {
		this.isFollowing = isFollowing;
	}

}
