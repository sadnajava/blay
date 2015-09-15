package workshop.dal;

import workshop.dal.datamodel.Subscriber;

public interface ISubscriberDao {
	Subscriber getSubscriber(String email);
	void putSubscriber(Subscriber sub);
}
