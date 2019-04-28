package com.example.miniresearchdatabase.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miniresearchdatabase.ChatActivity;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter <MessageAdapter.MessageViewHolder>{
    private Context mContext;
    private List<String> mUsers;
    private List<String> username;
    private String currUser = FirebaseAuth.getInstance().getCurrentUser().getUid();


    public MessageAdapter(Context mContext, List<String> mUsers) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        username = new ArrayList<>();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int position) {
        String user = mUsers.get(position);
        if(getItemCount() != 0) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(user);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        messageViewHolder.tvSenders.setText(user.username);
                        username.add(user.username);
                        if (user.avatar != null) {
                            messageViewHolder.imgUsers.setImageBitmap(user.getAvatar());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        }

        messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currKey = mUsers.get(messageViewHolder.getAdapterPosition());
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("username", username.get(messageViewHolder.getAdapterPosition()));
                intent.putExtra("userId", currKey);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView tvSenders;
        public CircleImageView imgUsers;


        public MessageViewHolder(View itemView) {
            super(itemView);
            tvSenders = itemView.findViewById(R.id.tvSenders);
            imgUsers = itemView.findViewById(R.id.imgUsers);
        }

    }
}

