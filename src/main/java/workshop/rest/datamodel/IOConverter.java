package workshop.rest.datamodel;

import workshop.SessionId;
import workshop.dal.SubscribersDao;
import workshop.dal.datamodel.SqueakData;
import workshop.dal.datamodel.SqueakInfo;
import workshop.dal.datamodel.Subscriber;

import java.util.HashSet;
import java.util.Set;

public class IOConverter {

    public static SessionId convert(SessionIdInput sessionId) {
        return new SessionId(sessionId.getSessionId());
    }

    public static Set<SqueakInfoOutput> convert(Set<SqueakInfo> squeaks) {
        SubscribersDao subscriberDao = new SubscribersDao();
        Set<SqueakInfoOutput> retSet = new HashSet<>();

        for (SqueakInfo squeak : squeaks) {
            Subscriber user = subscriberDao.getSubscriber(squeak.getEmail());
            retSet.add(new SqueakInfoOutput(squeak.getPK(), user.getEmail(), user.getDisplayName(), squeak.getDuration(), squeak.getDate(), squeak.getCaption()));
        }

        return retSet;
    }

    public static String convert(SqueakData squeak) {
        return squeak.getData();
    }
}
