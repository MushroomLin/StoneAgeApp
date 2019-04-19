package com.example.miniresearchdatabase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.miniresearchdatabase.R;
import com.example.miniresearchdatabase.models.Post;
import com.example.miniresearchdatabase.models.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends BaseActivity {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";

    // declare database_ref
    private DatabaseReference mDatabase;


    private EditText mTitleField;
    private EditText mAddressField;
    private EditText mLatitudeField;
    private EditText mLongitudeField;
    private FloatingActionButton mSubmitButton;

    // -------
    private final int PICK_IMAGE_REQUEST = 71;
    private Button button_addImage;
    private ImageView imageView;
    private EditText editText_description;

    private Uri filePath;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        // initialize database_ref
        mDatabase = FirebaseDatabase.getInstance().getReference();


        mTitleField = findViewById(R.id.fieldTitle);
        mSubmitButton = findViewById(R.id.fabSubmitPost);
        mAddressField = findViewById(R.id.fieldAddress);
        mLatitudeField = findViewById(R.id.fieldLatitude);
        mLongitudeField = findViewById(R.id.fieldLongitude);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });

        button_addImage = findViewById(R.id.button_addImage);
        imageView = findViewById(R.id.imageView);
        editText_description = findViewById(R.id.editText_description);

        button_addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
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
        if(bitmap != null)
        {
            data = ImageUtils.bitmapToString(bitmap);
        }
        else data = null;
        return data;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void submitPost() {
        final String title = mTitleField.getText().toString();
        final String address = mAddressField.getText().toString();
        final String latitude = mLatitudeField.getText().toString();
        final String longitude = mLongitudeField.getText().toString();

        final String description = editText_description.getText().toString();
        final String originalType = "originalType";
        final String targetType = "targetType";

        // Title is required
        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        // single value read
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);


                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            String picture = convertImage();
                            if (picture == null) {
                                picture = "";
                            }
                            writeNewPost(userId, user.username, title, address, latitude, longitude,
                                    description, originalType, targetType, picture);
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });

    }

    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.show();
        } else {
            mSubmitButton.hide();
        }
    }

    // write_fan_out
    private void writeNewPost(String userId, String username, String title,
                              String address, String latitude, String longitude,
                              String description, String originalType, String targetType, String picture) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, title, address, latitude, longitude,
                description, originalType, targetType, picture);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

}
