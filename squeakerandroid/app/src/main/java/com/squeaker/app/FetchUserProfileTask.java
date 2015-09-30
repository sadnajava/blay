package com.squeaker.app;

import android.os.AsyncTask;

import com.squeaker.squeaker.ServerApi;
import com.squeaker.squeaker.UserProfile;

/**
 * Represents an asynchronous user profile fetch task.
 */
public class FetchUserProfileTask extends AsyncTask<String, Void, UserProfile> {
    private final ServerApi api;

    FetchUserProfileTask(ServerApi api) {
        this.api = api;
    }

    @Override
    protected UserProfile doInBackground(String... params) {
        if (params.length != 1)
            return null;

        final String email = params[0];

        try {
            return api.getUserProfile(email);
        } catch (Exception e) {
            return null;
        }
    }
}
