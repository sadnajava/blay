package com.squeaker.app;

import android.content.Intent;
import android.os.Bundle;

import com.squeaker.squeaker.ServerApi;
import com.squeaker.squeaker.Session;
import com.squeaker.squeaker.ServerState;

/**
 * Class for serializing/deserializing the squeaker activity protocol parameters.
 */
public class SqueakerActivityProtocol {
    public static final String API_FIELD = "api";
    public static final String SESSION_FIELD = "sessionId";
    public static final String USER_FIELD = "user";
    public static final String MY_PROFILE_FIELD = "myProfile";

    public static ServerState deserializeActivityParameters(Intent intent) {
        final Bundle extras = intent.getExtras();

        final Session session = (Session) extras.get(SESSION_FIELD);
        final ServerApi api = (ServerApi) extras.get(API_FIELD);

        return new ServerState(session, api);
    }

    public static void serializeActivityParameters(Intent intent, Session session, ServerApi api) {
        intent.putExtra(SESSION_FIELD, session);
        intent.putExtra(API_FIELD, api);
    }
}
