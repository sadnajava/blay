package workshop.rest.datamodel;

public class SqueakInput extends SessionIdInput {
	String squeakId;

	
	public SqueakInput() {
		super();
	}

	public SqueakInput(String sid, String squeakId) {
		super(sid);
		this.squeakId = squeakId;
	}

	public String getSqueakId() {
		return squeakId;
	}

	public void setSqueakId(String squeakId) {
		this.squeakId = squeakId;
	}
	
}
