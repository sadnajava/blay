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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class JsonApi {

    private static final String SERVER_HOST = "http://10.100.102.2:8080/squeaker/api/";

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

    private static final String FIND_USER_URL = SERVER_HOST + "finduser";
    private static final String FIND_USER_SEARCH_VALUE_FIELD = "searchValue";
    private static final String FIND_USER_SQUEAKS_COUNT_FIELD = "squeaksCount";
    private static final String FIND_USER_EMAIL_FIELD = "email";

    private static final String GET_SQUEAK_URL = SERVER_HOST + "getsqueak";
    private static final String GET_SQUEAK_SQUEAK_ID_FIELD = "squeakId";
    private static final String GET_SQUEAK_AUDIO_DATA_FIELD = "data";

    private static final String FOLLOW_USER_URL = SERVER_HOST + "followsqueaker";
    private static final String FOLLOW_USER_EMAIL_FIELD = "email";

    private static final String UNFOLLOW_USER_URL = SERVER_HOST + "unfollowsqueaker";

    private static final String GET_USER_PROFILE_URL = SERVER_HOST + "getsqueaker";
    private static final String GET_USER_PROFILE_INPUT_EMAIL_FIELD = "searchValue";
    private static final String GET_USER_PROFILE_EMAIL_FIELD = "email";
    private static final String GET_USER_PROFILE_IS_FOLLOWING_FIELD = "following";
    private static final String GET_USER_PROFILE_SQUEAKS_FIELD = "squeaks";

    public static SessionId login(String email, String password) throws JSONException, IOException {
        JSONObject loginJson = new JSONObject()
                                        .put(LOGIN_EMAIL_FIELD, email)
                                        .put(LOGIN_PASSWORD_FIELD, password);

        String response = callApiWithJson(LOGIN_URL, loginJson);
        JSONObject responseJson = new JSONObject(response);
        return new SessionId(responseJson.getString(SESSION_ID_FIELD));
    }

    public static ArrayList<SqueakMetadata> updateFeed(SessionId sid) throws JSONException, IOException {
        JSONObject sidJson = new JSONObject().put(SESSION_ID_FIELD, sid.getId());

        String response = callApiWithJson(UPDATE_FEED_URL, sidJson);

        return deserializeSqueakArray(new JSONArray(response));
    }

    public static void broadcastSqueak(SessionId sid, SqueakMetadata sm, byte[] squeakAudioData) throws JSONException, IOException {
        String encodedAudioData = Base64.encodeToString(squeakAudioData, Base64.NO_WRAP);

        JSONObject reqJson = new JSONObject()
                                        .put(SESSION_ID_FIELD, sid.getId())
                                        .put(BROADCAST_SQUEAK_EMAIL_FIELD, sm.getEmail())
                                        .put(BROADCAST_SQUEAK_DURATION_FIELD, sm.getDuration())
                                        .put(BROADCAST_SQUEAK_DATE_FIELD, sm.getDate())
                                        .put(BROADCAST_SQUEAK_DATA_FIELD, encodedAudioData);

        callApiWithJson(BROADCAST_SQUEAK_URL, reqJson);
    }

    public static ArrayList<UserMetadata> findUser(SessionId sid, String searchValue) throws JSONException, IOException {
        JSONObject reqJson = new JSONObject()
                                        .put(SESSION_ID_FIELD, sid.getId())
                                        .put(FIND_USER_SEARCH_VALUE_FIELD, searchValue);

        String response = callApiWithJson(FIND_USER_URL, reqJson);
        JSONArray responseJson = new JSONArray(response);
        ArrayList<UserMetadata> users = new ArrayList<>(responseJson.length());

        for (int i = 0; i < responseJson.length(); ++i) {
            JSONObject user = responseJson.getJSONObject(i);
            users.add(new UserMetadata(user.getString(FIND_USER_EMAIL_FIELD), user.getInt(FIND_USER_SQUEAKS_COUNT_FIELD)));
        }

        return users;
    }

    public static UserProfile getUserProfile(SessionId sid, UserMetadata userMetadata) throws JSONException, IOException {
        JSONObject reqJson = new JSONObject()
                                        .put(SESSION_ID_FIELD, sid.getId())
                                        .put(GET_USER_PROFILE_INPUT_EMAIL_FIELD, userMetadata.getEmail());

        String response = callApiWithJson(GET_USER_PROFILE_URL, reqJson);
        JSONObject userProfileJson = new JSONObject(response);

        ArrayList<SqueakMetadata> squeaks = deserializeSqueakArray(userProfileJson.getJSONArray(GET_USER_PROFILE_SQUEAKS_FIELD));

        UserProfile userProfile = new UserProfile(userProfileJson.getString(GET_USER_PROFILE_EMAIL_FIELD), squeaks,
                                        new ArrayList<UserMetadata>(), userProfileJson.getBoolean(GET_USER_PROFILE_IS_FOLLOWING_FIELD));

        return userProfile;
    }

    public static byte[] getSqueakAudio(SessionId sid, String squeakId) throws JSONException, IOException {
        JSONObject reqJson = new JSONObject()
                                        .put(GET_SQUEAK_SQUEAK_ID_FIELD, squeakId);

        JSONObject response = new JSONObject(callApiWithJson(GET_SQUEAK_URL, reqJson));
        return Base64.decode(response.getString(GET_SQUEAK_AUDIO_DATA_FIELD), Base64.NO_WRAP);
    }

    private static SqueakMetadata deserializeSqueakMetadata(JSONObject jsm) throws JSONException {
        return new SqueakMetadata(  jsm.getString("squeakId"),
                                    jsm.getString("email"),
                                    jsm.getInt("duration"),
                                    jsm.getString("date"),
                                    jsm.getString("caption"));
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

    public static void followUser(SessionId sid, String email) throws JSONException, IOException {
        JSONObject reqJson = new JSONObject()
                                        .put(SESSION_ID_FIELD, sid.getId())
                                        .put(FOLLOW_USER_EMAIL_FIELD, email);

        callApiWithJson(FOLLOW_USER_URL, reqJson);
    }

    public static void unfollowUser(SessionId sid, String email) throws JSONException, IOException {
        JSONObject reqJson = new JSONObject()
                                        .put(SESSION_ID_FIELD, sid.getId())
                                        .put(FOLLOW_USER_EMAIL_FIELD, email);

        callApiWithJson(UNFOLLOW_USER_URL, reqJson);
    }
}
