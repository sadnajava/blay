package com.squeaker.squeaker;

public class UserMetadata {
    private String email;
    private int numSqueaks;

    public UserMetadata(String email, int numSqueaks) {
        this.email = email;
        this.numSqueaks = numSqueaks;
    }

    public String getEmail() {
        return email;
    }

    public int getNumSqueaks() {
        return numSqueaks;
    }
}
