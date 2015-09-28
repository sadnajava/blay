package com.squeaker.squeaker;

import android.os.Parcel;
import android.os.Parcelable;

public class Session implements Parcelable {
    private String sessionId;
    private String email;

    public Session(String sessionId, String email) {
        this.sessionId = sessionId;
        this.email = email;
    }

    protected Session(Parcel in) {
        sessionId = in.readString();
        email = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sessionId);
        dest.writeString(email);
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

    public String getEmail() {
        return email;
    }

}
