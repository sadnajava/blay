package com.squeaker.squeaker;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.util.Date;

/**
 * Helper class to manage recording audio.
 */
public class AudioPlayer extends Thread {
    private final byte[] audioData;

    /**
     * Give the thread high priority so that it's not canceled unexpectedly, and start it
     */
    public AudioPlayer(byte[] audioData) {
        this.audioData = audioData;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        start();
    }

    @Override
    public void run() {
        Log.i("AudioPlayer", "Running Audio Playback Thread");

        AudioTrack player = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, audioData.length, AudioTrack.MODE_STREAM);

        if (player.getState() == AudioTrack.STATE_UNINITIALIZED)
            return;

        player.play();
        player.write(audioData, 0, audioData.length);

        while (player.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            yield();
        }

        player.release();
    }
}
