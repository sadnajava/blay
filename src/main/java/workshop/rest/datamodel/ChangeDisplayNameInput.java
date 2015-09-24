package workshop.rest.datamodel;

public class ChangeDisplayNameInput extends SessionIdInput {
	String displayName;

	
	public ChangeDisplayNameInput(String sid) {
		super(sid);
	}

	public String getNewDisplayName() {
		return displayName;
	}

	public void setNewDisplayName(String newDisplayName) {
		this.displayName = newDisplayName;
	}

	public ChangeDisplayNameInput(String sid, String newDisplayName) {
		super(sid);
		this.displayName = newDisplayName;
	}
	
}
