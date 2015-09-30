package workshop.rest.datamodel;

public class SqueakInfoOutput {
	String squeakId;
	String email;
	String displayName;
	int duration;
	String date;
	String caption;

	
	
	public SqueakInfoOutput() {
		super();
	}


	public SqueakInfoOutput(String squeakId, String email, String displayName, int duration,
			String date, String caption) {
		super();
		this.squeakId = squeakId;
		this.email = email;
		this.displayName = displayName;
		this.duration = duration;
		this.date = date;
		this.caption = caption;
	}


	public String getSqueakId() {
		return squeakId;
	}


	public void setSqueakId(String squeakId) {
		this.squeakId = squeakId;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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


	public String getCaption() {
		return caption;
	}


	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	
}
