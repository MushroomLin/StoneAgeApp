package com.example.miniresearchdatabase.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.miniresearchdatabase.Adapter.MessageAdapter;
import com.example.miniresearchdatabase.Notifications.Token;
import com.example.miniresearchdatabase.R;
import com.example.miniresearchdatabase.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class MessageListFragment extends Fragment {

    private static final String TAG = "MessageListFragment";
    private Context mContext;
    private RecyclerView mRecycler;

    private MessageAdapter messageAdapter;
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();

    private List<String> mUsers;
    public MessageListFragment() {}


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_messages, container, false);

        mRecycler = rootView.findViewById(R.id.rvMessage);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setHasFixedSize(true);
        mUsers = new ArrayList<>();
        readUsers();
        updateToken(FirebaseInstanceId.getInstance().getToken());
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chats");

        try {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mUsers.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Message message = snapshot.getValue(Message.class);
                        assert  message != null;
                        assert message.receiver != null;
                        assert message.sender != null;
                        assert message.message != null;
                        assert message.time != null;
                        if(message.receiver.equals(getUid()) && !mUsers.contains(message.sender)) {
                            mUsers.add(message.sender);
                        }
                        else if(message.sender.equals(getUid()) && !mUsers.contains(message.receiver)) {
                            mUsers.add(message.receiver);
                        }
                    }
                    messageAdapter = new MessageAdapter(mContext, mUsers);
                    mRecycler.setAdapter(messageAdapter);
                    RecyclerView.ItemDecoration decor = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
                    mRecycler.addItemDecoration(decor);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(currUser.getUid()).setValue(token1);
    }


    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}



