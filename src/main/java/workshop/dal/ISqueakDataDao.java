package workshop.dal;

import java.util.UUID;

import workshop.dal.datamodel.SqueakData;

public interface ISqueakDataDao {
	SqueakData getSqueak(UUID squeakId);

	void putSqueak(SqueakData squeak);
}
