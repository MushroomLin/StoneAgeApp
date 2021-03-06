package com.example.miniresearchdatabase;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.miniresearchdatabase.models.User;

// Sign up a new user
public class SignupActivity extends BaseActivity{

    private Button button_signup2;

    private Button button_back2;
    private EditText editText_name;
    private EditText editText_pwd;
    private EditText editText_add;
    private EditText editText_phone;
    private EditText editText_intro;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private static final String TAG = "Signup";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        button_signup2 = findViewById(R.id.button_signup2);
        button_back2 = findViewById(R.id.button_back2);
        editText_name = findViewById(R.id.editText_name);
        editText_pwd = findViewById(R.id.editText_pwd);
        editText_add = findViewById(R.id.editText_add);
        editText_phone = findViewById(R.id.editText_phone);
        editText_intro = findViewById(R.id.editText_intro);


        // onclick will trigger signUp function
        button_signup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
    });
        // onclick will go back to sign in page
        button_back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, SignInActivity.class));
                finish();
            }
        });
    }

    // Signup
    private void signUp() {
        if (!validateForm()) {
            return;
        }

        String email = editText_name.getText().toString();
        String password = editText_pwd.getText().toString();

        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();
                        if (task.isSuccessful()) {
                            // if success, go to onAuthSuccess function
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignupActivity.this, getString(R.string.signin_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());
        String address = editText_add.getText().toString();
        String phone = editText_phone.getText().toString();
        String intro = editText_intro.getText().toString();
        // Write new user to database
        writeNewUser(user.getUid(), username, user.getEmail(), address, phone, intro, 5, "", 0);

        // Go to MainActivity
        startActivity(new Intent(SignupActivity.this, MainActivity.class));
        finish();
    }
    // Validate sign up form
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(editText_name.getText().toString())) {
            editText_name.setError("Required");
            result = false;
        }
        else if(!editText_name.getText().toString().contains("@")){
            editText_name.setError("Invalid Email Address!");
            result = false;
        }
        else {
            editText_name.setError(null);
        }

        if (TextUtils.isEmpty(editText_pwd.getText().toString())) {
            editText_pwd.setError("Required");
            result = false;
        }
        else if (editText_pwd.getText().toString().length()<6){
            editText_pwd.setError("At Least 6 Digit!");
            result = false;
        }
        else {
            editText_pwd.setError(null);
        }

        return result;
    }

    // Get user from from email, we use prefix of email as username
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    // Write new user function based on user format. Format is in models/User
    private void writeNewUser(String userId, String name, String email, String address, String phone, String intro, double rate, String avatar, int totalReview) {
        User user = new User(name, email, address, phone, intro, rate, avatar, totalReview);
        mDatabase.child("users").child(userId).setValue(user);
    }

}
