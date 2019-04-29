package com.example.miniresearchdatabase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.miniresearchdatabase.Adapter.MessageAdapter;
import com.example.miniresearchdatabase.Adapter.MessageContentAdapter;
import com.example.miniresearchdatabase.models.Message;
import com.example.miniresearchdatabase.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import rebus.bottomdialog.BottomDialog;


public class ChatActivity extends BaseActivity{
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private DatabaseReference mDatabase;
    private EditText mMessageEditText;
    private Button mSendButton;
    private MessageContentAdapter messageContentAdapter;
    private String userId;
    private String username;
    private RecyclerView messageRecyclerView;
    private List<Message> messageList;
    private ProgressBar mProgressBar;
    private LinearLayoutManager mLinearLayoutManager;
    private ImageView addMessageImageView;
    private static final int CAMERA_REQUEST = 1888;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private Bitmap bitmap;
    private String image;
    private Bitmap curr;
    private  Bitmap other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("chats");
        userId = intent.getStringExtra("userId");
        username = intent.getStringExtra("username");
        this.setTitle(username);
        messageList = new ArrayList<>();

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = findViewById(R.id.sendButton);
        messageRecyclerView = findViewById(R.id.messageRecyclerView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        addMessageImageView = (ImageView)findViewById(R.id.addMessageImageView);


        mLinearLayoutManager = new LinearLayoutManager(this);
        messageRecyclerView.setLayoutManager(mLinearLayoutManager);
        messageRecyclerView.setHasFixedSize(true);




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

        addMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                messageList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    assert  message != null;
                    if((userId.equals(message.receiver) && getUid().equals(message.sender))
                            || (getUid().equals(message.receiver) && userId.equals(message.sender))) {
                        messageList.add(message);
                    }
                }
                Collections.sort(messageList, new Comparator<Message>() {
                    @Override
                    public int compare(Message o1, Message o2) {
                        try{
                            return o1.time.compareTo(o2.time);
                        }
                        catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });

                DatabaseReference receive = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                receive.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        assert user != null;
                        if(user.avatar != null) {
                            Log.w("TAG", user.avatar);
                            curr = user.getAvatar();
                        }
                        DatabaseReference send = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
                        send.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                assert user != null;
                                if(user.avatar != null) {
                                    other = user.getAvatar();
                                }
                                messageContentAdapter = new MessageContentAdapter(ChatActivity.this, messageList, curr, other);
                                messageContentAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                    @Override
                                    public void onItemRangeInserted(int positionStart, int itemCount) {
                                        super.onItemRangeInserted(positionStart, itemCount);
                                        int friendlyMessageCount = messageContentAdapter.getItemCount();
                                        int lastVisiblePosition =
                                                mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                                        // If the recycler view is initially being loaded or the
                                        // user is at the bottom of the list, scroll to the bottom
                                        // of the list to show the newly added message.
                                        if (lastVisiblePosition == -1 ||
                                                (positionStart >= (friendlyMessageCount - 1) &&
                                                        lastVisiblePosition == (positionStart - 1))) {
                                            messageRecyclerView.scrollToPosition(positionStart);
                                        }
                                    }
                                });
                                messageRecyclerView.setAdapter(messageContentAdapter);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // get image
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image = convertImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendImage(firebaseUser.getUid(), userId, image);
        }
    }

    public void sendImage(String sender, String receiver, String image) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        Date date = new Date();
        String time = dateFormat.format(date);
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("image", image);
        hashMap.put("time", time);
        reference.child("chats").push().setValue(hashMap);
    }

    private void sendMessage(String sender, String receiver, String message) {
        try{
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
        catch (Exception e){
        };
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
