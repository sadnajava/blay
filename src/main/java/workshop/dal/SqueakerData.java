package workshop.dal;

import java.util.Set;

import workshop.dal.datamodel.SqueakInfo;
import workshop.rest.datamodel.FindUserOutput;
import workshop.rest.datamodel.SqueakInfoOutput;

public class SqueakerData {
	String email;
	String displayName;
	boolean isFollowing;
	Set<SqueakInfoOutput> squeaks;
	Set<FindUserOutput> follows;
	
	
	public SqueakerData(String email, String displayName, boolean isFollowing,
			Set<SqueakInfoOutput> squeaks, Set<FindUserOutput> follows) {
		super();
		this.email = email;
		this.displayName = displayName;
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Set<SqueakInfoOutput> getSqueaks() {
		return squeaks;
	}

	public void setSqueaks(Set<SqueakInfoOutput> squeaks) {
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
