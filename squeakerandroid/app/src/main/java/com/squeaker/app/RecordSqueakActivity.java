package com.squeaker.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.squeaker.squeaker.R;
import com.squeaker.squeaker.SqueakMetadata;
import com.squeaker.utils.AudioPlayer;
import com.squeaker.utils.MicRecorder;

import java.text.SimpleDateFormat;
import java.util.Date;


public class RecordSqueakActivity extends Activity {
    private EditText squeakText;
    private MicRecorder recorder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_squeak);

        squeakText = (EditText) findViewById(R.id.squeakText);
    }

    public void recordSqueak(View view) {
        if (recorder == null || !recorder.isRecording()) {
            recorder = new MicRecorder(api.getCodecSettings());
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
        new AudioPlayer(api.getCodecSettings(), recorder.getRecordedBytes());
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
                api.broadcastSqueak(sm, recorder.getRecordedBytes());
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
