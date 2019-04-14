package com.example.miniresearchdatabase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miniresearchdatabase.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserEditActivity extends AppCompatActivity {
    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]
    private TextView UserNameEditText;
    private EditText PhoneEditText;
    private TextView EmailEditText;
    private EditText AddressEditText;
    private EditText IntroductionEditText;
    private Button SaveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        UserNameEditText = findViewById(R.id.UserNameEditText);
        PhoneEditText = findViewById(R.id.PhoneEditText);
        EmailEditText = findViewById(R.id.EmailEditText);
        AddressEditText = findViewById(R.id.AddressEditText);
        IntroductionEditText = findViewById(R.id.IntroductionEditText);
        SaveButton = findViewById(R.id.SaveButton);
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
}
