package workshop.dal;

import java.io.IOException;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import workshop.cassandra.CassandraFactory;
import workshop.cassandra.ICassandraClient;
import workshop.dal.datamodel.SqueakInfo;
import workshop.dal.datamodel.Subscriber;

public class SqueakInfoDao implements ISqueakInfoDao {

	private static final String tableName = "squeak_info";
	private ICassandraClient client;

	public SqueakInfoDao() {
		client = CassandraFactory.connect(tableName);
	}

	@Override
	public SqueakInfo getSqueak(UUID squeakId) {
		String jsonedSqueakInfo = client.read(squeakId.toString());
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return mapper.readValue(jsonedSqueakInfo, SqueakInfo.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public void putSqueak(SqueakInfo squeak) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			client.write(squeak.getPK(), mapper.writeValueAsString(squeak));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeSqueak(UUID squeakId) {
		client.remove(squeakId.toString());
	}

}
