package workshop;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import workshop.dal.ISqueakDataDao;
import workshop.dal.ISqueakInfoDao;
import workshop.dal.ISubscriberDao;
import workshop.dal.SubscribersDao;
import workshop.dal.datamodel.SqueakData;
import workshop.dal.datamodel.SqueakInfo;
import workshop.dal.datamodel.Subscriber;

public class ServerLogic implements IBusinessLogic {
	Map<SessionId, Subscriber> sessions;
	ISubscriberDao sd;
	ISqueakInfoDao squeakInfoDao;
	ISqueakDataDao squeakDataDao;

	public ServerLogic() {
		sessions = new ConcurrentHashMap<>();
	}

	public SessionId login(String email, String password) {
		Subscriber subscriber = sd.getSubscriber(email);
		if (subscriber == null) {
			// create
			Date currentDate = Calendar.getInstance().getTime();

			Subscriber newSub = new Subscriber(email, email, password,
					currentDate);

			sd.putSubscriber(newSub);
			SessionId sid = addToSessionCache(newSub);
			return sid;
		} else if (subscriber.getPassword().equals(password)) {

			SessionId sid = addToSessionCache(subscriber);
			return sid;
		} else {
			return null;
		}
	}

	private SessionId addToSessionCache(Subscriber newSub) {
		SessionId sid = new SessionId();
		sessions.put(sid, newSub);
		return sid;
	}

	@Override
	public Set<SqueakInfo> updateFeed(SessionId sid) {
		Subscriber theUser = sessions.get(sid);
		if (theUser == null) {
			return new HashSet<>();
		}
		Set<SqueakInfo> returnedSqueaks = new HashSet<>();
		for (String followName : theUser.getFollowing()) {
			Subscriber follow = sd.getSubscriber(followName);
			if (follow == null) {
				continue;
			}
			Set<UUID> squeaks = follow.getSqueaks();
			if (!squeaks.isEmpty()) {
				// Get one squeak per follow
				SqueakInfo squeak = squeakInfoDao.getSqueak((UUID) squeaks
						.toArray()[0]);
				if (squeak != null) {
					returnedSqueaks.add(squeak);
				}
			}
		}
		return returnedSqueaks;
	}

	@Override
	public boolean follow(SessionId sid, String email) {
		Subscriber theUser = sessions.get(sid);
		if (theUser == null) {
			return false;
		}
		theUser.addFromFollowingList(email);
		sd.putSubscriber(theUser);
		return true;
	}

	@Override
	public boolean unfollow(SessionId sid, String email) {
		Subscriber theUser = sessions.get(sid);
		if (theUser == null) {
			return false;
		}
		theUser.removeFromFollowingList(email);
		sd.putSubscriber(theUser);
		return true;
	}

	@Override
	public boolean recordSqueak(SessionId sid, SqueakInfo info, SqueakData data) {
		Subscriber theUser = sessions.get(sid);
		if (theUser == null) {
			return false;
		}
		data.setSqueakId(info.getSqueakId());
		squeakInfoDao.putSqueak(info);
		squeakDataDao.putSqueak(data);
		theUser.addSquak(info.getSqueakId());
		sd.putSubscriber(theUser);
		return true;
	}

	@Override
	public SqueakData getSqueak(SessionId sid, String squeakId) {
		Subscriber theUser = sessions.get(sid);
		if (theUser == null) {
			return null;
		}
		
		UUID properSqueakId = UUID.fromString(squeakId);
		if (theUser.getSqueaks().contains(properSqueakId)){
			return squeakDataDao.getSqueak(properSqueakId);
		}
		return null;
	}
}