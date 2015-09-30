package com.squeaker.squeaker;

import android.os.Parcel;
import android.os.Parcelable;

public class Session implements Parcelable {
    private String sessionId;
    private UserMetadata user;

    public Session(String sessionId, UserMetadata user) {
        this.sessionId = sessionId;
        this.user = user;
    }

    protected Session(Parcel in) {
        sessionId = in.readString();
        user = in.readParcelable(UserMetadata.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sessionId);
        dest.writeParcelable(user, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };

    public String getId() {
        return this.sessionId;
    }

    public UserMetadata getUser() {
        return user;
    }
}
