package workshop.rest.datamodel;

public class SessionIdInput {
	String sid;

	public SessionIdInput(String sid) {
		super();
		this.sid = sid;
	}

	public String getSessionId() {
		return sid;
	}

	public void setSessionId(String sid) {
		this.sid = sid;
	}

	public SessionIdInput() {
		super();
	}
	
	
}
