package com.example.miniresearchdatabase.models;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String address = "null";
    public String phone = "null";
    public String intro = "null";
    public double rate = 5.0;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public void setUsername(String username){
        this.username = username;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public void setIntro(String intro){
        this.intro = intro;
    }
    public void setRate(double rate){
        this.rate = rate;
    }

}

