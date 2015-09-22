package workshop.dal;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import workshop.cassandra.CassandraFactory;
import workshop.cassandra.ICassandraClient;
import workshop.dal.datamodel.Subscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SubscribersDao implements ISubscriberDao {
	private static final String tableName = "subscribers";
	private ICassandraClient client;
	
	public SubscribersDao(){
		client = CassandraFactory.connect(tableName);
	}
	@Override
	public Subscriber getSubscriber(String email) {
		String jsonedSubscriber = client.read(email);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return mapper.readValue(jsonedSubscriber, Subscriber.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void putSubscriber(Subscriber sub) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			client.write(sub.getPK(),mapper.writeValueAsString(sub));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	@Override
	public Collection<Subscriber> getAllSubscriber() {
		Collection<Subscriber> allSubscribers = new HashSet<>();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		Collection<String> alljsonedUsers = client.readAll();
		for (String jsonedUser : alljsonedUsers){
			try {
				allSubscribers.add(mapper.readValue(jsonedUser, Subscriber.class));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return allSubscribers;
	}

}
