package com.example.miniresearchdatabase;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OfferRateActivity extends AppCompatActivity {
    private Button button_facebook;
    private Button button_back;
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
    private RatingBar starBar;
    private TextView textView_rate;
    private TextView textView_rateother;
    private Button button_submitrate;
    private float starRate = 5;
    private String postuid;
    private float newrate;
    private int newTotalReview;
    private String postUserName;

    public static final String FINAL_POST_KEY2 = "mPostkey";
    public static final String FINAL_OFFER_KEY2 = "offerkey";


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

        setContentView(R.layout.activity_offerrate);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        button_facebook = findViewById(R.id.button_facebook2);
        button_back = findViewById(R.id.button_back8);
        button_submitrate = findViewById(R.id.button_submitrate2);
        starBar = findViewById(R.id.starBar2);
        textView_rate = findViewById(R.id.textView_rate2);
        textView_rateother = findViewById(R.id.textView_rateother2);
        finalPostKey = getIntent().getStringExtra(FINAL_POST_KEY2);
        finalOfferKey = getIntent().getStringExtra(FINAL_OFFER_KEY2);
        textView_rate.setText("5.0/5.0");



        button_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog.show(OfferRateActivity.this,sharePhotoContent);
            }
        });

        starBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar2, float rating2, boolean fromUser2) {
                starRate = rating2;
                textView_rate.setText(Float.toString(starRate)+ "/5.0");
                DatabaseReference rateRef2 = mDatabase.child("posts").child(finalPostKey);
                rateRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Serialize retrieved data to a User object
                        Post post = dataSnapshot.getValue(Post.class);
                        //Now you have an object of the User class and can use its getters like this
                        postuid = String.valueOf(post.uid);
                        postUserName = String.valueOf(post.author);
                        textView_rateother.setText("Please Rate Your Trade with "+postUserName);

                        DatabaseReference userRef2 = mDatabase.child("users").child(postuid);
                        userRef2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Serialize retrieved data to a User object
                                User user = dataSnapshot.getValue(User.class);
                                float userrate = (float)user.rate;
                                float totalrate = userrate * ((float)user.totalReview);
                                newrate = (totalrate + starRate) / ((float)user.totalReview+1);
                                newTotalReview = user.totalReview;

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

        DatabaseReference rateRef2 = mDatabase.child("posts").child(finalPostKey);
        rateRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Serialize retrieved data to a User object
                Post post = dataSnapshot.getValue(Post.class);
                //Now you have an object of the User class and can use its getters like this
                postuid = String.valueOf(post.uid);
                postUserName = String.valueOf(post.author);
                textView_rateother.setText("Please Rate Your Trade with "+postUserName);

                DatabaseReference userRef2 = mDatabase.child("users").child(postuid);
                userRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Serialize retrieved data to a User object
                        User user = dataSnapshot.getValue(User.class);
                        float userrate = (float)user.rate;
                        float totalrate = userrate * ((float)user.totalReview);
                        newrate = (totalrate + starRate) / ((float)user.totalReview+1);
                        newTotalReview = user.totalReview;

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

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OfferRateActivity.this, MyOfferActivity.class));
            }
        });

        button_submitrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("users").child(postuid).child("rate").setValue((double)newrate);
                mDatabase.child("users").child(postuid).child("totalReview").setValue(newTotalReview+1);
                mDatabase.child("user-offers").child(getUid()).child(finalOfferKey).child("status").setValue("closed");
                mDatabase.child("post-offers").child(finalPostKey).child(finalOfferKey).child("status").setValue("closed");
                Toast.makeText(OfferRateActivity.this, "Sending Review", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(OfferRateActivity.this, MyOfferActivity.class);
                startActivity(intent);
            }
        });

        DatabaseReference refOffer = mDatabase.child("user-offers").child(getUid()).child(finalOfferKey);
        refOffer.addValueEventListener(new ValueEventListener() {
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


                        DatabaseReference refPost = mDatabase.child("posts").child(finalPostKey);
                        refPost.addValueEventListener(new ValueEventListener() {
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
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}

