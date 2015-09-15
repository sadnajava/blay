package workshop;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import workshop.cassandra.CassandraClient;
import workshop.cassandra.CassandraFactory;
import workshop.cassandra.ICassandraClient;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("db/")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("/write/{key}/{value}")
    @Produces(MediaType.TEXT_PLAIN)
    public String write(@PathParam("key") String key, @PathParam("value") String value) {
        System.out.println("Inside getIt!");
        ICassandraClient db = CassandraFactory.connect("bla");
        db.write(key, value);
        return "Got it!";
    }
    
    @GET
    @Path("/read/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public String read(@PathParam("key") String key) {
        ICassandraClient db = CassandraFactory.connect("bla");
        return db.read(key);
    }
    
    @GET
    @Path("/readall")
    @Produces(MediaType.TEXT_PLAIN)
    public String readAll() {
        ICassandraClient db = CassandraFactory.connect("bla");
        return db.readAll();
    }
}
