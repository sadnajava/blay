package workshop.rest.datamodel;

public class FindUserOutput {
	String email;
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

	public Integer getSqueaksCount() {
		return squeaksCount;
	}

	public void setSqueaksCount(Integer squeaksCount) {
		this.squeaksCount = squeaksCount;
	}

	public FindUserOutput(String email, Integer squeaksCount) {
		super();
		this.email = email;
		this.squeaksCount = squeaksCount;
	}

}
