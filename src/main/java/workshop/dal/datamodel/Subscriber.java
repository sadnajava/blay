package workshop.dal.datamodel;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Subscriber implements IPrimaryKey {
	String email;
	String userName;
	String password;
	Date registrationDate;
	Set<String> following;
	Set<UUID> Squeaks;
	

	public Set<UUID> getSqueaks() {
		return Squeaks;
	}

	public void setSqueaks(Set<UUID> squeaks) {
		Squeaks = squeaks;
	}

	public Subscriber() {

	}

	public void setFollowing(Set<String> following) {
		this.following = following;
	}

	
	public Subscriber(String email, String userName, String password,
			Date registrationDate) {
		super();
		this.email = email;
		this.userName = userName;
		this.password = password;
		this.registrationDate = registrationDate;
		this.following = new HashSet<>();
	}

	public Subscriber(String email, String userName, String password,
			Date registrationDate, Set<String> following) {
		super();
		this.email = email;
		this.userName = userName;
		this.password = password;
		this.registrationDate = registrationDate;
		this.following = following;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result
				+ ((following == null) ? 0 : following.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime
				* result
				+ ((registrationDate == null) ? 0 : registrationDate.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
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
		Subscriber other = (Subscriber) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (following == null) {
			if (other.following != null)
				return false;
		} else if (!following.equals(other.following))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (registrationDate == null) {
			if (other.registrationDate != null)
				return false;
		} else if (!registrationDate.equals(other.registrationDate))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Subscriber [email=" + email + ", userName=" + userName
				+ ", password=" + password + ", registrationDate="
				+ registrationDate + ", following=" + following + "]";
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String getPK() {
		return email;
	}

	public Set<String> getFollowing() {
		return following;
 	}
	
	public boolean removeFromFollowingList(String email){
		return following.remove(email);
	}
	public void addFromFollowingList(String email){
		following.add(email);
	}
	
	public void addSquak(UUID squeakId){
		Squeaks.add(squeakId);
	}
}
