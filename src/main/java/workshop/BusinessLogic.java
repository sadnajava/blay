package workshop;

import org.apache.commons.lang3.StringUtils;
import workshop.dal.*;
import workshop.dal.datamodel.SqueakData;
import workshop.dal.datamodel.SqueakInfo;
import workshop.dal.datamodel.Subscriber;
import workshop.rest.datamodel.FindUserOutput;
import workshop.rest.datamodel.IOConverter;
import workshop.rest.datamodel.SqueakInfoOutput;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BusinessLogic implements IBusinessLogic {
	Map<SessionId, Subscriber> sessions;
	ISubscriberDao subscriberDao;
	ISqueakInfoDao squeakInfoDao;
	ISqueakDataDao squeakDataDao;

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
		SessionId sid = null;

		Subscriber subscriber = subscriberDao.getSubscriber(email);

		if (subscriber == null) {
			// create
			Subscriber newSub = new Subscriber(email, email, password, Calendar
					.getInstance().getTime());

			subscriberDao.putSubscriber(newSub);
			sid = addToSessionCache(newSub);
		} else if (subscriber.getPassword().equals(password)) {
			sid = addToSessionCache(subscriber);
		}

		return sid;
	}

	private SessionId addToSessionCache(Subscriber newSub) {
		SessionId sid = new SessionId();

		sessions.putIfAbsent(sid, newSub);
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

		info.setEmail(theUser.getEmail());
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

		if (theUser.getSqueaks().contains(properSqueakId)) {
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

		if (subscriber == null) {
			return null;
		}

		Set<SqueakInfo> squeaks = new HashSet<>();
		Set<UUID> squeaksIds = subscriber.getSqueaks();

		for (UUID squeakId : squeaksIds) {
			SqueakInfo squeakInfo = squeakInfoDao.getSqueak(squeakId);

			if (squeakInfo != null) {
				squeaks.add(squeakInfo);
			}
		}

		Set<SqueakInfoOutput> retSqueaks = IOConverter.convert(squeaks);

		Set<String> following = subscriber.getFollowing();
		Set<FindUserOutput> followingOutput = new HashSet<>();

		for (String user : following) {
			Subscriber subscriberFollowing = subscriberDao.getSubscriber(user);
			followingOutput.add(new FindUserOutput(subscriberFollowing.getEmail(), subscriberFollowing.getDisplayName(), subscriberFollowing.getNumberOfSqueaks()));
		}

		return new SqueakerData(email, subscriber.getDisplayName(), theUser.isFollowing(email), retSqueaks,
				followingOutput);
	}

	@Override
	public boolean deleteSqueak(SessionId sessionId, String squeakId) {
		Subscriber theUser = sessions.get(sessionId);

		if (theUser == null) {
			return false;
		}

		UUID properSqueakId = UUID.fromString(squeakId);

		if (theUser.removeSqueak(properSqueakId)) {
			squeakInfoDao.removeSqueak(properSqueakId);
			squeakDataDao.removeSqueak(properSqueakId);
			subscriberDao.putSubscriber(theUser);
			return true;
		}

		return false;
	}

	@Override
	public boolean updateUserName(SessionId sessionId, String newName) {
		Subscriber theUser = sessions.get(sessionId);

		if (theUser == null) {
			return false;
		}

		theUser.setDisplayName(newName);
		subscriberDao.putSubscriber(theUser);

		return true;
	}

	@Override
	public Collection<FindUserOutput> findUsers(SessionId sessionId, String searchValue) {
		Collection<FindUserOutput> users = new HashSet<>();
		Subscriber theUser = sessions.get(sessionId);

		if (theUser == null) {
			return users;
		}

		Collection<Subscriber> allSubscriber = subscriberDao.getAllSubscriber();

		for (Subscriber sub : allSubscriber) {
			if (StringUtils.containsIgnoreCase(sub.getDisplayName(), searchValue) || StringUtils.containsIgnoreCase(sub.getEmail(), searchValue)) {
				users.add(new FindUserOutput(sub.getEmail(), sub.getDisplayName(), sub.getNumberOfSqueaks()));
			}
		}

		return users;
	}
}