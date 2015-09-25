package workshop.rest.datamodel;

public class RecordSqueakInput extends SessionIdInput {
	int duration;
	String date;
	String caption;
	String data;

	
	public RecordSqueakInput() {
		super();
	}

	public RecordSqueakInput(String sid) {
		super(sid);
	}

	public RecordSqueakInput(String sid, int duration,
			String date, String caption, String data) {
		super(sid);
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
