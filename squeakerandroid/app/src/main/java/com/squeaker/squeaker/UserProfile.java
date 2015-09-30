package com.squeaker.squeaker;

import java.util.ArrayList;

public class UserProfile {
    private UserMetadata userMetadata;
    private ArrayList<SqueakMetadata> squeaks;
    private ArrayList<UserMetadata> followingUsers;
    private boolean isFollowing;

    public UserProfile(UserMetadata userMetadata, ArrayList<SqueakMetadata> squeaks, ArrayList<UserMetadata> followingUsers, boolean isFollowing) {
        this.userMetadata = userMetadata;
        this.squeaks = squeaks;
        this.followingUsers = followingUsers;
        this.isFollowing = isFollowing;
    }

    public ArrayList<SqueakMetadata> getSqueaks() {
        return squeaks;
    }

    public ArrayList<UserMetadata> getFollowingUsers() {
        return followingUsers;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public UserMetadata getMetadata() {
        return userMetadata;
    }
}
