package com.squeaker.squeaker;

import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class JsonApi {

    private static final String SERVER_HOST = "http://localhost:8080/squeaker/api/";

    private static final String SESSION_ID_FIELD = "sessionId";

    private static final String LOGIN_URL = SERVER_HOST + "login";
    private static final String LOGIN_EMAIL_FIELD = "email";
    private static final String LOGIN_PASSWORD_FIELD = "password";

    private static final String UPDATE_FEED_URL = SERVER_HOST + "updatefeed";

    private static final String BROADCAST_SQUEAK_URL = SERVER_HOST + "recordsqueak";
    private static final String BROADCAST_SQUEAK_EMAIL_FIELD = "email";
    private static final String BROADCAST_SQUEAK_DURATION_FIELD = "duration";
    private static final String BROADCAST_SQUEAK_DATE_FIELD = "date";
    private static final String BROADCAST_SQUEAK_DATA_FIELD = "data";
    private static final String BROADCAST_SQUEAK_CAPTION_FIELD = "caption";

    private static final String FIND_USER_URL = SERVER_HOST + "finduser";
    private static final String FIND_USER_SEARCH_VALUE_FIELD = "searchValue";
    private static final String FIND_USER_SQUEAKS_COUNT_FIELD = "squeaksCount";
    private static final String FIND_USER_EMAIL_FIELD = "email";

    private static final String GET_SQUEAK_URL = SERVER_HOST + "getsqueak";
    private static final String GET_SQUEAK_SQUEAK_ID_FIELD = "squeakId";

    private static final String FOLLOW_USER_URL = SERVER_HOST + "followsqueaker";
    private static final String FOLLOW_USER_EMAIL_FIELD = "email";

    private static final String UNFOLLOW_USER_URL = SERVER_HOST + "unfollowsqueaker";

    private static final String GET_USER_PROFILE_URL = SERVER_HOST + "getsqueaker";
    private static final String GET_USER_PROFILE_INPUT_EMAIL_FIELD = "searchValue";
    private static final String GET_USER_PROFILE_EMAIL_FIELD = "email";
    private static final String GET_USER_PROFILE_IS_FOLLOWING_FIELD = "following";
    private static final String GET_USER_PROFILE_FOLLOWING_USERS_FIELD = "follows";
    private static final String GET_USER_PROFILE_SQUEAKS_FIELD = "squeaks";

    private static final String DELETE_SQUEAK_URL = SERVER_HOST + "deletesqueak";
    private static final String DELETE_SQUEAK_SQUEAK_ID_FIELD = "squeakId";

    public static Session login(String email, String password) throws JSONException, IOException {
        JSONObject loginJson = new JSONObject()
                                        .put(LOGIN_EMAIL_FIELD, email)
                                        .put(LOGIN_PASSWORD_FIELD, password);

        String response = callApiWithJson(LOGIN_URL, loginJson);
        JSONObject responseJson = new JSONObject(response);
        return new Session(responseJson.getString(SESSION_ID_FIELD), email);
    }

    public static ArrayList<SqueakMetadata> updateFeed(Session session) throws JSONException, IOException {
        JSONObject sessionJson = new JSONObject().put(SESSION_ID_FIELD, session.getId());

        String response = callApiWithJson(UPDATE_FEED_URL, sessionJson);

        return deserializeSqueakArray(new JSONArray(response));
    }

    public static void broadcastSqueak(Session session, SqueakMetadata sm, byte[] squeakAudioData) throws JSONException, IOException {
        String encodedAudioData = encodeAudioData(squeakAudioData);

        JSONObject reqJson = new JSONObject()
                                        .put(SESSION_ID_FIELD, session.getId())
                                        .put(BROADCAST_SQUEAK_DURATION_FIELD, sm.getDuration())
                                        .put(BROADCAST_SQUEAK_DATE_FIELD, sm.getDate())
                                        .put(BROADCAST_SQUEAK_CAPTION_FIELD, sm.getCaption())
                                        .put(BROADCAST_SQUEAK_DATA_FIELD, encodedAudioData);

        callApiWithJson(BROADCAST_SQUEAK_URL, reqJson);
    }

    public static ArrayList<UserMetadata> findUser(Session session, String searchValue) throws JSONException, IOException {
        JSONObject reqJson = new JSONObject()
                                        .put(SESSION_ID_FIELD, session.getId())
                                        .put(FIND_USER_SEARCH_VALUE_FIELD, searchValue);

        String response = callApiWithJson(FIND_USER_URL, reqJson);
        JSONArray responseJson = new JSONArray(response);
        ArrayList<UserMetadata> users = deserializeUserMetadataArray(responseJson);

        return users;
    }

    public static UserProfile getUserProfile(Session session, String email) throws JSONException, IOException {
        JSONObject reqJson = new JSONObject()
                                        .put(SESSION_ID_FIELD, session.getId())
                                        .put(GET_USER_PROFILE_INPUT_EMAIL_FIELD, email);

        String response = callApiWithJson(GET_USER_PROFILE_URL, reqJson);
        JSONObject userProfileJson = new JSONObject(response);

        ArrayList<SqueakMetadata> squeaks = deserializeSqueakArray(userProfileJson.getJSONArray(GET_USER_PROFILE_SQUEAKS_FIELD));
        ArrayList<UserMetadata> followingUsers = deserializeUserMetadataArray(userProfileJson.getJSONArray(GET_USER_PROFILE_FOLLOWING_USERS_FIELD));

        return new UserProfile(userProfileJson.getString(GET_USER_PROFILE_EMAIL_FIELD), squeaks,
                                followingUsers, userProfileJson.getBoolean(GET_USER_PROFILE_IS_FOLLOWING_FIELD));
    }

    public static void followUser(Session session, String email) throws JSONException, IOException {
        JSONObject reqJson = new JSONObject()
                .put(SESSION_ID_FIELD, session.getId())
                .put(FOLLOW_USER_EMAIL_FIELD, email);

        callApiWithJson(FOLLOW_USER_URL, reqJson);
    }

    public static void unfollowUser(Session session, String email) throws JSONException, IOException {
        JSONObject reqJson = new JSONObject()
                .put(SESSION_ID_FIELD, session.getId())
                .put(FOLLOW_USER_EMAIL_FIELD, email);

        callApiWithJson(UNFOLLOW_USER_URL, reqJson);
    }

    public static byte[] getSqueakAudio(Session session, String squeakId) throws JSONException, IOException {
        JSONObject reqJson = new JSONObject()
                                        .put(SESSION_ID_FIELD, session.getId())
                                        .put(GET_SQUEAK_SQUEAK_ID_FIELD, squeakId);

        try {
            return decodeAudioData(callApiWithJson(GET_SQUEAK_URL, reqJson));
        } catch (Exception e) {
            return null;
        }
    }

    public static void deleteSqueak(Session session, String squeakId) throws JSONException, IOException {
        JSONObject reqJson = new JSONObject()
                                    .put(SESSION_ID_FIELD, session.getId())
                                    .put(DELETE_SQUEAK_SQUEAK_ID_FIELD, squeakId);

        callApiWithJson(DELETE_SQUEAK_URL, reqJson);
    }

    @NonNull
    private static UserMetadata deserializeUserMetadata(JSONObject user) throws JSONException {
        return new UserMetadata(user.getString(FIND_USER_EMAIL_FIELD), user.getInt(FIND_USER_SQUEAKS_COUNT_FIELD));
    }

    @NonNull
    private static ArrayList<UserMetadata> deserializeUserMetadataArray(JSONArray usersJson) throws JSONException {
        ArrayList<UserMetadata> users = new ArrayList<>(usersJson.length());

        for (int i = 0; i < usersJson.length(); ++i) {
            JSONObject user = usersJson.getJSONObject(i);
            users.add(deserializeUserMetadata(user));
        }

        return users;
    }

    private static SqueakMetadata deserializeSqueakMetadata(JSONObject jsm) throws JSONException {
        return new SqueakMetadata(  jsm.getString("squeakId"),
                                    jsm.getString(BROADCAST_SQUEAK_EMAIL_FIELD),
                                    jsm.getInt(BROADCAST_SQUEAK_DURATION_FIELD),
                                    jsm.getString(BROADCAST_SQUEAK_DATE_FIELD),
                                    jsm.getString(BROADCAST_SQUEAK_CAPTION_FIELD));
    }

    @NonNull
    private static ArrayList<SqueakMetadata> deserializeSqueakArray(JSONArray responseJson) throws JSONException {
        ArrayList<SqueakMetadata> squeaks = new ArrayList<>(responseJson.length());

        for (int i = 0; i < responseJson.length(); ++i) {
            squeaks.add(deserializeSqueakMetadata(responseJson.getJSONObject(i)));
        }
        return squeaks;
    }

    private static String callApiWithJson(String apiUrl, JSONObject reqJson) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.connect();

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
            osw.write(reqJson.toString());
            osw.close();

            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new IOException(urlConnection.getResponseMessage());

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder strBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = reader.readLine()) != null)
                strBuilder.append(inputStr);

            return strBuilder.toString();
        } catch (Exception e) {
            Log.d("SQUEAKERJSONAPI", e.getMessage());
            throw e;
        } finally {
            urlConnection.disconnect();
        }
    }

    @NonNull
    private static String encodeAudioData(byte[] audioData) throws IOException {
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(audioData);

        ByteArrayOutputStream out = new ByteArrayOutputStream(audioData.length);

        deflater.finish();

        byte[] buffer = new byte[1024];

        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            out.write(buffer, 0, count);
        }

        out.close();
        deflater.end();

        return Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);
    }

    @NonNull
    private static byte[] decodeAudioData(String encodedAudioData) throws JSONException, DataFormatException, IOException {
        byte[] arrayData = Base64.decode(encodedAudioData, Base64.NO_WRAP);

        Inflater inflater = new Inflater();
        inflater.setInput(arrayData);

        ByteArrayOutputStream out = new ByteArrayOutputStream(arrayData.length);
        byte[] buffer = new byte[1024];

        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            out.write(buffer, 0, count);
        }

        out.close();
        inflater.end();

        return out.toByteArray();
    }
}
