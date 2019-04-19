package com.example.miniresearchdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.miniresearchdatabase.models.Message;
import com.example.miniresearchdatabase.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends BaseActivity{
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private DatabaseReference mDatabase;
    private EditText mMessageEditText;
    private Button mSendButton;
    private String userId;
    private String username;
    private RecyclerView messageRecyclerView;
    private List<String> sentMessage;
    private List<String> receivedMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("chats");
        username = intent.getStringExtra("username");
        this.setTitle(username);
        sentMessage = new ArrayList<>();
        receivedMessage = new ArrayList<>();

        userId = intent.getStringExtra("userId");
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = findViewById(R.id.sendButton);
        messageRecyclerView = findViewById(R.id.messageRecyclerView);




        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mMessageEditText.getText().toString();

                if(!msg.equals("")) {
                    sendMessage(firebaseUser.getUid(), userId, msg);
                }
                else {
                    Toast.makeText(ChatActivity.this, "message empty", Toast.LENGTH_SHORT).show();
                }
                mMessageEditText.setText("");
            }
        });



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sentMessage.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    assert  message != null;
//                    Log.w("TAG", message.message);
                    Log.w("USER", message.sender);
                    if(userId.equals(message.receiver) && getUid().equals(message.sender)) {
                        sentMessage.add(message.message);
                    }
                    if(getUid().equals(message.receiver) && userId.equals(message.sender)){
                        receivedMessage.add(message.message);
                    }
                }
                Log.w("Mess", sentMessage.toString());
                Log.w("Mess", receivedMessage.toString());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String sender, String receiver, String message) {
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

    }
}
