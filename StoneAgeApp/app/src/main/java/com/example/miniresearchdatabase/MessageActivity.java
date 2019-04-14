package com.example.miniresearchdatabase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MessageActivity";
    public static final String EXTRA_MESSAGE_KEY = "message_key";
    private String mMessageKey;
    private DatabaseReference mMessageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mMessageKey = getIntent().getStringExtra(EXTRA_MESSAGE_KEY);
        if (mMessageKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }
        mMessageReference = FirebaseDatabase.getInstance().getReference()
                .child("messages").child(mMessageKey);

    }

    @Override
    public void onClick(View v) {

    }
}
