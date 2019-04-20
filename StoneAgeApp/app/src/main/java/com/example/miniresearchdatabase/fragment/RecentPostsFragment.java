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
import java.util.Map;

public class RecentPostsFragment extends PostListFragment {

    public RecentPostsFragment() {}

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
