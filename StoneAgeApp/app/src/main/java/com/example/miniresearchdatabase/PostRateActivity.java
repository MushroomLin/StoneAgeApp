package com.example.miniresearchdatabase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miniresearchdatabase.models.Offer;
import com.example.miniresearchdatabase.models.Post;
import com.example.miniresearchdatabase.models.User;
import com.example.miniresearchdatabase.ImageUtils;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostRateActivity extends AppCompatActivity {
    private Button button_facebook;
    private Button button_back;
    private RatingBar starBar;
    private TextView textView_rate;
    private Button button_submitrate;
    private String postpicture;
    private String offerpicture;
    private SharePhotoContent sharePhotoContent;
    private DatabaseReference mDatabase;
    private String finalPostKey;
    private String finalOfferKey;
    private Bitmap postpictureBitmap;
    private Bitmap offerpictureBitmap;
    private SharePhoto postPhoto;
    private SharePhoto offerPhoto;
    private float rate;
    private String offeruid;
    private float newrate;
    private int newTotalReview;


    public static final String FINAL_POST_KEY = "mPostkey";
    public static final String FINAL_OFFER_KEY = "offerkey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.setApplicationId("2376751019220967");
        //initialize Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }

        setContentView(R.layout.activity_postrate);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        button_facebook = findViewById(R.id.button_facebook);
        button_back = findViewById(R.id.button_back7);
        button_submitrate = findViewById(R.id.button_submitrate);
        starBar = findViewById(R.id.starBar);
        textView_rate = findViewById(R.id.textView_rate);
        finalPostKey = getIntent().getStringExtra(FINAL_POST_KEY);
        finalOfferKey = getIntent().getStringExtra(FINAL_OFFER_KEY);


        button_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog.show(PostRateActivity.this,sharePhotoContent);
            }
        });

        starBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rate = rating;
                textView_rate.setText(Float.toString(rate));
            }

        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostRateActivity.this, MainActivity.class));
            }
        });

        button_submitrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rateRef = mDatabase.child("post-offers").child(finalPostKey).child(finalOfferKey);
                rateRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Serialize retrieved data to a User object
                        Offer offer = dataSnapshot.getValue(Offer.class);
                        //Now you have an object of the User class and can use its getters like this
                        offeruid = String.valueOf(offer.uid);

                        DatabaseReference userRef = mDatabase.child("users").child(offeruid);
                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Serialize retrieved data to a User object
                                User user = dataSnapshot.getValue(User.class);
                                float userrate = (float)user.rate;
                                float totalrate = userrate * ((float)user.totalReview-1);
                                newrate = (totalrate + rate) / ((float)user.totalReview);
                                newTotalReview = user.totalReview;
                                mDatabase.child("users").child(offeruid).child("rate").setValue((double)newrate);
                                mDatabase.child("users").child(offeruid).child("totalReview").setValue(newTotalReview+1);

                                //Now you have an object of the User class and can use its getters like this

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                            }
                        });


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                    }
                });

            }
        });

        DatabaseReference ref = mDatabase.child("posts").child(finalPostKey);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Serialize retrieved data to a User object
                Post post = dataSnapshot.getValue(Post.class);
                //Now you have an object of the User class and can use its getters like this
                if (post != null) {
                    // Set the user profile picture
                    if (post.picture != null) {
                        postpicture = String.valueOf(post.picture);
                        postpictureBitmap = ImageUtils.stringToBitmap(postpicture);
                        postPhoto = new SharePhoto.Builder()
                                .setBitmap(postpictureBitmap)
                                .build();


                        DatabaseReference ref2 = mDatabase.child("post-offers").child(finalPostKey).child(finalOfferKey);
                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Serialize retrieved data to a User object
                                Offer offer = dataSnapshot.getValue(Offer.class);

                                //Now you have an object of the User class and can use its getters like this
                                if (offer != null) {
                                    // Set the user profile picture
                                    if (offer.picture != null) {
                                        offerpicture = String.valueOf(offer.picture);
                                        //Toast.makeText(PostRateActivity.this, offerpicture, Toast.LENGTH_SHORT).show();
                                        offerpictureBitmap = ImageUtils.stringToBitmap(offerpicture);
                                        offerPhoto = new SharePhoto.Builder()
                                                .setBitmap(offerpictureBitmap)
                                                .build();
                                        sharePhotoContent = new SharePhotoContent.Builder()
                                                .addPhoto(postPhoto)
                                                .addPhoto(offerPhoto)
                                                .build();

                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        });

    }
}

