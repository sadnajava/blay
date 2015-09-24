package com.squeaker.squeaker;

public class SqueakMetadata {
    String squeakId;
    String email;
    long duration;
    String date;
    String caption;

    public SqueakMetadata(String squeakId, String email, long duration,
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

    public long getDuration() {
        return duration;
    }

    public String getDate() {
        return date;
    }
}