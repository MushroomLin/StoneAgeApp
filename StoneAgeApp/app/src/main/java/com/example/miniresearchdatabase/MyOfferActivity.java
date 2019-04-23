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
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
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
import com.google.firebase.database.ValueEventListener;

public class MyOfferActivity extends AppCompatActivity {
    private Button button_back;
    private static final String TAG = "MyOfferActivity";
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Offer, OfferViewHolder> mOfferAdapter;
    private RecyclerView mOfferRecycler;

    private LinearLayoutManager mOfferManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myoffer);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mOfferRecycler = findViewById(R.id.myoffersmessagesList);
        mOfferRecycler.setHasFixedSize(true);

        mOfferManager = new LinearLayoutManager(this);
        mOfferManager.setReverseLayout(true);
        mOfferManager.setStackFromEnd(true);
        mOfferRecycler.setLayoutManager(mOfferManager);
        button_back = findViewById(R.id.button_back5);

        // Set up FirebaseRecyclerAdapter with the Query
        Query offersQuery = getOfferQuery(mDatabase);

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Offer>()
                .setQuery(offersQuery, Offer.class)
                .build();

        mOfferAdapter = new FirebaseRecyclerAdapter<Offer, OfferViewHolder>(options) {

            @Override
            public OfferViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new OfferViewHolder(inflater.inflate(R.layout.item_offer, viewGroup, false), getApplicationContext());
            }

            @Override
            protected void onBindViewHolder(OfferViewHolder viewHolder2, int position, final Offer model) {
                final DatabaseReference offerRef = getRef(position);

                // Set click listener for the whole post view
                final String offerKey = offerRef.getKey();

                viewHolder2.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        DatabaseReference ref3 = mDatabase.child("user-offers").child(getUid()).child(offerKey);
                        ref3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Serialize retrieved data to a User object
                                Offer offer = dataSnapshot.getValue(Offer.class);
                                //Now you have an object of the User class and can use its getters like this
                                if (offer != null) {
                                    // Set the user profile picture
                                        String mPostKey = String.valueOf(offer.postid);
                                        Intent intent = new Intent(MyOfferActivity.this, PostRateActivity.class);
                                        intent.putExtra(PostRateActivity.FINAL_POST_KEY, mPostKey);
                                        intent.putExtra(PostRateActivity.FINAL_OFFER_KEY, offerKey);
                                        startActivity(intent);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                            }
                        });
                    }
                });

                viewHolder2.bindToOffer(model);
            }
        };
        mOfferRecycler.setAdapter(mOfferAdapter);

    }

    public Query getOfferQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("user-offers")
                .child(getUid());

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

