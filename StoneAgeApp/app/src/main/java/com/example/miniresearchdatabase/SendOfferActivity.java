package com.example.miniresearchdatabase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private EditText offerAddress;
    private FloatingActionButton offerButton;
    private FloatingActionButton button_back;
    private ImageButton button_selectAddress;
    private final int PICK_IMAGE_REQUEST = 71;
    private final int SELECT_ADDRESS_ON_MAP = 118;
    private String address = "";
    private ImageView offerimageView;
    private Uri filePath;
    private Bitmap bitmap_offer;
    private String offerPostKey;
    public static final String OFFER_POST_KEY = "post_key";
    private DatabaseReference offerReference;
    private DatabaseReference offerUserReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendoffer);
        offerPostKey = getIntent().getStringExtra(OFFER_POST_KEY);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        offerTitle = findViewById(R.id.fieldOfferTitle);
        offerdescription = findViewById(R.id.editText_offerdescription);
        offerAddress = findViewById(R.id.addressEditText);
        offerButton = findViewById(R.id.fabSubmitOffer);
        offerimageView = findViewById(R.id.imageView_offer);
        button_back = findViewById(R.id.fabCancelOffer);
        button_selectAddress = findViewById(R.id.selectAddressBtn);
        offerReference = FirebaseDatabase.getInstance().getReference()
                .child("post-offers").child(offerPostKey);
        offerUserReference = FirebaseDatabase.getInstance().getReference()
                .child("user-offers");
        offerimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }

        offerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOffer();
            }
        });
        button_selectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SendOfferActivity.this, MapsActivity_selectAddress.class), SELECT_ADDRESS_ON_MAP);
            }
        });
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SendOfferActivity.this, PostDetailActivity.class);
                intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, offerPostKey);
                startActivity(intent);
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
        final String address = offerAddress.getText().toString();
        final String status = "open";

        if (TextUtils.isEmpty(title)) {
            offerTitle.setError(REQUIRED);
            return;
        }
        setEditingEnabled(false);
        Toast.makeText(this, "Sending...", Toast.LENGTH_SHORT).show();

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
                            Offer offer = new Offer(userId,author,title,description,address,picture,offerPostKey,status);
                            String key = offerReference.push().getKey();
                            offerReference.child(key).setValue(offer);
                            offerUserReference.child(userId).child(key).setValue(offer);
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
        if (requestCode == SELECT_ADDRESS_ON_MAP && resultCode == RESULT_OK && data != null) {
            address = data.getExtras().getString("selectAddress"); //get the data from new Activity when it finished
            offerAddress.setText(address);
        }
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
