package com.example.miniresearchdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.miniresearchdatabase.models.Offer;
import com.example.miniresearchdatabase.viewholder.PostOfferViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MyPostOfferActivity extends AppCompatActivity {
    private Button button_back;
    private static final String TAG = "MyPostOfferActivity";
    public static final String EXTRA_POSTOFFER_POST_KEY = "post_key";
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Offer, PostOfferViewHolder> mPostOfferAdapter;
    private RecyclerView mPostOfferRecycler;
    private LinearLayoutManager mPostOfferManager;
    private String mPostKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypostoffer);

        mPostKey = getIntent().getStringExtra(EXTRA_POSTOFFER_POST_KEY);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPostOfferRecycler = findViewById(R.id.mypostoffermessagesList);
        mPostOfferRecycler.setHasFixedSize(true);

        mPostOfferManager = new LinearLayoutManager(this);
        mPostOfferManager.setReverseLayout(true);
        mPostOfferManager.setStackFromEnd(true);
        mPostOfferRecycler.setLayoutManager(mPostOfferManager);
        button_back = findViewById(R.id.button_back4);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postoffersQuery = getPostOfferQuery(mDatabase);

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyPostOfferActivity.this, MyPostActivity.class));
            }
        });


        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Offer>()
                .setQuery(postoffersQuery, Offer.class)
                .build();

        mPostOfferAdapter = new FirebaseRecyclerAdapter<Offer, PostOfferViewHolder>(options) {

            @Override
            public PostOfferViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new PostOfferViewHolder(inflater.inflate(R.layout.item_acceptable_offer, viewGroup, false), getApplicationContext());
            }

            @Override
            protected void onBindViewHolder(PostOfferViewHolder viewHolder3, int position, final Offer model) {
                final DatabaseReference offerRef = getRef(position);

                // Set click listener for the whole post view
                final String offerKey = offerRef.getKey();

                viewHolder3.bindToPostOffer(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View button_accept) {
                        Intent intent = new Intent(MyPostOfferActivity.this, PostRateActivity.class);
                        intent.putExtra(PostRateActivity.FINAL_POST_KEY, mPostKey);
                        intent.putExtra(PostRateActivity.FINAL_OFFER_KEY, offerKey);
                        startActivity(intent);
                    }
                });
            }
        };
        mPostOfferRecycler.setAdapter(mPostOfferAdapter);

    }
    public Query getPostOfferQuery(DatabaseReference databaseReference) {

        return databaseReference.child("post-offers")
                .child(mPostKey);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mPostOfferAdapter != null) {
            mPostOfferAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mPostOfferAdapter != null) {
            mPostOfferAdapter.stopListening();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}