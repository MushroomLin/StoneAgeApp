package com.example.miniresearchdatabase;


import android.util.Log;


import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;


import java.io.IOException;



public class AddressToLocation {
    public double lat;
    public double lng;

    public AddressToLocation() {}


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
//        double[] a = new AddressToLocation().getLocation("42 Gardner St, MA 02134");
//        System.out.println(a[0]);
//        System.out.println(a[1]);
//
//    }

}
