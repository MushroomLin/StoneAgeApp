package com.example.miniresearchdatabase.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.miniresearchdatabase.MapsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.example.miniresearchdatabase.R;
import com.example.miniresearchdatabase.PostDetailActivity;
import com.example.miniresearchdatabase.models.Post;
import com.example.miniresearchdatabase.viewholder.PostViewHolder;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;


public abstract class PostListFragment extends Fragment {

    private static final String TAG = "PostListFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]


    private FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private double minPrice = 99999999.0;
    private double maxPrice = 0.0;
    private int recommendFlag = 0;
    private String searchText = "";

    private Button bt_search;
    private EditText et_searchText;
    private Button button_user;

    List<Double> avgPricesList = null;
    Query postsQuery;

    public PostListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);

        Log.e("ppp", String.valueOf(recommendFlag));
        Log.e("ppp", String.valueOf(minPrice));
        Log.e("ppp", String.valueOf(maxPrice));

        et_searchText = rootView.findViewById(R.id.et_searchText);

        ImageButton imageButton = rootView.findViewById(R.id.imageButton_recommend);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recommendFlag == 0 || recommendFlag == 2) {
                    recommendFlag = 1;
                    Toast.makeText(getActivity(),"Price Recommend Mode",Toast.LENGTH_SHORT).show();
                }
                else {
                    recommendFlag = 0;
                    Toast.makeText(getActivity(),"Regular Mode",Toast.LENGTH_SHORT).show();
                }
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(PostListFragment.this).attach(PostListFragment.this).commit();
                Log.e("ppp", String.valueOf(recommendFlag));
            }
        });

        bt_search = rootView.findViewById(R.id.bt_search);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText = et_searchText.getText().toString();
                recommendFlag = 2;
                Toast.makeText(getActivity(),"Searching...",Toast.LENGTH_SHORT).show();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(PostListFragment.this).attach(PostListFragment.this).commit();
            }
        });

        button_user = rootView.findViewById(R.id.button_user);
        button_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText = et_searchText.getText().toString();
                recommendFlag = 3;
                Toast.makeText(getActivity(),"Searching...",Toast.LENGTH_SHORT).show();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(PostListFragment.this).attach(PostListFragment.this).commit();
            }
        });

        final String userId = getUid();
        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // [END create_database_reference]
        DatabaseReference databaseReference = mDatabase.child("user-posts").child(userId);;
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
                    }
                }
                // if the range of minPrice and maxPrcie isn't big enough
                minPrice = Math.min(Math.max(minPrice - 20, 0.0), minPrice * 0.5);
                maxPrice = Math.max(maxPrice + 20.0, maxPrice * 1.5);
//                avgPricesList = avgPriceList;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRecycler = rootView.findViewById(R.id.messagesList);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        Log.e("pricequery", String.valueOf(minPrice)+" "+String.valueOf(maxPrice));

        // Set up FirebaseRecyclerAdapter with the Query

//        avgPricesList
        if (recommendFlag == 0) {
            postsQuery = getQuery(mDatabase);
        } else if(recommendFlag == 1) {
            postsQuery = getQueryFromPrice(mDatabase, minPrice, maxPrice);
        } else if(recommendFlag == 2) {
            if(searchText.equals("")) {
                postsQuery = getQuery(mDatabase);
            } else {
                postsQuery = getQueryBySearch(mDatabase, searchText);
            }
        } else if(recommendFlag == 3) {
            if(searchText.equals("")) {
                postsQuery = getQuery(mDatabase);
            } else {
                postsQuery = getQueryBySearchUser(mDatabase, searchText);
            }
        }

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(postsQuery, Post.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {

            @Override
            public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new PostViewHolder(inflater.inflate(R.layout.item_post, viewGroup, false), getContext());
            }

            @Override
            protected void onBindViewHolder(PostViewHolder viewHolder, int position, final Post model) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                        intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });

                // Determine if the current user has liked this post and set UI accordingly


                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Need to write to both places the post is stored
                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
                        DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());

                        // Run two transactions
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
        Log.e("pricequery", "finish create the start activity");
    }

    // [START post_stars_transaction]


    // [END post_stars_transaction]


    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
            Log.e("pricequery", "now is in Onstart");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract double[] getQuery3(DatabaseReference databaseReference);

    public abstract Query getQueryFromPrice(DatabaseReference databaseReference, double min, double max);

    public abstract Query getQueryBySearch(DatabaseReference databaseReference, String searchText);

    public abstract Query getQueryBySearchUser(DatabaseReference databaseReference, String searchText);

    public abstract Query getQuery(DatabaseReference databaseReference);
}
