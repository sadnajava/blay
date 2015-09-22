package workshop.dal;

import java.util.Collection;

import workshop.dal.datamodel.Subscriber;

public interface ISubscriberDao {
	Subscriber getSubscriber(String email);
	Collection<Subscriber> getAllSubscriber();
	void putSubscriber(Subscriber sub);
}
