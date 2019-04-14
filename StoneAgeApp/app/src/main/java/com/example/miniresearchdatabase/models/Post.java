package com.example.miniresearchdatabase.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class Post {

    public String uid;
    public String author;
    public String title;
    public String body;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();
    public double latitude;
    public double longitude;
    public String address;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }


    public Post(String uid, String author, String title, String body,
                String address, String latitude, String longitude) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.address = address;
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
    }

    // post_to_map
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("address", address);
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        return result;
    }
}

