package workshop.dal;

import java.util.Set;

import workshop.dal.datamodel.SqueakInfo;
import workshop.rest.datamodel.FindUserOutput;

public class SqueakerData {
	String email;
	boolean isFollowing;
	Set<SqueakInfo> squeaks;
	Set<FindUserOutput> follows;
	
	
	public SqueakerData(String email, boolean isFollowing,
			Set<SqueakInfo> squeaks, Set<FindUserOutput> follows) {
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

	public Set<FindUserOutput> getFollows() {
		return follows;
	}

	public void setFollows(Set<FindUserOutput> follows) {
		this.follows = follows;
	}
	
	
}
