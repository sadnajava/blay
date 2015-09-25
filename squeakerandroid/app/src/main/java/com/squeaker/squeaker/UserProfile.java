package com.squeaker.squeaker;

import java.util.ArrayList;

public class UserProfile {
    private String email;
    private ArrayList<SqueakMetadata> squeaks;
    private ArrayList<UserMetadata> followingUsers;
    private boolean isFollowing;

    public UserProfile(String email, ArrayList<SqueakMetadata> squeaks, ArrayList<UserMetadata> followingUsers, boolean isFollowing) {
        this.email = email;
        this.squeaks = squeaks;
        this.followingUsers = followingUsers;
        this.isFollowing = isFollowing;
    }

    public String getEmail() {
        return email;
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
}
