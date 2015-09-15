package workshop.rest.datamodel;

public class SessionIdInput {
	String sessionId;

	public SessionIdInput(String sid) {
		super();
		this.sessionId = sid;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sid) {
		this.sessionId = sid;
	}

	public SessionIdInput() {
		super();
	}
	
	
}
