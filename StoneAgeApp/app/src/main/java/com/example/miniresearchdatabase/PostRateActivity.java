package com.example.miniresearchdatabase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.miniresearchdatabase.models.Post;
import com.example.miniresearchdatabase.models.User;
import com.example.miniresearchdatabase.ImageUtils;
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

    public static final String FINAL_POST_KEY = "mPostkey";
    public static final String FINAL_OFFER_KEY = "offerkey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postrate);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        button_facebook = findViewById(R.id.button_facebook);
        finalPostKey = getIntent().getStringExtra(FINAL_POST_KEY);
        finalOfferKey = getIntent().getStringExtra(FINAL_OFFER_KEY);

        button_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog.show(PostRateActivity.this,sharePhotoContent);
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
                        sharePhotoContent = new SharePhotoContent.Builder()
                                .addPhoto(postPhoto)
                                .build();
                    }

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        });

        //ShareHashtag shareHashTag = new ShareHashtag.Builder().setHashtag("#YOUR_HASHTAG").build();
    }
}

