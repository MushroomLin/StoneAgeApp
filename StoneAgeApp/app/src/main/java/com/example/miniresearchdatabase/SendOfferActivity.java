package com.example.miniresearchdatabase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.miniresearchdatabase.models.Offer;
import com.example.miniresearchdatabase.models.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SendOfferActivity extends AppCompatActivity {
    private static final String TAG = "SendOfferActivity";
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

        button_addofferImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
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

    private void submitPost() {
        final String title = offerTitle.getText().toString();
        final String description = offerdescription.getText().toString();
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
}
