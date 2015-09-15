package workshop;

import java.util.UUID;

public class SessionId {
	UUID uuid;

	public SessionId() {
		uuid = UUID.randomUUID();
	}
	
	public SessionId(String sessionid) {
		this.uuid = UUID.fromString(sessionid);
	}

	public String getSessionId(){
		return uuid.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SessionId other = (SessionId) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	
}
