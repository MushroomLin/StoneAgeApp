package com.example.miniresearchdatabase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miniresearchdatabase.Notifications.Client;
import com.example.miniresearchdatabase.Notifications.Data;
import com.example.miniresearchdatabase.Notifications.MyResponse;
import com.example.miniresearchdatabase.Notifications.Sender;
import com.example.miniresearchdatabase.Notifications.Token;
import com.example.miniresearchdatabase.fragment.APIService;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// After accept an offer, go to this page and rate the user who you trade with. You can also send pictures to Facebook.
// Please make sure your phone has Google Play Service, had Facebook app installed in order to use the Facebook function.
// Please use this testing account to log in to your Facebook app first:
// Username: test_yzdjnnb_zhu@tfbnw.net   Password:199613

public class PostRateActivity extends AppCompatActivity {
    private Button button_facebook;
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
    private ImageView button_back;
    APIService apiService;
    boolean notify = false;

    public static final String FINAL_POST_KEY = "mPostkey";
    public static final String FINAL_OFFER_KEY = "offerkey";

    // Reference: From Facebook Developer Guide
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
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        button_facebook = findViewById(R.id.button_facebook);
        button_back = findViewById(R.id.BackImageView);
        button_submitrate = findViewById(R.id.button_submitrate);
        starBar = findViewById(R.id.starBar);
        textView_rate = findViewById(R.id.textView_rate);
        textView_rateother = findViewById(R.id.textView_rateother);
        finalPostKey = getIntent().getStringExtra(FINAL_POST_KEY);
        finalOfferKey = getIntent().getStringExtra(FINAL_OFFER_KEY);
        textView_rate.setText("5.0/5.0");

        // run Facebook function when click this button
        button_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog.show(PostRateActivity.this,sharePhotoContent);
            }
        });

        // Calculate new rate in case user change the rating bar.
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
                        textView_rateother.setText(getString(R.string.please_rate_trade)+ " " +offerUserName);

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

            }
        });

        // Calculate new rate in case user never touch the rating bar.
        DatabaseReference rateRef = mDatabase.child("post-offers").child(finalPostKey).child(finalOfferKey);
        rateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Serialize retrieved data to a User object
                Offer offer = dataSnapshot.getValue(Offer.class);
                //Now you have an object of the User class and can use its getters like this
                offeruid = String.valueOf(offer.uid);
                offerUserName = String.valueOf(offer.author);
                textView_rateother.setText(getString(R.string.please_rate_trade)+ " " +offerUserName);

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

        // Go back to MyPost page
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostRateActivity.this, MyPostActivity.class);
                //intent.putExtra(MyPostOfferActivity.EXTRA_POSTOFFER_POST_KEY, finalPostKey);
                startActivity(intent);
            }
        });

        // when click submit button, update the rating, change post and rejected offers' status to closed.
        // change the status of accepted offer to accepted. Send appropriate messages to users who send offers.
        button_submitrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rateRef = mDatabase.child("post-offers").child(finalPostKey).child(finalOfferKey);

                rateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Serialize retrieved data to a User object
                        Offer offer = dataSnapshot.getValue(Offer.class);
                        //Now you have an object of the User class and can use its getters like this
                        offeruid = String.valueOf(offer.uid);
                        offerTitle = String.valueOf(offer.title);
                        notify = true;
                        mDatabase.child("user-offers").child(offeruid).child(finalOfferKey).child("status").setValue("accepted");
                        mDatabase.child("post-offers").child(finalPostKey).child(finalOfferKey).child("status").setValue("accepted");
                        // Send message to tell user that his/her offer is accepted.
                        sendMessage(getUid(),offeruid, getString(R.string.hello_accept)+ " " + offerTitle + getString(R.string.check_accepted));

                        DatabaseReference rejectedRef = mDatabase.child("post-offers").child(finalPostKey);
                        rejectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot alloffer : dataSnapshot.getChildren()) {
                                    Offer offer = alloffer.getValue(Offer.class);
                                    String s = String.valueOf(offer.status);
                                    String offeruids = String.valueOf(offer.uid);
                                    String offerTitles = String.valueOf(offer.title);
                                    if (!s.equals("accepted")) {
                                        alloffer.child("status").getRef().setValue("closed");
                                        // Send message to tell all other users that his/her offer is rejected.
                                        sendMessage(getUid(),offeruids,getString(R.string.sorry_reject) + " " +offerTitles + getString(R.string.please_check_other));
                                    }
                                }
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
                // update rating and close the post.
                mDatabase.child("users").child(offeruid).child("rate").setValue((double)newrate);
                mDatabase.child("users").child(offeruid).child("totalReview").setValue(newTotalReview+1);
                mDatabase.child("posts").child(finalPostKey).child("status").setValue("closed");
                mDatabase.child("user-posts").child(getUid()).child(finalPostKey).child("status").setValue("closed");

                Intent intent = new Intent(PostRateActivity.this, MyPostActivity.class);
                startActivity(intent);
            }
        });

        //Get the two picture of the post and the accepted offer.Add them to Facebook's photo content.
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
    // Helper function to send message.
    private void sendMessage(String sender, final String receiver, String message) {
        try{
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

            final String msg = message;
            reference = FirebaseDatabase.getInstance().getReference("users").child(getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (notify) {
                        sendNotificaction(receiver, user.username, msg);
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
        catch (Exception e){
            e.printStackTrace();
        };
    }
    // Helper function to send notification.
    private void sendNotificaction(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(getUid(), R.mipmap.ic_launcher, username+": "+message, "New Message",
                            offeruid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){

                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

