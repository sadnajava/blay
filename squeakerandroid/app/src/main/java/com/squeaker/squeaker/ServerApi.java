package com.squeaker.squeaker;

import android.os.Parcel;
import android.os.Parcelable;

import com.squeaker.utils.AudioCodecSettings;

import java.util.ArrayList;

public interface ServerApi extends Parcelable {
    String login(String email, String password) throws Exception;
    ArrayList<SqueakMetadata> updateFeed() throws Exception;
    void broadcastSqueak(SqueakMetadata sm, byte[] squeakAudioData) throws Exception;
    ArrayList<UserMetadata> findUser(String searchValue) throws Exception;
    UserProfile getUserProfile(String email) throws Exception;
    void followUser(String email) throws Exception;
    void unfollowUser(String email) throws Exception;
    byte[] getSqueakAudio(String squeakId) throws Exception;
    void deleteSqueak(String squeakId) throws Exception;
    AudioCodecSettings getCodecSettings();

    Creator<ServerApi> CREATOR = new Creator<ServerApi>() {
        @Override
        public ServerApi createFromParcel(Parcel in) {
            return new JsonServerApi(in);
        }

        @Override
        public ServerApi[] newArray(int size) {
            return new ServerApi[size];
        }
    };

    @Override
    int describeContents();

    @Override
    void writeToParcel(Parcel dest, int flags);
}
