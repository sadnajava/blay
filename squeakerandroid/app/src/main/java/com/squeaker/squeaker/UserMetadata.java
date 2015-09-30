package com.squeaker.squeaker;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class UserMetadata implements Parcelable {
    private String email;
    private String displayName;
    private int numSqueaks;

    public UserMetadata(String email, String displayName) {
        this.email = email;
        this.displayName = displayName;
        this.numSqueaks = -1;
    }

    public UserMetadata(String email, String displayName, int numSqueaks) {
        this.email = email;
        this.displayName = displayName;
        this.numSqueaks = numSqueaks;
    }

    protected UserMetadata(Parcel in) {
        email = in.readString();
        displayName = in.readString();
        numSqueaks = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(displayName);
        dest.writeInt(numSqueaks);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayString() {
        if (TextUtils.isEmpty(displayName) || displayName.equals("null")) {
            return email;
        }
        else {
            return displayName;
        }
    }

    public int getNumSqueaks() {
        return numSqueaks;
    }

}
