package workshop.rest.datamodel;

public class RecordSqueakInput extends SessionIdInput {
	String email;
	int duration;
	String date;
	String caption;
	String data;

	public RecordSqueakInput(String sid, String email, int duration,
			String date, String caption, String data) {
		super(sid);
		this.email = email;
		this.duration = duration;
		this.date = date;
		this.caption = caption;
		this.data = data;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
