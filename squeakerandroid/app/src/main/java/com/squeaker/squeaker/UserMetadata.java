package com.squeaker.squeaker;

import android.os.Parcel;
import android.os.Parcelable;

public class UserMetadata implements Parcelable {
    private String email;
    private int numSqueaks;

    public UserMetadata(String email, int numSqueaks) {
        this.email = email;
        this.numSqueaks = numSqueaks;
    }

    protected UserMetadata(Parcel in) {
        email = in.readString();
        numSqueaks = in.readInt();
    }

    public static final Creator<UserMetadata> CREATOR = new Creator<UserMetadata>() {
        @Override
        public UserMetadata createFromParcel(Parcel in) {
            return new UserMetadata(in);
        }

        @Override
        public UserMetadata[] newArray(int size) {
            return new UserMetadata[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public int getNumSqueaks() {
        return numSqueaks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeInt(numSqueaks);
    }
}
