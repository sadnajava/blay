package com.squeaker.squeaker;

public class SqueakMetadata {
    String squeakId;
    UserMetadata user;
    long duration;
    String date;
    String caption;

    public SqueakMetadata(String squeakId, UserMetadata user, long duration,
                          String date, String caption) {
        this.squeakId = squeakId;
        this.user = user;
        this.duration = duration;
        this.date = date;
        this.caption = caption == null ? "" : caption;
    }

    public String getSqueakId() {
        return squeakId;
    }

    public UserMetadata getUser() {
        return user;
    }

    public long getDuration() {
        return duration;
    }

    public String getDate() {
        return date;
    }

    public String getCaption() {
        return caption;
    }
}