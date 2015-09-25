package workshop.rest.datamodel;

public class followSqueakerInput extends SessionIdInput {
	String email;

	public followSqueakerInput() {}
	
	public followSqueakerInput(String sid) {
		super(sid);
	}

	public followSqueakerInput(String sid, String email) {
		super(sid);
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
