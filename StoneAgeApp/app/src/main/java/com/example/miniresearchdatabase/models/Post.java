package com.example.miniresearchdatabase.models;

import com.example.miniresearchdatabase.AddressToLocation;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@IgnoreExtraProperties
public class Post {

    public String uid;
    public String author;

    public String title;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();
    public double latitude;
    public double longitude;
    public String address;

    public String description;
    public String originalType;
    public String targetType;
    public String picture;

    public List<Double> estimatedPrices;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }


    public Post(String uid, String author, String title,
                String address,
                String description, String originalType, String targetType, String picture,
                List<Double> estimatedPrices) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.address = address;

        // TO_BE_FIXED
        double[] coordinates = null;
//        double[] coordinates = getCoordinates(address);
        if(coordinates == null) {
            coordinates = new double[]{0.0, 0.0};
        }

        this.latitude = coordinates[0];
        this.longitude = coordinates[1];

        this.description = description;
        this.originalType = originalType;
        this.targetType = targetType;
        this.picture = picture;

        this.estimatedPrices = estimatedPrices;
    }

    // post_to_map
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("address", address);
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        result.put("description", description);
        result.put("originalType", originalType);
        result.put("targetType", targetType);
        result.put("picture", picture);

        result.put("estimatedPrices", estimatedPrices);

        return result;
    }

    public double[] getCoordinates(String address) {
        double[] result = null;
        try{
            result = new AddressToLocation().getLocation(address);
        } catch(Exception e){

        }
        return result;
    }
}

