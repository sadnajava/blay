package com.squeaker.squeaker;

import android.media.AudioFormat;

import com.squeaker.utils.AudioCodecSettings;

public class SqueakerCodecSettings implements AudioCodecSettings {
    @Override
    public int getSampleRate() {
        return 8000;
    }

    @Override
    public int getBitDepth() {
        return AudioFormat.ENCODING_PCM_16BIT;
    }
}
