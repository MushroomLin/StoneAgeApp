package com.example.miniresearchdatabase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.miniresearchdatabase.models.Offer;
import com.example.miniresearchdatabase.models.Post;
import com.example.miniresearchdatabase.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SendOfferActivity extends AppCompatActivity {
    private static final String TAG = "SendOfferActivity";
    private static final String REQUIRED = "Required";
    private DatabaseReference mDatabase;
    private EditText offerTitle;
    private EditText offerdescription;
    private FloatingActionButton offerButton;
    private Button button_addofferImage;
    private Button button_back;
    private final int PICK_IMAGE_REQUEST = 71;
    private ImageView offerimageView;
    private Uri filePath;
    private Bitmap bitmap_offer;
    private String offerPostKey;
    public static final String OFFER_POST_KEY = "post_key";
    private DatabaseReference offerReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendoffer);
        offerPostKey = getIntent().getStringExtra(OFFER_POST_KEY);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        offerTitle = findViewById(R.id.fieldOfferTitle);
        offerdescription = findViewById(R.id.editText_offerdescription);
        offerButton = findViewById(R.id.fabSubmitOffer);
        button_addofferImage = findViewById(R.id.button_addofferImage);
        offerimageView = findViewById(R.id.imageView_offer);
        button_back = findViewById(R.id.button_back3);

        offerReference = FirebaseDatabase.getInstance().getReference()
                .child("post-offers").child(offerPostKey);

        button_addofferImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        offerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOffer();
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SendOfferActivity.this, PostDetailActivity.class));
            }
        });


    }

    private void chooseImage() {
        // set up intent to choose a picture from phone
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private String convertImage() {
        String data;
        if(bitmap_offer != null)
        {
            data = ImageUtils.bitmapToString(bitmap_offer);
        }
        else data = null;
        return data;
    }

    private void submitOffer() {
        final String title = offerTitle.getText().toString();
        final String description = offerdescription.getText().toString();

        if (TextUtils.isEmpty(title)) {
            offerTitle.setError(REQUIRED);
            return;
        }
        setEditingEnabled(false);
        Toast.makeText(this, offerPostKey, Toast.LENGTH_SHORT).show();

        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        String author = user.username;

                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "Post " + offerPostKey + " is unexpectedly null");
                            Toast.makeText(SendOfferActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            String picture = convertImage();
                            if (picture == null) {
                                picture = "";
                            }
                            Offer offer = new Offer(userId,author,title,description,picture);
                            offerReference.push().setValue(offer);
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getPost:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                bitmap_offer = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                offerimageView.setImageBitmap(bitmap_offer);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void writeNewOffer(String userId, String username, String title,
                              String description, String picture) {

        String key = mDatabase.child("offers").push().getKey();
        Offer offer = new Offer(userId, username, title, description, picture);
        Map<String, Object> offerValues = offer.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/offers/" + key, offerValues);
        childUpdates.put("/post-offers/" + offerPostKey + "/" + key, offerValues);

        mDatabase.updateChildren(childUpdates);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    private void setEditingEnabled(boolean enabled) {
        offerTitle.setEnabled(enabled);
        if (enabled) {
            offerButton.show();
        } else {
            offerButton.hide();
        }
    }
}
