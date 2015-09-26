package com.squeaker.squeaker;

import android.os.AsyncTask;
import android.view.View;

public class FetchPlaySqueakOnClickListener implements View.OnClickListener {
    private final Session session;
    private final String squeakId;

    public FetchPlaySqueakOnClickListener(Session session, String squeakId) {
        this.session = session;
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
                return JsonApi.getSqueakAudio(session, squeakId);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(byte[] audioData) {
            super.onPostExecute(audioData);

            if (audioData != null) {
                AudioPlayer player = new AudioPlayer(audioData);
            }
        }
    }
}
