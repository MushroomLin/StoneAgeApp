package com.example.miniresearchdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.miniresearchdatabase.models.Offer;
import com.example.miniresearchdatabase.viewholder.OfferViewHolder;
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

public class MyOfferActivity extends AppCompatActivity {
    private Button button_back;
    private static final String TAG = "MyOfferActivity";
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Offer, OfferViewHolder> mAdapter;
    private RecyclerView mRecycler;

    private LinearLayoutManager mManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myoffer);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = findViewById(R.id.myoffersmessagesList);
        mRecycler.setHasFixedSize(true);

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        button_back = findViewById(R.id.button_back5);

        // Set up FirebaseRecyclerAdapter with the Query
        Query offersQuery = getQuery(mDatabase);

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyOfferActivity.this, MainActivity.class));
            }
        });

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Offer>()
                .setQuery(offersQuery, Offer.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Offer, OfferViewHolder>(options) {

            @Override
            public OfferViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new OfferViewHolder(inflater.inflate(R.layout.item_offer, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(OfferViewHolder viewHolder, int position, final Offer model) {
                final DatabaseReference offerRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = offerRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(MyOfferActivity.this, MyPostOfferActivity.class);
                        intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });

                viewHolder.bindToOffer(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Need to write to both places the post is stored
                        DatabaseReference globalPostRef = mDatabase.child("posts").child(offerRef.getKey());
                        DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(offerRef.getKey());

                        // Run two transactions

                    }
                });

            }
        };
        mRecycler.setAdapter(mAdapter);

    }

    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("user-offers")
                .child(getUid());
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
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

}

