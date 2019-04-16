package com.example.miniresearchdatabase;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miniresearchdatabase.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserEditActivity extends AppCompatActivity {
    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]
    private ImageView AvatarImageView;
    private TextView UserNameEditText;
    private EditText PhoneEditText;
    private TextView EmailEditText;
    private EditText AddressEditText;
    private EditText IntroductionEditText;
    private Button SaveButton;
    private Button TakePhotoButton;
    private Uri filePath;
    private static final int PICK_IMAGE_REQUEST = 71;
    private static final int CAMERA_REQUEST = 1888;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        AvatarImageView = findViewById(R.id.AvatarImageView);
        UserNameEditText = findViewById(R.id.UserNameEditText);
        PhoneEditText = findViewById(R.id.PhoneEditText);
        EmailEditText = findViewById(R.id.EmailEditText);
        AddressEditText = findViewById(R.id.AddressEditText);
        IntroductionEditText = findViewById(R.id.IntroductionEditText);
        SaveButton = findViewById(R.id.SaveButton);
        TakePhotoButton = findViewById(R.id.TakePhotoButton);
        // Load the data from the Database
        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Serialize retrieved data to a User object
                User user = dataSnapshot.getValue(User.class);
                //Now you have an object of the User class and can use its getters like this
                if (user!=null){
                    Log.w("TAG", user.email);
                    Log.w("TAG", user.username);
                    Log.w("TAG",Double.toString(user.rate));
                    if (user.avatar!=null){
                        AvatarImageView.setImageBitmap(user.getAvatar());
                    }
                    AvatarImageView.setClickable(true);
                    AvatarImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chooseImage();
                        }
                    });
                    TakePhotoButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                    });

                    UserNameEditText.setText(user.username);
                    EmailEditText.setText(user.email);
                    AddressEditText.setText(user.address);
                    PhoneEditText.setText(user.phone);
                    IntroductionEditText.setText(user.intro);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        });
        // Save button clicked
        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = AddressEditText.getText().toString();
                String phone = PhoneEditText.getText().toString();
                String intro = IntroductionEditText.getText().toString();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("address", address);
                childUpdates.put("phone", phone);
                childUpdates.put("intro", intro);
                String avatar = convertImage();
                if (avatar!=null) {
                    childUpdates.put("avatar", avatar);
                }
                mDatabase.updateChildren(childUpdates);

                Toast.makeText(getApplicationContext(), "Success!",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    private void chooseImage() {
        // set up intent to choose a picture from phone
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w("TAG", String.valueOf(requestCode)+" "+String.valueOf(resultCode));
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                AvatarImageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null)
        {
            Log.w("TAG", data.toString());
            bitmap = (Bitmap) data.getExtras().get("data");
            AvatarImageView.setImageBitmap(bitmap);
        }
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
}
