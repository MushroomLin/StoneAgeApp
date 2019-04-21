package com.example.miniresearchdatabase;


import android.util.Log;


import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

//import okhttp3.OkHttpClient;

//import okhttp3.OkHttpClient;

//import okhttp3.OkHttpClient;
//import okhttp3.Response;
//import okhttp3.Request;


public class AddressToLocation {
    public double lat;
    public double lng;

    public AddressToLocation() {}


//    public double[] getLocation(String address) throws JSONException {
//        // Geocoding API: https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=YOUR_API_KEY
//        String basicUrl1 = "https://maps.googleapis.com/maps/api/geocode/json?address=";
//        String basicUrl2 = "&key=AIzaSyCaUcdLLm4ifpW9ZYMhcCm_6RMvArAz-hA";
//        String url = basicUrl1 + address.replace(' ', '+') + basicUrl2;
//        Log.e("123", url);
//        double[] location = new double[2];
//
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//        Response responses;
//        String jsonResponse;
//
//        try {
//            responses = client.newCall(request).execute();
//            jsonResponse = responses.body().string();
//            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//            System.out.println(jsonResponse);
//            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//        } catch (IOException e) {
//            Log.w("loc", "not get jsonResponse");
//            jsonResponse = null;
//            e.printStackTrace();
//        }
//        if (jsonResponse != null) {
//            JSONObject obj = new JSONObject(jsonResponse);
//            obj = obj.getJSONObject("location");
//            Log.w("loc", obj.toString());
//            location[0] = obj.getDouble("lat");
//            location[1] = obj.getDouble("lng");
//            Log.w("loc", String.valueOf(location[0]));
//            Log.w("loc", String.valueOf(location[1]));
//
//            return location;
//        }
//        else
//            return null;
//    }


    // this method will return the location by parsing the address
    // address pattern: "1600 Amphitheatre Parkway Mountain View, CA 94043"
    // return double[2] double[0]=latitude  double[1]=longitude
    public double[] getLocation(String address) throws InterruptedException, ApiException, IOException {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyCaUcdLLm4ifpW9ZYMhcCm_6RMvArAz-hA")
                .build();
        double[] loc = new double[2];
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context,
                    address).await();
            double lat = results[0].geometry.location.lat;
            double lng = results[0].geometry.location.lng;
            loc[0] = lat;
            loc[1] = lng;

            this.lat = lat;
            this.lng = lng;

            return loc;
        } catch (Exception e) {
            Log.e("AddressToLocation", e.toString());
            this.lng = -999999.99999;
            this.lat = -999999.99999;
            loc[0] = -999999.9999;
            loc[1] = -999999.9999;
            return loc;
        }
    }

//    public static void main(String[] arsg) throws InterruptedException, ApiException, IOException {
////        double[] a = new AddressToLocation().getLocation("42 Gardner St, MA 02134");
//        double[] a = new AddressToLocation().getLocation("42 Gardner St, MA 02134");
//        System.out.println(a[0]);
//        System.out.println(a[1]);
//
//    }

}
