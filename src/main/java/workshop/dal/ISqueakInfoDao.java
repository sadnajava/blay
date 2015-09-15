package workshop.dal;

import java.util.UUID;

import workshop.dal.datamodel.SqueakInfo;

public interface ISqueakInfoDao {
	SqueakInfo getSqueak(UUID squeakId);

	void putSqueak(SqueakInfo squeak);
}
