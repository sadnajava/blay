package workshop.rest.datamodel;

public class SqueakDataOutput extends SqueakInfoOutput {
	String data;

	
	public SqueakDataOutput() {
		super();
	}

	public SqueakDataOutput(String squeakId, String email, String displayName, int duration,
			String date, String caption) {
		super(squeakId, email, displayName, duration, date, caption);
	}

	public SqueakDataOutput(String squeakId, String email, String displayName, int duration,
			String date, String caption, String data) {
		super(squeakId, email, displayName, duration, date, caption);
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
