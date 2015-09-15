package workshop.dal.datamodel;

public class SqueakData extends AbstractSqueak {
	String data;

	public SqueakData(String data) {
		super();
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	
}
