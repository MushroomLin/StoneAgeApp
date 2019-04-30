package com.example.miniresearchdatabase;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.miniresearchdatabase.Adapter.MessageContentAdapter;
import com.example.miniresearchdatabase.Notifications.Client;
import com.example.miniresearchdatabase.Notifications.Data;
import com.example.miniresearchdatabase.Notifications.MyResponse;
import com.example.miniresearchdatabase.Notifications.Sender;
import com.example.miniresearchdatabase.Notifications.Token;
import com.example.miniresearchdatabase.fragment.APIService;
import com.example.miniresearchdatabase.models.Message;
import com.example.miniresearchdatabase.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import rebus.bottomdialog.BottomDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatActivity extends BaseActivity{
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private EditText mMessageEditText;
    private Button mSendButton;
    private MessageContentAdapter messageContentAdapter;
    private String userId;
    private String username;
    private RecyclerView messageRecyclerView;
    private List<Message> messageList;
    private ProgressBar mProgressBar;
    private ImageView addMessageImageView;
    private static final int CAMERA_REQUEST = 1888;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private Bitmap bitmap;
    private String image;
    private Bitmap curr;
    private  Bitmap other;
    private BottomDialog dialog;
    APIService apiService;
    boolean notify = false;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("chats");
        userId = intent.getStringExtra("userId");
        userid = intent.getStringExtra("userid");
        username = intent.getStringExtra("username");
        this.setTitle(username);
        messageList = new ArrayList<>();

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = findViewById(R.id.sendButton);
        messageRecyclerView = findViewById(R.id.messageRecyclerView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        addMessageImageView = (ImageView)findViewById(R.id.addMessageImageView);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        dialog = new BottomDialog(ChatActivity.this);
        dialog.canceledOnTouchOutside(true);
        dialog.cancelable(true);
        dialog.inflateMenu(R.menu.menu_choose_picture);
        dialog.setOnItemSelectedListener(new BottomDialog.OnItemSelectedListener() {
            @Override
            public boolean onItemSelected(int id) {
                switch (id) {
                    case R.id.action_choose:
                        chooseImage();
                        return true;
                    case R.id.action_new:
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        return true;
                    default:
                        return false;
                }
            }
        });




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
                notify = true;
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
                dialog.show();
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
                                messageRecyclerView.setAdapter(messageContentAdapter);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
                                linearLayoutManager.scrollToPositionWithOffset(messageContentAdapter.getItemCount()-1, 0);
                                messageRecyclerView.setLayoutManager(linearLayoutManager);
                                messageRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
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
//        Log.w("TAG", String.valueOf(requestCode)+" "+String.valueOf(resultCode));
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image = convertImage(bitmap);
                if(image !=null) {
                    sendImage(firebaseUser.getUid(), userId, image);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null)
        {
            try {
//                Log.w("TAG", data.toString());
                bitmap = (Bitmap) data.getExtras().get("data");
                image = convertImage(bitmap);
                if(image !=null) {
                    sendImage(firebaseUser.getUid(), userId, image);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

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

    private void sendMessage(String sender, final String receiver, String message) {
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

            final String msg = message;
            reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (notify) {
                        sendNotificaction(receiver, user.username, msg);
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
        catch (Exception e){
            e.printStackTrace();
        };
    }

    private String convertImage(Bitmap b) {
        String data;
        if(b != null)
        {
            data = ImageUtils.bitmapToString(b);
        }
        else data = null;
        return data;
    }

    private void chooseImage() {
        // set up intent to choose a picture from phone
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void sendNotificaction(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username+": "+message, "New Message",
                            userId);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(ChatActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    public class ScrollLinearLayoutManager extends LinearLayoutManager {
//        private static final float MILLISECONDS_PER_INCH = 100f;
//        public ScrollLinearLayoutManager(Context context) {
//            super(context);
//        }
//
//        @Override
//        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, final int position) {
//            LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext())
//            {
//                @Nullable
//                @Override
//                public PointF computeScrollVectorForPosition(int targetPosition) {
//                    return ScrollLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
//                }
//
//                @Override
//                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
//                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
//                }
//            };
//            linearSmoothScroller.setTargetPosition(position);
//            startSmoothScroll(linearSmoothScroller);
//        }
//    }
}
