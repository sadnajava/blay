package com.squeaker.squeaker;

import android.view.View;

public class FetchPlaySqueakOnClickListener implements View.OnClickListener {
    private final SessionId sid;
    private final String squeakId;

    public FetchPlaySqueakOnClickListener(SessionId sid, String squeakId) {
        this.sid = sid;
        this.squeakId = squeakId;
    }

    @Override
    public void onClick(View v) {
        try {
            byte[] audioData = JsonApi.getSqueakAudio(sid, squeakId);
            AudioPlayer player = new AudioPlayer(audioData);
        } catch (Exception e) {
        }
    }
}
