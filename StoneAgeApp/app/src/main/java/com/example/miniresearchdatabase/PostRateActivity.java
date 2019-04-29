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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PostRateActivity extends AppCompatActivity {
    private Button button_facebook;
    private Button button_back;
    private RatingBar starBar;
    private TextView textView_rate;
    private TextView textView_rateother;
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
    private float starRate = 5;
    private String offeruid;
    private String offerTitle;
    private float newrate;
    private int newTotalReview;
    private String offerUserName;


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
        textView_rateother = findViewById(R.id.textView_rateother);
        finalPostKey = getIntent().getStringExtra(FINAL_POST_KEY);
        finalOfferKey = getIntent().getStringExtra(FINAL_OFFER_KEY);
        textView_rate.setText("5.0/5.0");


        button_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog.show(PostRateActivity.this,sharePhotoContent);
            }
        });

        starBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                starRate = rating;
                textView_rate.setText(Float.toString(starRate)+ "/5.0");
                DatabaseReference rateRef = mDatabase.child("post-offers").child(finalPostKey).child(finalOfferKey);
                rateRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Serialize retrieved data to a User object
                        Offer offer = dataSnapshot.getValue(Offer.class);
                        //Now you have an object of the User class and can use its getters like this
                        offeruid = String.valueOf(offer.uid);
                        offerUserName = String.valueOf(offer.author);
                        textView_rateother.setText("Please Rate Your Trade with "+offerUserName);

                        DatabaseReference userRef = mDatabase.child("users").child(offeruid);
                        userRef.addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(PostRateActivity.this, String.valueOf(newrate), Toast.LENGTH_SHORT).show();

            }
        });

        DatabaseReference rateRef = mDatabase.child("post-offers").child(finalPostKey).child(finalOfferKey);
        rateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Serialize retrieved data to a User object
                Offer offer = dataSnapshot.getValue(Offer.class);
                //Now you have an object of the User class and can use its getters like this
                offeruid = String.valueOf(offer.uid);
                offerUserName = String.valueOf(offer.author);
                textView_rateother.setText("Please Rate Your Trade with "+offerUserName);

                DatabaseReference userRef = mDatabase.child("users").child(offeruid);
                userRef.addValueEventListener(new ValueEventListener() {
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
                Intent intent = new Intent(PostRateActivity.this, MyPostActivity.class);
                //intent.putExtra(MyPostOfferActivity.EXTRA_POSTOFFER_POST_KEY, finalPostKey);
                startActivity(intent);
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
                        offerTitle = String.valueOf(offer.title);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                    }
                });
                mDatabase.child("users").child(offeruid).child("rate").setValue((double)newrate);
                mDatabase.child("users").child(offeruid).child("totalReview").setValue(newTotalReview+1);
                mDatabase.child("posts").child(finalPostKey).child("status").setValue("closed");
                mDatabase.child("user-posts").child(getUid()).child(finalPostKey).child("status").setValue("closed");
                mDatabase.child("user-offers").child(offeruid).child(finalOfferKey).child("status").setValue("accepted");
                mDatabase.child("post-offers").child(finalPostKey).child(finalOfferKey).child("status").setValue("accepted");
                sendMessage(getUid(),offeruid,"Hello, I accept your offer of " + offerTitle + ". Let's find a time to meet!");

                Intent intent = new Intent(PostRateActivity.this, MyPostActivity.class);
                startActivity(intent);
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
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        Date date = new Date();
        String time = dateFormat.format(date);
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("time", time);
        reference.child("chats").push().setValue(hashMap);

    }
}

