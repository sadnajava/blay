package workshop;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import workshop.dal.SqueakerData;
import workshop.dal.datamodel.SqueakData;
import workshop.dal.datamodel.SqueakInfo;
import workshop.rest.datamodel.FindUserOutput;

public interface IBusinessLogic {
	SessionId login(String email, String password);
	Set<SqueakInfo> updateFeed(SessionId sid);
	boolean follow(SessionId sid, String email);
	boolean unfollow(SessionId sid, String email);
	boolean recordSqueak(SessionId sid, SqueakInfo info, SqueakData data);
	SqueakData getSqueak(SessionId sid, String squeakId);
	SqueakerData getSqueaker(SessionId sid, String email);
	boolean deleteSqueak(SessionId sessionId, String sqeuakId);
	boolean updateUserName(SessionId sessionId, String newName);
	Collection<FindUserOutput> findUsers(SessionId sessionId, String searchValue);
	
}
