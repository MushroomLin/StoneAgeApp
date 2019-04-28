package com.example.miniresearchdatabase.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Offer {
    public String uid;
    public String author;
    public String title;
    public String description;
    public String address;
    public String picture;
    public String postid;
    public String status;


    public Offer() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Offer(String uid, String author, String title, String description, String address, String picture, String postid, String status) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.address = address;
        this.description = description;
        this.picture = picture;
        this.postid = postid;
        this.status = status;
    }

    // post_to_map
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("description", description);
        result.put("picture", picture);
        result.put("address", address);
        result.put("postid", postid);
        result.put("status", status);


        return result;
    }

}
