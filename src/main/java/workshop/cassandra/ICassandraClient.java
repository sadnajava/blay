package workshop.cassandra;

public interface ICassandraClient {
    void write (String key, String value);
    String read (String key);
    String readAll ();
    void remove(String key);
}
