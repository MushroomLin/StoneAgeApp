package com.example.miniresearchdatabase.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyPostsFragment extends PostListFragment {

    public MyPostsFragment() {}

    @Override
    public Query getQueryBySearch(DatabaseReference databaseReference, String searchText){
        return null;
    }

    @Override
    public Query getQueryBySearchUser(DatabaseReference databaseReference, String searchText){
        return null;
    }

    @Override
    public double[] getQuery3(DatabaseReference databaseReference) {
        return null;
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {

        // All my posts
        return databaseReference.child("user-posts")
                .child(getUid());
    }

    @Override
    public Query getQueryFromPrice(DatabaseReference databaseReference, double minPrice, double maxPrice) {
        return null;
    }
}
