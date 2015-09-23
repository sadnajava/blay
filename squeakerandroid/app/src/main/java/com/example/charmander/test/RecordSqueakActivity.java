package com.example.charmander.test;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.nio.Buffer;
import java.nio.ShortBuffer;


public class RecordSqueakActivity extends ActionBarActivity {
    private EditText squeakText;

    private Recorder recorder;
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
    }

    /**
     * Helper class to manage recording audio.
     */
    /*
 * Thread to manage live recording/playback of voice input from the device's microphone.
 */
    private class Recorder extends Thread {
        private boolean stopped = false;
        private byte[][] buffers = new byte[256][160];

        /**
         * Give the thread high priority so that it's not canceled unexpectedly, and start it
         */
        private Recorder() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            start();
        }

        @Override
        public void run() {
            int N = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, N * 10);

            int currentBuffer = 0;

            Log.i("Recorder", "Running Audio Thread");

            try {
                recorder.startRecording();

                while (!stopped) {
                    byte[] buffer = buffers[currentBuffer++ % buffers.length];
                    Log.i("Recorder", "Writing new data to buffer with length " + buffer.length);
                    recorder.read(buffer, 0, buffer.length);
                }
            } catch (Throwable x) {
                Log.w("Recorder", "Error reading voice audio", x);
            } finally {
                recorder.stop();
                recorder.release();
            }
        }

        public void close() {
            stopped = true;
        }

        public byte[] getRecordedBytes() {
            return null;
        }

    }

    /**
     * Represents an asynchronous news feed fetch task.
     */
    private class BroadcastSqueakTask extends AsyncTask<Void, Void, Boolean> {
        private SessionId sid;

        BroadcastSqueakTask(SessionId sid) {
            this.sid = sid;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
//                JsonApi.broadcastSqueak(sid);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
        }
    }
}
