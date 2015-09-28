package com.squeaker.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;

import com.squeaker.squeaker.ServerApi;
import com.squeaker.squeaker.Session;
import com.squeaker.squeaker.ServerState;

/**
 * Activity subclass for serializing/deserializing the parameters between activities.
 */
public class Activity extends ActionBarActivity {
    protected Session session;
    protected ServerApi api;

    @Override
    protected void onResume() {
        super.onResume();

        ServerState state = SqueakerActivityProtocol.deserializeActivityParameters(getIntent());

        session = state.getSession();
        api = state.getApi();
    }

    public void startActivity(Intent intent) {
        SqueakerActivityProtocol.serializeActivityParameters(intent, session, api);

        super.startActivity(intent);
    }
}
