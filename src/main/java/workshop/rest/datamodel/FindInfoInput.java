package workshop.rest.datamodel;

public class FindInfoInput extends SessionIdInput{
	String searchValue;

	public FindInfoInput(String sid, String searchValue) {
		super(sid);
		this.searchValue = searchValue;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	
}
