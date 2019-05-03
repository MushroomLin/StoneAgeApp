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

    // query the items in collection "posts" by limiting the max price and the min price of items
    @Override
    public Query getQueryFromPrice(DatabaseReference databaseReference, double min, double max) {
        Query minMaxPricePostsQuery = databaseReference.child("posts").orderByChild("avgPrice").startAt(min).endAt(max);
        return minMaxPricePostsQuery;
    }

    // query the items in collection "posts" by titles of items
    @Override
    public Query getQueryBySearch(DatabaseReference databaseReference, String searchText) {
        Query minMaxPricePostsQuery = databaseReference.child("posts").orderByChild("title")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff");
        return minMaxPricePostsQuery;
    }

    // query the items in collection "posts" by names of owners
    @Override
    public Query getQueryBySearchUser(DatabaseReference databaseReference, String searchText) {
        Query minMaxPricePostsQuery = databaseReference.child("posts").orderByChild("author")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff");
        return minMaxPricePostsQuery;
    }

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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return recentPostsQuery;
    }
}
