package workshop.dal.datamodel;


public class SqueakInfo extends AbstractSqueak {
	String email;
	int duration;
	String date;
	String caption;
	
	public SqueakInfo(String email, int duration, String date, String caption) {
		super();
		this.email = email;
		this.duration = duration;
		this.date = date;
		this.caption = caption;
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
	
}
