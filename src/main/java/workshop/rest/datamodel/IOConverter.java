package workshop.rest.datamodel;

import java.util.HashSet;
import java.util.Set;

import workshop.SessionId;
import workshop.dal.datamodel.SqueakData;
import workshop.dal.datamodel.SqueakInfo;

public class IOConverter {

	public static SessionId convert(SessionIdInput sessionId) {
		return new SessionId(sessionId.getSessionId());
	}
	public static Set<SqueakInfoOutput> convert(Set<SqueakInfo> squeaks) {
		Set<SqueakInfoOutput> retSet = new HashSet<>();
		for (SqueakInfo squeak : squeaks){
			retSet.add(new SqueakInfoOutput(squeak.getPK(), squeak.getEmail(), squeak.getDuration(), squeak.getDate()));
		}
		return retSet;
	}
	
	public static String convert(SqueakData squeak) {
		return squeak.getData();
	}
}
