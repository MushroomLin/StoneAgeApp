package com.example.miniresearchdatabase.models;


import android.graphics.Bitmap;

import com.example.miniresearchdatabase.ImageUtils;
import com.google.firebase.database.IgnoreExtraProperties;

//a class for load message from database to mobile device and save and push the message to the database.
@IgnoreExtraProperties
public class Message {
    public String sender;
    public String receiver;
    public String message;
    public String time;
    public String image;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Message(String sender, String receiver, String message, String time, String image) {
        // it has sender and receiver as the message's sender and receiver. And time for then message when it is sent,
        // image for users who send would like to send image to others.
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.time = time;
        this.image = image;
    }
    public Bitmap getAvatar(){
        return ImageUtils.stringToBitmap(this.image);
    }
}
