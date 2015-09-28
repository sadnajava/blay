package com.squeaker.utils;

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
    private final AudioCodecSettings codecSettings;

    private Date startTime, endTime;
    private int currentWriteIndex = 0;
    private boolean stopped = false;
    private byte[] returnBuffer = null;

    /**
     * Give the thread high priority so that it's not canceled unexpectedly, and start it
     */
    public MicRecorder(AudioCodecSettings codecSettings) {
        this.codecSettings = codecSettings;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        start();
    }

    private int getBufferSize() throws IOException {
        final int bufferSize = AudioRecord.getMinBufferSize(codecSettings.getSampleRate(), AudioFormat.CHANNEL_IN_MONO, codecSettings.getBitDepth());
        Log.i("MicRecorder", "min recordBuffer size is " + bufferSize);

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            throw new IOException("Can't read audio");
        }

        return bufferSize;
    }

    private AudioRecord getAudioRecorder(int bufferSize) throws IOException {
        return new AudioRecord(MediaRecorder.AudioSource.MIC, codecSettings.getSampleRate(), AudioFormat.CHANNEL_IN_MONO, codecSettings.getBitDepth(), bufferSize);
    }

    @Override
    public void run() {
        Log.i("MicRecorder", "Running Audio Thread");

        try {
            final int bufferSize = getBufferSize() * 1000;
            final byte[] recordBuffer = new byte[bufferSize];
            final AudioRecord recorder = getAudioRecorder(bufferSize);

            try {
                startTime = new Date();
                recorder.startRecording();

                while (!stopped && currentWriteIndex < bufferSize) {
                    Log.i("MicRecorder", "Writing new data to recordBuffer from " + currentWriteIndex);

                    int readChunkSize = 256;
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
                Log.w("MicRecorder", "Error reading voice audio", x);
            } finally {
                recorder.stop();
                recorder.release();
            }
        } catch (Exception e) {
            Log.w("MicRecorder", "Error initializing recorder");
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
