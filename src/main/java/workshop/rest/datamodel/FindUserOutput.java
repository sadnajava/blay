package workshop.rest.datamodel;

public class FindUserOutput {
	String email;
	String displayName;
	Integer squeaksCount;

	
	public FindUserOutput() {
		super();
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

	public Integer getSqueaksCount() {
		return squeaksCount;
	}

	public void setSqueaksCount(Integer squeaksCount) {
		this.squeaksCount = squeaksCount;
	}

	public FindUserOutput(String email, String displayName, Integer squeaksCount) {
		super();
		this.email = email;
		this.displayName = displayName;
		this.squeaksCount = squeaksCount;
	}

}
