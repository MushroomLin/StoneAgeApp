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

    public User(String username, String email, String address, String phone, String intro, double rate, String avatar) {
        this.username = username;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.intro = intro;
        this.rate = rate;
        this.avatar = avatar;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("email", email);
        result.put("address", address);
        result.put("phone", phone);
        result.put("intro", intro);
        result.put("rate", rate);
        result.put("avatar", avatar);

        return result;
    }
    public Bitmap getAvatar(){
        return ImageUtils.stringToBitmap(this.avatar);
    }
}

