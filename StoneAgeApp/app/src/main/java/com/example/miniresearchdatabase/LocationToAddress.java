package com.example.miniresearchdatabase;

import android.util.Log;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;


public class LocationToAddress {

    LocationToAddress() {}

    public String getAddress(double lat, double lng) {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyCaUcdLLm4ifpW9ZYMhcCm_6RMvArAz-hA")
                .build();
        String address;
        LatLng loc = new LatLng(lat, lng);
        try {
            GeocodingResult[] results = GeocodingApi.reverseGeocode(context, loc).await();
            address = results[0].formattedAddress;
        } catch (Exception e) {
            Log.e("locationToAddress", e.toString());
            address = "";
        }
        return address;
    }


//    public static void main(String[] args) {
//        String a = new com.example.miniresearchdatabase.LocationToAddress().getAddress(new LatLng(40.714224,-73.961452));
//        System.out.println(a);
//    }
}
