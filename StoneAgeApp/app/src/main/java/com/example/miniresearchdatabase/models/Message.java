package com.example.miniresearchdatabase.models;


import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class Message {

    public String sender;
    public String receiver;
    public String message;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Message(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

}
