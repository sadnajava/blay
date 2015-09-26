package com.squeaker.squeaker;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;


public class RecordSqueakActivity extends ActionBarActivity {
    private Session session;
    private EditText squeakText;
    private MicRecorder recorder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_squeak);

        squeakText = (EditText) findViewById(R.id.squeakText);
    }

    @Override
    protected void onResume() {
        super.onResume();

        session = (Session) getIntent().getExtras().get(SqueakerAndroidConstants.SESSION_FIELD);
    }

    public void recordSqueak(View view) {
        if (recorder == null || !recorder.isRecording()) {
            recorder = new MicRecorder();
        } else {
            recorder.stopRecording();
        }
    }

    public void playbackSqueak(View view) {
        if (recorder == null)
            return;

        if (recorder.isRecording())
            recorder.stopRecording();

        while (recorder.isRecording()) {
            Thread.yield();
        }

        // Play recorded sound.
        new AudioPlayer(recorder.getRecordedBytes());
    }

    public void broadcastSqueak(View view) {
        if (recorder == null)
            return;

        recorder.stopRecording();

        final String caption = squeakText.getText().toString();

        BroadcastSqueakTask task = new BroadcastSqueakTask(caption, recorder);
        task.execute();
    }

    /**
     * Represents an asynchronous squeak broadcast task.
     */
    private class BroadcastSqueakTask extends AsyncTask<Void, Void, Boolean> {
        private String caption;
        private MicRecorder recorder;

        BroadcastSqueakTask(String caption, MicRecorder recorder) {
            this.caption = caption;
            this.recorder = recorder;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            while (recorder.isRecording()) {
                Thread.yield();
            }

            final String squeakDate = new SimpleDateFormat().format(new Date());
            SqueakMetadata sm = new SqueakMetadata("", "", recorder.getDuration(), squeakDate, caption);

            try {
                JsonApi.broadcastSqueak(session, sm, recorder.getRecordedBytes());
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
