package workshop.rest.datamodel;

public class SqueakInfoOutput {
	String squeakId;
	String email;
	int duration;
	String date;

	public SqueakInfoOutput(String squeakId, String email, int duration,
			String date) {
		this.squeakId = squeakId;
		this.email = email;
		this.duration = duration;
		this.date = date;
	}

	public String getSqueakId() {
		return squeakId;
	}

	public String getEmail() {
		return email;
	}

	public int getDuration() {
		return duration;
	}

	public String getDate() {
		return date;
	}
}
