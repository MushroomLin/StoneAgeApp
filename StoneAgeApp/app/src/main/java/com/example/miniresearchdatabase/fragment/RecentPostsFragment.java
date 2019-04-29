package com.example.miniresearchdatabase.fragment;

import android.support.annotation.NonNull;
import android.util.Log;
import com.example.miniresearchdatabase.models.Post;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RecentPostsFragment extends PostListFragment {
    double minPrice = 999999.99;
    double maxPrice = 0.0;
    public RecentPostsFragment() {}

    @Override
    public double[] getQuery3(DatabaseReference databaseReference) {
        final String userId = getUid();
        databaseReference = databaseReference.child("user-posts").child(userId);
        final double[] res = new double[2];
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Double> avgPriceList = new LinkedList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    List<Double> pricesList = post.estimatedPrices;
                    if(pricesList != null) {
                        double priceSum = 0.0;
                        for(int i = 0; i < pricesList.size(); i++) {
                            double curPrice = pricesList.get(i);
                            priceSum += curPrice;
                        }
                        double priceAvg = priceSum / pricesList.size();
                        // update minPrice and maxPrice
                        if (priceAvg < minPrice)
                            minPrice = priceAvg;
                        if (priceAvg > maxPrice)
                            maxPrice = priceAvg;
                        avgPriceList.add(priceAvg);
                        Log.e("pricequery", String.valueOf(priceAvg));
                        Log.e("pricequery", "minPrice:"+String.valueOf(minPrice)+" maxPrice:"+String.valueOf(maxPrice));
                        Log.e("pricequery", "--------------------------");
                    }
                }
                // if the range of minPrice and maxPrcie isn't big enough
                if (minPrice - 20.0 > 0.0)
                    minPrice = minPrice - 20.0;
                else
                    minPrice = 0.0;
                maxPrice = maxPrice + 20.0;
//                avgPricesList = avgPriceList;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.e("pricequery", String.valueOf(minPrice)+" "+String.valueOf(maxPrice));

        return null;
    }


    @Override
    public Query getQueryFromPrice(DatabaseReference databaseReference, double min, double max) {
        Log.e("pricequery", "min:"+String.valueOf(min)+" max:"+String.valueOf(max));
        Query minMaxPricePostsQuery = databaseReference.child("posts").orderByChild("avgPrice").startAt(min).endAt(max);

        return minMaxPricePostsQuery;
    }

    @Override
    public Query getQueryBySearch(DatabaseReference databaseReference, String searchText) {
        Query minMaxPricePostsQuery = databaseReference.child("posts").orderByChild("title")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff");

//        Log.e("getQueryBySearch", minMaxPricePostsQuery.);
        return minMaxPricePostsQuery;
    }

    @Override
    public Query getQueryBySearchUser(DatabaseReference databaseReference, String searchText) {
        Query minMaxPricePostsQuery = databaseReference.child("posts").orderByChild("author")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff");
//        Log.e("getQueryBySearch", minMaxPricePostsQuery.);
        return minMaxPricePostsQuery;
    }

//    @Override
//    public Query getQueryFromPrice(DatabaseReference databaseReference, double minPrice, double maxPrice) {
//
//        Log.e("pricequery", "minPrice:"+String.valueOf(minPrice)+" maxPrice:"+String.valueOf(maxPrice));
//        Query minMaxPricePostsQuery = databaseReference.child("posts").orderByChild("avgPrice").startAt(minPrice).endAt(maxPrice);
//
//        return minMaxPricePostsQuery;
//    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START recent_posts_query]

        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys

        Query recentPostsQuery = databaseReference.child("posts").limitToFirst(100);

        final String userId = getUid();
        DatabaseReference databaseReference2 = databaseReference.child("user-posts").child(userId);

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);

//                    // get the key of current post and store for future use
//                    DatabaseReference marker2postReference = postSnapshot.getRef();
//                    final String postkey = marker2postReference.getKey();
//                    // get current position for distance computing
//                    double postLatitude = post.latitude;
//                    double postLongitude = post.longitude;
//                    if (!marker2post.containsValue(postkey)) {
//                        LatLng postPoint = new LatLng(postLatitude, postLongitude);
//                        options.position(postPoint);
//                        options.title(post.title);
//                        options.snippet("Author: " + post.author + " Address: " + post.address
//                                + " " + post.body);
//                        Marker mId = mMap.addMarker(options);
//                        // store marker id and post key for future use
//                        Log.w("marker", mId.getId() + " " + postkey);
//                        marker2post.put(mId, postkey);
//                        post2marker.put(postkey, mId);
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return recentPostsQuery;
    }



}
