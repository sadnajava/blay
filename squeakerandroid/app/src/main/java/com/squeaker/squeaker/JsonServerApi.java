package com.squeaker.squeaker;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.squeaker.utils.AudioCodecSettings;
import com.squeaker.utils.BinaryDataSerializer;
import com.squeaker.utils.CompressedBase64Serializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class JsonServerApi implements ServerApi {
    private final String apiBaseUrl;
    private String sessionId = null;
    private BinaryDataSerializer dataSerializer = new CompressedBase64Serializer();

    private static final String API_BASE_URL = "http://%s:8080/squeaker/api";

    private static final String SESSION_ID_FIELD = "sessionId";

    private static final String LOGIN_VERB = "login";
    private static final String LOGIN_EMAIL_FIELD = "email";
    private static final String LOGIN_PASSWORD_FIELD = "password";

    private static final String UPDATE_FEED_VERB = "updatefeed";

    private static final String BROADCAST_SQUEAK_VERB = "recordsqueak";
    private static final String BROADCAST_SQUEAK_EMAIL_FIELD = "email";
    private static final String BROADCAST_SQUEAK_DURATION_FIELD = "duration";
    private static final String BROADCAST_SQUEAK_DATE_FIELD = "date";
    private static final String BROADCAST_SQUEAK_DATA_FIELD = "data";
    private static final String BROADCAST_SQUEAK_CAPTION_FIELD = "caption";

    private static final String FIND_USER_VERB = "finduser";
    private static final String FIND_USER_SEARCH_VALUE_FIELD = "searchValue";
    private static final String FIND_USER_SQUEAKS_COUNT_FIELD = "squeaksCount";
    private static final String FIND_USER_EMAIL_FIELD = "email";

    private static final String GET_SQUEAK_VERB = "getsqueak";
    private static final String GET_SQUEAK_SQUEAK_ID_FIELD = "squeakId";

    private static final String FOLLOW_USER_VERB = "followsqueaker";
    private static final String FOLLOW_USER_EMAIL_FIELD = "email";

    private static final String UNFOLLOW_USER_VERB = "unfollowsqueaker";

    private static final String GET_USER_PROFILE_VERB = "getsqueaker";
    private static final String GET_USER_PROFILE_INPUT_EMAIL_FIELD = "searchValue";
    private static final String GET_USER_PROFILE_EMAIL_FIELD = "email";
    private static final String GET_USER_PROFILE_IS_FOLLOWING_FIELD = "following";
    private static final String GET_USER_PROFILE_FOLLOWING_USERS_FIELD = "follows";
    private static final String GET_USER_PROFILE_SQUEAKS_FIELD = "squeaks";

    private static final String DELETE_SQUEAK_VERB = "deletesqueak";
    private static final String DELETE_SQUEAK_SQUEAK_ID_FIELD = "squeakId";

    private static final AudioCodecSettings CODEC_SETTINGS = new SqueakerCodecSettings();

    public JsonServerApi(String serverIp) {
        this.apiBaseUrl = String.format(API_BASE_URL, serverIp);
    }

    protected JsonServerApi(Parcel in) {
        apiBaseUrl = in.readString();
        sessionId = in.readString();
    }

    private String apiVerbToUrl(String apiVerb) {
        return apiBaseUrl + "/" + apiVerb;
    }

    private void assertSessionId() throws IllegalStateException {
        if (sessionId == null)
            throw new IllegalStateException("Must obtain a session ID via login() before calling this method!");
    }

    @Override
    public String login(String email, String password) throws Exception {
        JSONObject loginJson = new JSONObject()
                                        .put(LOGIN_EMAIL_FIELD, email)
                                        .put(LOGIN_PASSWORD_FIELD, password);

        String response = callApiWithJson(LOGIN_VERB, loginJson);

        final String sessionId = new JSONObject(response).getString(SESSION_ID_FIELD);
        this.sessionId = sessionId;

        return sessionId;
    }

    @Override
    public ArrayList<SqueakMetadata> updateFeed() throws Exception {
        assertSessionId();

        JSONObject sessionJson = new JSONObject().put(SESSION_ID_FIELD, sessionId);

        String response = callApiWithJson(UPDATE_FEED_VERB, sessionJson);

        return deserializeSqueakArray(new JSONArray(response));
    }

    @Override
    public void broadcastSqueak(SqueakMetadata sm, byte[] squeakAudioData) throws Exception {
        assertSessionId();

        String encodedAudioData = dataSerializer.encode(squeakAudioData);

        JSONObject reqJson = new JSONObject()
                                        .put(SESSION_ID_FIELD, sessionId)
                                        .put(BROADCAST_SQUEAK_DURATION_FIELD, sm.getDuration())
                                        .put(BROADCAST_SQUEAK_DATE_FIELD, sm.getDate())
                                        .put(BROADCAST_SQUEAK_CAPTION_FIELD, sm.getCaption())
                                        .put(BROADCAST_SQUEAK_DATA_FIELD, encodedAudioData);

        callApiWithJson(BROADCAST_SQUEAK_VERB, reqJson);
    }

    @Override
    public ArrayList<UserMetadata> findUser(String searchValue) throws Exception {
        assertSessionId();

        JSONObject reqJson = new JSONObject()
                                        .put(SESSION_ID_FIELD, sessionId)
                                        .put(FIND_USER_SEARCH_VALUE_FIELD, searchValue);

        String response = callApiWithJson(FIND_USER_VERB, reqJson);
        JSONArray responseJson = new JSONArray(response);
        ArrayList<UserMetadata> users = deserializeUserMetadataArray(responseJson);

        return users;
    }

    @Override
    public UserProfile getUserProfile(String email) throws Exception {
        assertSessionId();

        JSONObject reqJson = new JSONObject()
                                        .put(SESSION_ID_FIELD, sessionId)
                                        .put(GET_USER_PROFILE_INPUT_EMAIL_FIELD, email);

        String response = callApiWithJson(GET_USER_PROFILE_VERB, reqJson);
        JSONObject userProfileJson = new JSONObject(response);

        ArrayList<SqueakMetadata> squeaks = deserializeSqueakArray(userProfileJson.getJSONArray(GET_USER_PROFILE_SQUEAKS_FIELD));
        ArrayList<UserMetadata> followingUsers = deserializeUserMetadataArray(userProfileJson.getJSONArray(GET_USER_PROFILE_FOLLOWING_USERS_FIELD));

        return new UserProfile(userProfileJson.getString(GET_USER_PROFILE_EMAIL_FIELD), squeaks,
                                followingUsers, userProfileJson.getBoolean(GET_USER_PROFILE_IS_FOLLOWING_FIELD));
    }

    @Override
    public void followUser(String email) throws Exception {
        assertSessionId();

        JSONObject reqJson = new JSONObject()
                .put(SESSION_ID_FIELD, sessionId)
                .put(FOLLOW_USER_EMAIL_FIELD, email);

        callApiWithJson(FOLLOW_USER_VERB, reqJson);
    }

    @Override
    public void unfollowUser(String email) throws Exception {
        assertSessionId();

        JSONObject reqJson = new JSONObject()
                .put(SESSION_ID_FIELD, sessionId)
                .put(FOLLOW_USER_EMAIL_FIELD, email);

        callApiWithJson(UNFOLLOW_USER_VERB, reqJson);
    }

    @Override
    public byte[] getSqueakAudio(String squeakId) throws Exception {
        assertSessionId();

        JSONObject reqJson = new JSONObject()
                                        .put(SESSION_ID_FIELD, sessionId)
                                        .put(GET_SQUEAK_SQUEAK_ID_FIELD, squeakId);

        try {
            return dataSerializer.decode(callApiWithJson(GET_SQUEAK_VERB, reqJson));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void deleteSqueak(String squeakId) throws Exception {
        assertSessionId();

        JSONObject reqJson = new JSONObject()
                                    .put(SESSION_ID_FIELD, sessionId)
                                    .put(DELETE_SQUEAK_SQUEAK_ID_FIELD, squeakId);

        callApiWithJson(DELETE_SQUEAK_VERB, reqJson);
    }

    @Override
    public AudioCodecSettings getCodecSettings() {
        return CODEC_SETTINGS;
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

    private String callApiWithJson(String apiVerb, JSONObject reqJson) throws IOException {
        URL url = new URL(apiVerbToUrl(apiVerb));
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(apiBaseUrl);
        dest.writeString(sessionId);
    }

}
