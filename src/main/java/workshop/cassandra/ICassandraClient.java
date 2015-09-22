package workshop.cassandra;

import java.util.Collection;

public interface ICassandraClient {
    void write (String key, String value);
    String read (String key);
    String readAllFormatted ();
    Collection<String> readAll();
    void remove(String key);
}
