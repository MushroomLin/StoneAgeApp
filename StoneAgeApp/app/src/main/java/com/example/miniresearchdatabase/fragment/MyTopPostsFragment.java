package com.example.miniresearchdatabase.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyTopPostsFragment extends PostListFragment {

    public MyTopPostsFragment() {}

    @Override
    public double[] getQuery3(DatabaseReference databaseReference) {
        return null;
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START my_top_posts_query]

        // My top posts by number of stars
        String myUserId = getUid();
        Query myTopPostsQuery = databaseReference.child("user-posts").child(myUserId)
                .orderByChild("starCount");
        // [END my_top_posts_query]

        return myTopPostsQuery;
    }


    @Override
    public Query getQueryBySearch(DatabaseReference databaseReference, String searchText){
        return null;
    }

    @Override
    public Query getQueryBySearchUser(DatabaseReference databaseReference, String searchText){
        return null;
    }

    @Override
    public Query getQueryFromPrice(DatabaseReference databaseReference, double minPrice, double maxPrice) {
        return null;
    }
}
