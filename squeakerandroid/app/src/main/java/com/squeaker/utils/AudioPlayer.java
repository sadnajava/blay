package com.squeaker.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Helper class to manage recording audio.
 */
public class AudioPlayer extends Thread {
    private final AudioCodecSettings codecSettings;
    private final byte[] audioData;

    /**
     * Give the thread high priority so that it's not canceled unexpectedly, and start it
     */
    public AudioPlayer(AudioCodecSettings codecSettings, byte[] audioData) {
        this.codecSettings = codecSettings;
        this.audioData = audioData;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        start();
    }

    @Override
    public void run() {
        Log.i("AudioPlayer", "Running Audio Playback Thread");

        AudioTrack player = getAudioTrack();

        if (player.getState() == AudioTrack.STATE_UNINITIALIZED)
            return;

        player.play();
        player.write(audioData, 0, audioData.length);

        while (player.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            yield();
        }

        player.release();
    }

    @NonNull
    private AudioTrack getAudioTrack() {
        return new AudioTrack(AudioManager.STREAM_MUSIC, codecSettings.getSampleRate(), AudioFormat.CHANNEL_OUT_MONO, codecSettings.getBitDepth(), audioData.length, AudioTrack.MODE_STREAM);
    }
}
