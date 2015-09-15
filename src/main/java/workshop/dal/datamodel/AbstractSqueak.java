package workshop.dal.datamodel;

import java.util.UUID;

public abstract class AbstractSqueak implements IPrimaryKey {
	UUID squeakId;

	public AbstractSqueak() {
		squeakId = UUID.randomUUID();
	}
	@Override
	public String getPK() {
		return squeakId.toString();
	}
	
	public UUID getSqueakId() {
		return squeakId;
	}
	public void setSqueakId(UUID squeakId) {
		this.squeakId = squeakId;
	}
	
	
}
