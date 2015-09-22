package workshop.rest.datamodel;

public class SqueakInput extends SessionIdInput {
	String sqeuakId;

	public SqueakInput(String sid, String sqeuakId) {
		super(sid);
		this.sqeuakId = sqeuakId;
	}

	public String getSqeuakId() {
		return sqeuakId;
	}

	public void setSqeuakId(String sqeuakId) {
		this.sqeuakId = sqeuakId;
	}
	
}
