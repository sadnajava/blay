package com.example.charmander.test;

public class SqueakMetadata {
    String squeakId;
    String email;
    int duration;
    String date;
    String caption;

    public SqueakMetadata(String squeakId, String email, int duration,
                            String date, String caption) {
        this.squeakId = squeakId;
        this.email = email;
        this.duration = duration;
        this.date = date;
        this.caption = caption == null ? "" : caption;
    }

    public String getSqueakId() {
        return squeakId;
    }

    public String getEmail() {
        return email;
    }

    public int getDuration() {
        return duration;
    }

    public String getDate() {
        return date;
    }
}