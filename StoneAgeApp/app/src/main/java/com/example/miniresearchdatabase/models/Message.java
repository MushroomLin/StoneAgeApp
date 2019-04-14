package com.example.miniresearchdatabase.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class Message {
    public String uid;
    public String sender;
    public String receiver;
    public  String message;
    public Message() {};

    public Message(String uid, String sender, String receiver, String message) {
        this.uid = uid;
        this.receiver = receiver;
        this.sender = sender;
        this.message = message;
    }

    // post_to_map
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("receiver", receiver);
        result.put("sender", sender);
        result.put("message", message);
        return result;
    }
}

