package com.squeaker.squeaker;

public class User {
    private String email;
    private int numSqueaks;

    public User(String email, int numSqueaks) {
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
