package workshop;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import workshop.cassandra.CassandraFactory;
import workshop.dal.ISqueakDataDao;
import workshop.dal.ISqueakInfoDao;
import workshop.dal.ISubscriberDao;
import workshop.dal.SqueakDataDao;
import workshop.dal.SqueakInfoDao;
import workshop.dal.SqueakerData;
import workshop.dal.SubscribersDao;
import workshop.dal.datamodel.SqueakData;
import workshop.dal.datamodel.SqueakInfo;
import workshop.dal.datamodel.Subscriber;

public class BusinessLogic implements IBusinessLogic {
	Map<SessionId, Subscriber> sessions;
	ISubscriberDao subscriberDao;
	ISqueakInfoDao squeakInfoDao;
	ISqueakDataDao squeakDataDao;
	private Set<UUID> squeaksIds;

	private static BusinessLogic instance = null;
	
	private BusinessLogic() {
		sessions = new ConcurrentHashMap<>();
		subscriberDao = new SubscribersDao();
		squeakInfoDao = new SqueakInfoDao();
		squeakDataDao = new SqueakDataDao();
	}

	// Lazy Initialization (If required then only)
	public static BusinessLogic getInstance() {
		if (instance == null) {
			// Thread Safe
			synchronized (BusinessLogic.class) {
				if (instance == null) {
					instance = new BusinessLogic();
				}
			}
		}
		return instance;
	}

	public SessionId login(String email, String password) {
		Subscriber subscriber = subscriberDao.getSubscriber(email);
		if (subscriber == null) {
			// create
			Date currentDate = Calendar.getInstance().getTime();

			Subscriber newSub = new Subscriber(email, email, password,
					currentDate);

			subscriberDao.putSubscriber(newSub);
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
			Subscriber follow = subscriberDao.getSubscriber(followName);
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
		subscriberDao.putSubscriber(theUser);
		return true;
	}

	@Override
	public boolean unfollow(SessionId sid, String email) {
		Subscriber theUser = sessions.get(sid);
		if (theUser == null) {
			return false;
		}
		theUser.removeFromFollowingList(email);
		subscriberDao.putSubscriber(theUser);
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
		subscriberDao.putSubscriber(theUser);
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

	@Override
	public SqueakerData getSqueaker(SessionId sid, String email) {
		Subscriber theUser = sessions.get(sid);
		if (theUser == null) {
			return null;
		}
		Subscriber subscriber = subscriberDao.getSubscriber(email);
		
		if (subscriber == null){
			return null;
		}
		
		Set<SqueakInfo> retSqueaks = new HashSet<>();
		squeaksIds = subscriber.getSqueaks();
		for (UUID squeakId : squeaksIds){
			SqueakInfo squeakInfo = squeakInfoDao.getSqueak(squeakId);
			if (squeakInfo != null){
				retSqueaks.add(squeakInfo);
			}
		}
		return new SqueakerData(theUser.getEmail(),email,retSqueaks, theUser.isFollowing(email));
	}
}