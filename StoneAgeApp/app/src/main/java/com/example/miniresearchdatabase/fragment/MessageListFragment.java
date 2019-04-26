package com.example.miniresearchdatabase.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.miniresearchdatabase.Adapter.MessageAdapter;
import com.example.miniresearchdatabase.R;
import com.example.miniresearchdatabase.models.Message;
import com.example.miniresearchdatabase.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MessageListFragment extends Fragment {

    private static final String TAG = "MessageListFragment";
    private DatabaseReference mDatabase;
    private RecyclerView mRecycler;

    private MessageAdapter messageAdapter;

    private LinearLayoutManager mManager;
    private List<Message> mUsers;
    private List<String> userKeys;
    private List<String> otherAvatar;
    private String userAvatar;

    public MessageListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_messages, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //        // [END create_database_reference]

        mRecycler = rootView.findViewById(R.id.rvMessage);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setHasFixedSize(true);
        mUsers = new ArrayList<>();
        userKeys = new ArrayList<>();
        otherAvatar = new ArrayList<>();
        readUsers();



        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chats");
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        Log.w("TAG", reference.toString());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                userKeys.clear();
                otherAvatar.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    assert  message != null;
                    assert firebaseUser != null;
                    if(message.sender.equals(getUid())) {
//                        Log.w("TAG", snapshot.getKey());
                        mUsers.add(message);

                    }
                }
                Log.w("AVA", getUid());
                Log.w("AVA", mUsers.toString());
                messageAdapter = new MessageAdapter(getContext(), mUsers);
                mRecycler.setAdapter(messageAdapter);
                RecyclerView.ItemDecoration decor = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                mRecycler.addItemDecoration(decor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

}



