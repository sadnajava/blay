package workshop.dal;

import java.util.Set;

import workshop.dal.datamodel.SqueakInfo;

public class SqueakerData {
	String requesterEmail;
	String searchedEmail;
	boolean isFollowing;
	Set<SqueakInfo> squeaks;
	Set<String> follows;

	public SqueakerData(String requesterEmail, String searchedEmail,
			Set<SqueakInfo> squeaks, boolean isFollowing) {
		super();
		this.requesterEmail = requesterEmail;
		this.searchedEmail = searchedEmail;
		this.squeaks = squeaks;
		this.isFollowing = isFollowing;
	}

	public SqueakerData(String requesterEmail, String searchedEmail,
			boolean isFollowing, Set<SqueakInfo> squeaks, Set<String> follows) {
		super();
		this.requesterEmail = requesterEmail;
		this.searchedEmail = searchedEmail;
		this.isFollowing = isFollowing;
		this.squeaks = squeaks;
		this.follows = follows;
	}

	public String getSearchedEmail() {
		return searchedEmail;
	}

	public void setSearchedEmail(String searchedEmail) {
		this.searchedEmail = searchedEmail;
	}

	public String getRequesterEmail() {
		return requesterEmail;
	}

	public void setRequesterEmail(String requesterEmail) {
		this.requesterEmail = requesterEmail;
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
