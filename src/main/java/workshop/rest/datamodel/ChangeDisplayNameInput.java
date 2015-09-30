package workshop.rest.datamodel;

public class ChangeDisplayNameInput extends SessionIdInput {
	String displayName;

	public ChangeDisplayNameInput() {}
	
	public ChangeDisplayNameInput(String sid) {
		super(sid);
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String newDisplayName) {
		this.displayName = newDisplayName;
	}

	public ChangeDisplayNameInput(String sid, String newDisplayName) {
		super(sid);
		this.displayName = newDisplayName;
	}
	
}
