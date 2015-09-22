package workshop.rest;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import workshop.BusinessLogic;
import workshop.IBusinessLogic;
import workshop.SessionId;
import workshop.StringUtils;
import workshop.cassandra.CassandraFactory;
import workshop.cassandra.ICassandraClient;
import workshop.dal.SqueakerData;
import workshop.dal.datamodel.SqueakData;
import workshop.dal.datamodel.SqueakInfo;
import workshop.rest.datamodel.FindInfoInput;
import workshop.rest.datamodel.GetSqueakInput;
import workshop.rest.datamodel.IOConverter;
import workshop.rest.datamodel.LoginInput;
import workshop.rest.datamodel.RecordSqueakInput;
import workshop.rest.datamodel.SessionIdInput;
import workshop.rest.datamodel.followSqueakerInput;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("api/")
public class RestAPI {

	IBusinessLogic bi = BusinessLogic.getInstance();
	String cfName = "TomerCF";

	/**
	 * Method handling HTTP GET requests. The returned object will be sent to
	 * the client as "text/plain" media type.
	 * 
	 * @return String that will be returned as a text/plain response.
	 */
	@GET
	@Path("/write/{key}/{value}")
	@Produces(MediaType.TEXT_PLAIN)
	public String write(@PathParam("key") String key,
			@PathParam("value") String value) {
		ICassandraClient db = CassandraFactory.connect(cfName);
		db.write(key, value);
		return "Got it!";
	}

	@GET
	@Path("/read/{key}")
	@Produces(MediaType.TEXT_PLAIN)
	public String read(@PathParam("key") String key) {
		ICassandraClient db = CassandraFactory.connect(cfName);
		return db.read(key);
	}

	@GET
	@Path("/readall")
	@Produces(MediaType.TEXT_PLAIN)
	public String readAll() {
		ICassandraClient db = CassandraFactory.connect(cfName);
		return db.readAll();
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@Type(LoginInput.class) LoginInput loginInfo) {
		if (loginInfo == null || StringUtils.isEmpty(loginInfo.getEmail())
				|| StringUtils.isEmpty(loginInfo.getPassword())) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		SessionId sid = bi.login(loginInfo.getEmail(), loginInfo.getPassword());

		if (sid == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			return Response.ok(mapper.writeValueAsString(sid),
					MediaType.APPLICATION_JSON).build();
		} catch (JsonProcessingException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/updatefeed")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateFeed(
			@Type(SessionIdInput.class) SessionIdInput sessionId) {
		if (sessionId == null || StringUtils.isEmpty(sessionId.getSessionId())) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		Set<SqueakInfo> squeaks = bi.updateFeed(IOConverter.convert(sessionId));

		ObjectMapper mapper = new ObjectMapper();
		try {
			return Response.ok(
					mapper.writeValueAsString(IOConverter.convert(squeaks)),
					MediaType.APPLICATION_JSON).build();
		} catch (JsonProcessingException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/recordsqueak")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response recordSqueak(
			@Type(RecordSqueakInput.class) RecordSqueakInput squeak) {
		if (squeak == null || StringUtils.isEmpty(squeak.getSessionId())
				|| StringUtils.isEmpty(squeak.getData())) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		SessionId sessionId = new SessionId(squeak.getSessionId());
		SqueakInfo info = new SqueakInfo(squeak.getEmail(),
				squeak.getDuration(), squeak.getDate());
		SqueakData data = new SqueakData(squeak.getData());
		bi.recordSqueak(sessionId, info, data);
		return Response.status(Status.OK).build();
	}

	@POST
	@Path("/followsqueaker")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response followSqueaker(
			@Type(followSqueakerInput.class) followSqueakerInput follow) {
		if (follow == null || //
				StringUtils.isEmpty(follow.getSessionId()) || //
				StringUtils.isEmpty(follow.getEmail())) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		boolean result = bi.follow(new SessionId(follow.getSessionId()),
				follow.getEmail());
		if (result) {
			return Response.status(Status.OK).build();
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	@POST
	@Path("/unfollowsqueaker")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response unfollowSqueaker(
			@Type(followSqueakerInput.class) followSqueakerInput unfollow) {
		if (unfollow == null || StringUtils.isEmpty(unfollow.getSessionId())) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		boolean result = bi.unfollow(new SessionId(unfollow.getSessionId()),
				unfollow.getEmail());
		if (result) {
			return Response.status(Status.OK).build();
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/getsqueak")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSqueak(@Type(GetSqueakInput.class) GetSqueakInput request) {
		if (request == null || StringUtils.isEmpty(request.getSessionId())
				|| StringUtils.isEmpty(request.getSqeuakId())) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		SqueakData squeak = bi.getSqueak(new SessionId(request.getSessionId()),
				request.getSqeuakId());
		if (squeak == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			return Response.ok(
					mapper.writeValueAsString(IOConverter.convert(squeak)),
					MediaType.APPLICATION_JSON).build();
		} catch (JsonProcessingException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/finduser")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response findUser(@Type(FindInfoInput.class) FindInfoInput search) {
		return null;
	}

	@POST
	@Path("/getsqueaker")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSqueaker(@Type(FindInfoInput.class) FindInfoInput search) {
		if (search == null || StringUtils.isEmpty(search.getSessionId())
				|| StringUtils.isEmpty(search.getSearchValue())) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		SqueakerData squeaker = bi.getSqueaker(
				new SessionId(search.getSessionId()), search.getSearchValue());
		if (squeaker == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} else {
			ObjectMapper mapper = new ObjectMapper();
			try {
				return Response.ok(
						mapper.writeValueAsString(squeaker),
						MediaType.APPLICATION_JSON).build();
			} catch (JsonProcessingException e) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}
}
