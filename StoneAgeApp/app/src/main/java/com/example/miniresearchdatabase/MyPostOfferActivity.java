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
import com.example.miniresearchdatabase.viewholder.OfferViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MyPostOfferActivity extends AppCompatActivity {
    private Button button_back;
    private Button button_accept;
    private static final String TAG = "MyPostOfferActivity";
    public static final String EXTRA_POSTOFFER_POST_KEY = "post_key";
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Offer, OfferViewHolder> mOfferAdapter;
    private RecyclerView mOfferRecycler;
    private LinearLayoutManager mOfferManager;
    private String mPostKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypostoffer);

        mPostKey = getIntent().getStringExtra(EXTRA_POSTOFFER_POST_KEY);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mOfferRecycler = findViewById(R.id.mypostoffermessagesList);
        mOfferRecycler.setHasFixedSize(true);

        mOfferManager = new LinearLayoutManager(this);
        mOfferManager.setReverseLayout(true);
        mOfferManager.setStackFromEnd(true);
        mOfferRecycler.setLayoutManager(mOfferManager);
        button_back = findViewById(R.id.button_back4);
        button_accept = findViewById(R.id.button_accept);

        // Set up FirebaseRecyclerAdapter with the Query
        Query offersQuery = getPostOfferQuery(mDatabase);

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyPostOfferActivity.this, MyPostActivity.class));
            }
        });


        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Offer>()
                .setQuery(offersQuery, Offer.class)
                .build();

        mOfferAdapter = new FirebaseRecyclerAdapter<Offer, OfferViewHolder>(options) {

            @Override
            public OfferViewHolder onCreateViewHolder(ViewGroup viewGroup2, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup2.getContext());
                return new OfferViewHolder(inflater.inflate(R.layout.item_offer, viewGroup2, false));
            }

            @Override
            protected void onBindViewHolder(OfferViewHolder viewHolder3, int position, final Offer model) {
                //final DatabaseReference offerRef = getRef(position);

                // Set click listener for the whole post view
                //final String postKey = offerRef.getKey();
                viewHolder3.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(MyPostOfferActivity.this, MainActivity.class);
                        //intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });

                viewHolder3.bindToOffer(model);
            }
        };
        mOfferRecycler.setAdapter(mOfferAdapter);

    }
    public Query getPostOfferQuery(DatabaseReference databaseReference) {

        return databaseReference.child("post-offers")
                .child(mPostKey);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mOfferAdapter != null) {
            mOfferAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mOfferAdapter != null) {
            mOfferAdapter.stopListening();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}