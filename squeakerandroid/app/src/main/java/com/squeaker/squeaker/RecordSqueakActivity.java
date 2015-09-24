package com.squeaker.squeaker;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;


public class RecordSqueakActivity extends ActionBarActivity {
    private EditText squeakText;

    private MicRecorder recorder = null;
    private byte[] squeakData;

    private SessionId sid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_squeak);

        squeakText = (EditText) findViewById(R.id.squeakText);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String intentSid = getIntent().getExtras().getString(SqueakerAndroidConstants.SESSION_ID_FIELD);
        sid = new SessionId(intentSid);
    }

    public void recordSqueak(View view) {
        if (recorder == null || !recorder.isRecording()) {
            recorder = new MicRecorder();
        } else {
            recorder.stopRecording();
        }
    }

    public void playbackSqueak(View view) {
    }

    public void broadcastSqueak(View view) {
        if (recorder == null)
            return;

        recorder.stopRecording();

        final String caption = squeakText.getText().toString();

        BroadcastSqueakTask task = new BroadcastSqueakTask(sid, caption, recorder);
        task.execute();
    }

    /**
     * Represents an asynchronous squeak broadcast task.
     */
    private class BroadcastSqueakTask extends AsyncTask<Void, Void, Boolean> {
        private SessionId sid;
        private String caption;
        private MicRecorder recorder;

        BroadcastSqueakTask(SessionId sid, String caption, MicRecorder recorder) {
            this.sid = sid;
            this.caption = caption;
            this.recorder = recorder;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            while (recorder.isRecording()) {
                Thread.yield();
            }

            final String encodedAudio = Base64.encodeToString(recorder.getRecordedBytes(), Base64.DEFAULT);
            final String squeakDate = new SimpleDateFormat().format(new Date());
            final long squeakDuration = recorder.getDuration();

            SqueakMetadata sm = new SqueakMetadata("", "", squeakDuration, squeakDate, caption);

            try {
                JsonApi.broadcastSqueak(sid, sm, encodedAudio);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if (success)
                finish();
        }
    }
}
