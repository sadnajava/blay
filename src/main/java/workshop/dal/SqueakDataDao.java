package workshop.dal;

import java.io.IOException;
import java.util.UUID;

import workshop.cassandra.CassandraFactory;
import workshop.cassandra.ICassandraClient;
import workshop.dal.datamodel.SqueakData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SqueakDataDao implements ISqueakDataDao {

	private static final String tableName = "squeak_info";
	private ICassandraClient client;

	public SqueakDataDao() {
		client = CassandraFactory.connect(tableName);
	}

	@Override
	public SqueakData getSqueak(UUID squeakId) {
		String jsonedSqueakData = client.read(squeakId.toString());
		ObjectMapper mapper = new ObjectMapper();
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
		try {
			client.write(squeak.getPK(), mapper.writeValueAsString(squeak));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}
