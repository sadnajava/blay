package workshop.dal;

import java.io.IOException;
import java.util.UUID;

import workshop.cassandra.CassandraFactory;
import workshop.cassandra.ICassandraClient;
import workshop.dal.datamodel.SqueakData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SqueakDataDao implements ISqueakDataDao {

	private static final String tableName = "squeak_data";
	private ICassandraClient client;

	public SqueakDataDao() {
		client = CassandraFactory.connect(tableName);
	}

	@Override
	public SqueakData getSqueak(UUID squeakId) {
		String jsonedSqueakData = client.read(squeakId.toString());
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return mapper.readValue(jsonedSqueakData, SqueakData.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public void putSqueak(SqueakData squeak) {
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
