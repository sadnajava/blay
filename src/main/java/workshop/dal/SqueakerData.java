package workshop.dal;

import java.util.Set;

import workshop.dal.datamodel.SqueakInfo;

public class SqueakerData {
	String email;
	boolean isFollowing;
	Set<SqueakInfo> squeaks;
	Set<String> follows;
	
	
	public SqueakerData(String email, boolean isFollowing,
			Set<SqueakInfo> squeaks, Set<String> follows) {
		super();
		this.email = email;
		this.isFollowing = isFollowing;
		this.squeaks = squeaks;
		this.follows = follows;
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

	public Set<String> getFollows() {
		return follows;
	}

	public void setFollows(Set<String> follows) {
		this.follows = follows;
	}
	
	
}
