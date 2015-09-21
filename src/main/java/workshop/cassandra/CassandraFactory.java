package workshop.cassandra;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;





public class CassandraFactory {
    public static ICassandraClient connect(String columnFamily){
    	AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
        .forCluster("Test Cluster")
        .forKeyspace("Test")
        .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()      
            .setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE)
            .setTargetCassandraVersion("2.0")
        )
        .withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("MyConnectionPool")
            .setPort(9160)
            .setMaxConnsPerHost(1)
            .setSeeds("127.0.0.1:9160")
        )
        .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
        .buildKeyspace(ThriftFamilyFactory.getInstance());    	

        context.start();
        return new CassandraClient(context.getClient(), columnFamily);
//        return null;
    }
}
