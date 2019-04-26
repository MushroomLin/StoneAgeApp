package com.example.miniresearchdatabase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.miniresearchdatabase.models.Offer;
import com.example.miniresearchdatabase.models.Post;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
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
        finalPostKey = getIntent().getStringExtra(FINAL_POST_KEY2);
        finalOfferKey = getIntent().getStringExtra(FINAL_OFFER_KEY2);



        button_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog.show(OfferRateActivity.this,sharePhotoContent);
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OfferRateActivity.this, MainActivity.class));
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

