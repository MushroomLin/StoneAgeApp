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


    public double latitude;
    public double longitude;
    public String address;

    public String description;
    public String originalType;
    public String targetType;
    public String picture;

    public List<Double> estimatedPrices;
    public double avgPrice;
    public String status;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }


    public Post(String uid, String author, String title,
                String address,
                String description, String originalType, String targetType, String picture,
                List<Double> estimatedPrices, double avgPrice, String status) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.address = address;
        this.status = status;

        // TO_BE_FIXED
//        double[] coordinates = null;
        double[] coordinates = getCoordinates(address);
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
        this.avgPrice = avgPrice;
    }

    // post_to_map
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("address", address);
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        result.put("description", description);
        result.put("originalType", originalType);
        result.put("targetType", targetType);
        result.put("picture", picture);

        result.put("estimatedPrices", estimatedPrices);
        result.put("avgPrice", avgPrice);
        result.put("status", status);

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

