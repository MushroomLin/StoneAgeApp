package com.example.miniresearchdatabase.models;

import android.graphics.Bitmap;

import com.example.miniresearchdatabase.ImageUtils;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String address;
    public String phone;
    public String intro;
    public double rate;
    public String avatar;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("address", address);
        result.put("phone", phone);
        result.put("intro", intro);
        result.put("rate", rate);
        return result;
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
    public void setAvatar(String avatar){
        this.avatar = avatar;
    }
    public Bitmap getAvatar(){
        return ImageUtils.stringToBitmap(this.avatar);
    }
}

