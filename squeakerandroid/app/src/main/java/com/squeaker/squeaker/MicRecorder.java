package com.squeaker.squeaker;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.util.Date;

/**
 * Helper class to manage recording audio.
 */
public class MicRecorder extends Thread {
    private final int readChunkSize = 256;
    private Date startTime, endTime;
    private int currentWriteIndex = 0;
    private boolean stopped = false;
    private byte[] returnBuffer = null;

    /**
     * Give the thread high priority so that it's not canceled unexpectedly, and start it
     */
    public MicRecorder() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        start();
    }

    @Override
    public void run() {
        int N = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        if (N == AudioRecord.ERROR || N == AudioRecord.ERROR_BAD_VALUE) {
            return;
        }

        Log.i("Recorder", "min recordBuffer size is " + N);
        final int bufferSize = N * 1000;
        final byte[] recordBuffer = new byte[bufferSize];
        final AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        Log.i("Recorder", "Running Audio Thread");

        try {
            startTime = new Date();
            recorder.startRecording();

            while (!stopped && currentWriteIndex < bufferSize) {
                Log.i("Recorder", "Writing new data to recordBuffer from " + currentWriteIndex);

                int read = recorder.read(recordBuffer, currentWriteIndex, readChunkSize);

                if (read != AudioRecord.ERROR_INVALID_OPERATION && read != AudioRecord.ERROR_BAD_VALUE) {
                    currentWriteIndex += readChunkSize;
                } else {
                    throw new IOException("Can't read audio");
                }

                yield();
            }

            endTime = new Date();

            // Truncate array to recorded size.
            returnBuffer = java.util.Arrays.copyOf(recordBuffer, currentWriteIndex);
        } catch (Throwable x) {
            Log.w("Recorder", "Error reading voice audio", x);
        } finally {
            recorder.stop();
            recorder.release();
        }
    }

    public boolean isRecording() {
        return returnBuffer == null;
    }

    public void stopRecording() {
        stopped = true;
    }

    public byte[] getRecordedBytes() {
        return returnBuffer;
    }

    public long getDuration() {
        return endTime.getTime() - startTime.getTime();
    }
}
