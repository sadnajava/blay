package com.squeaker.app;

import android.os.AsyncTask;
import android.view.View;

import com.squeaker.squeaker.ServerApi;
import com.squeaker.utils.AudioPlayer;

public class FetchPlaySqueakOnClickListener implements View.OnClickListener {
    private final ServerApi api;
    private final String squeakId;

    public FetchPlaySqueakOnClickListener(ServerApi api, String squeakId) {
        this.api = api;
        this.squeakId = squeakId;
    }

    @Override
    public void onClick(View v) {
        FetchSqueakAudioDataTask task = new FetchSqueakAudioDataTask();
        task.execute();
    }

    /**
     * Represents an async squeak audio fetch task.
     */
    private class FetchSqueakAudioDataTask extends AsyncTask<Void, Void, byte[]> {
        @Override
        protected byte[] doInBackground(Void... params) {
            try {
                return api.getSqueakAudio(squeakId);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(byte[] audioData) {
            super.onPostExecute(audioData);

            if (audioData != null) {
                AudioPlayer player = new AudioPlayer(api.getCodecSettings(), audioData);
            }
        }
    }
}
