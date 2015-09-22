package workshop.cassandra;

import java.util.Iterator;
import java.util.Properties;

import com.google.common.collect.ImmutableMap;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.ddl.SchemaChangeResult;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.serializers.StringSerializer;

public class CassandraClient implements ICassandraClient {

	private ColumnFamily<String, String> cfInfo;
	private Keyspace keyspace;

	public CassandraClient(Keyspace keyspace, String columnFamilyName) {
		this.keyspace = keyspace;
		cfInfo = new ColumnFamily<String, String>(columnFamilyName, // Column
																	// Family
																	// Name
				StringSerializer.get(), // Key Serializer
				StringSerializer.get()); // Column Serializer
		ImmutableMap<String, Object> keyspaceMap = ImmutableMap
				.<String, Object> builder()
				.put("strategy_options",
						ImmutableMap.<String, Object> builder()
								.put("replication_factor", "1").build())
				.put("strategy_class", "SimpleStrategy").build();

		try {
			if (keyspace != null) {
				keyspace.createKeyspaceIfNotExists(keyspaceMap);
				keyspace.createColumnFamily(cfInfo, null);
			}
		} catch (ConnectionException e) {
		}
	}

	@Override
	public void write(String key, String value) {
		if (keyspace != null) {
			MutationBatch mb = keyspace.prepareMutationBatch();
			mb.withRow(cfInfo, key).putColumn("JSON", value);

			try {
				mb.execute();
			} catch (ConnectionException e) {
				System.out.println("Didn't wrote { " + key + ":" + value
						+ "}. " + e.getMessage());
			}
		}
	}

	@Override
	public String read(String key) {
		if (keyspace != null) {
			Column<String> result;
			try {
				result = keyspace.prepareQuery(cfInfo).getKey(key)
						.getColumn("JSON").execute().getResult();
				return result.getStringValue();
			} catch (ConnectionException e) {
				return e.getMessage();
			}

		}
		return "No connection to DB";
	}
	
	@Override
	public void remove(String key) {
		if (keyspace != null) {
			MutationBatch mb = keyspace.prepareMutationBatch();
			mb.withRow(cfInfo, key).delete();

			try {
				mb.execute();
			} catch (ConnectionException e) {
			}
		}
	}

	@Override
	public String readAll() {
		if (keyspace != null) {
			try {
				Rows<String, String> result = keyspace.prepareQuery(cfInfo)
						.getAllRows().execute().getResult();
				StringBuilder sb = new StringBuilder();
				Iterator<Row<String, String>> iterator = result.iterator();
				while (iterator.hasNext()) {
					Row<String, String> row = iterator.next();

					sb.append("Key=");
					sb.append(row.getKey());
					sb.append(":");
					sb.append("Value=");
					sb.append(row.getColumns().getColumnByName("JSON")
							.getStringValue());
					sb.append("\n");

				}
				return sb.toString();
			} catch (Exception e) {
				return e.getMessage();
			}

		}
		return "No connection to DB";
	}
}
