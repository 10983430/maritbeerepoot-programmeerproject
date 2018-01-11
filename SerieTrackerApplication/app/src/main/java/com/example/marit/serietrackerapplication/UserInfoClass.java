package com.example.marit.serietrackerapplication;

import java.util.HashMap;

/**
 * Contains the user information
 */
public class UserInfoClass {
    public String id;
    public String username;
    public HashMap followseries;
    public HashMap followusers;
    public String email;

    /**
     * Constructs the class
     */
    public UserInfoClass(String id, String username, HashMap followseries, HashMap followusers, String email) {
        this.username = username;
        this.followseries = followseries;
        this.followusers = followusers;
        this.email = email;
        this.id = id;
    }

    /**
     * Constructs the class with an default constructor (for getting information from Firebase)
     */
    public UserInfoClass() {

    }
}
