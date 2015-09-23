package com.example.charmander.test;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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
        JSONArray responseJson = new JSONArray(response);
        ArrayList<SqueakMetadata> squeaks = new ArrayList<>(responseJson.length());

        for (int i = 0; i < responseJson.length(); ++i) {
            squeaks.add(deserializeSqueakMetadata(responseJson.getJSONObject(i)));
        }

        return squeaks;
    }

    public static void broadcastSqueak(SessionId sid, SqueakMetadata sm, String squeakData) throws JSONException, IOException {
        JSONObject reqJson = new JSONObject()
                                        .put(SESSION_ID_FIELD, sid.getId())
                                        .put(BROADCAST_SQUEAK_EMAIL_FIELD, sm.getEmail())
                                        .put(BROADCAST_SQUEAK_DURATION_FIELD, sm.getDuration())
                                        .put(BROADCAST_SQUEAK_DATE_FIELD, sm.getDate())
                                        .put(BROADCAST_SQUEAK_DATA_FIELD, squeakData);

        callApiWithJson(BROADCAST_SQUEAK_URL, reqJson);
    }

    public static void findUser(SessionId sid, String searchValue) throws JSONException, IOException {
        JSONObject reqJson = new JSONObject()
                                        .put(SESSION_ID_FIELD, sid.getId())
                                        .put(FIND_USER_SEARCH_VALUE_FIELD, searchValue);

        String response = callApiWithJson(FIND_USER_URL, reqJson);
    }

    private static SqueakMetadata deserializeSqueakMetadata(JSONObject jsm) throws JSONException {
        return new SqueakMetadata(  jsm.getString("squeakId"),
                                    jsm.getString("email"),
                                    jsm.getInt("duration"),
                                    jsm.getString("date"),
                                    "");
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
            return "";
        } finally {
            urlConnection.disconnect();
        }
    }
}
