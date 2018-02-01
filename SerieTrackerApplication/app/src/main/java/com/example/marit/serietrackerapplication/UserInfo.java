package com.example.marit.serietrackerapplication;

import java.util.HashMap;

/**
 * Contains the user information
 */
public class UserInfo {
    public String id;
    public String username;
    public String email;

    /**
     * Constructs the class
     */
    public UserInfo (String id, String username, String email) {
        this.username = username;
        this.email = email;
        this.id = id;
    }

    /**
     * Constructs the class with an default constructor (for getting information from Firebase)
     */
    public UserInfo() {

    }
}