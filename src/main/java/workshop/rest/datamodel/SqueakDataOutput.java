package workshop.rest.datamodel;

public class SqueakDataOutput extends SqueakInfoOutput {
	String data;

	public SqueakDataOutput(String squeakId, String email, int duration,
			String date, String data) {
		super(squeakId, email, duration, date);
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
