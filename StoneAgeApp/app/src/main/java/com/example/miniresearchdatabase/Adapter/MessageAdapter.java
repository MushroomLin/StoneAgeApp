package com.example.miniresearchdatabase.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miniresearchdatabase.ChatActivity;
import com.example.miniresearchdatabase.R;

import com.example.miniresearchdatabase.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter <MessageAdapter.MessageViewHolder>{
    private Context mContext;
    private List<User> mUsers;
    private List<String> userKeys;
    private String currKey;
    private String username;
    private List<String> otherAvatar;
    private String userAvatar;
    private String other;

    public MessageAdapter(Context mContext, List<User> mUsers, List<String> userKeys, String userAvatar, List<String> otherAvatar) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.userKeys = userKeys;
        this.userAvatar = userAvatar;
        this.otherAvatar = otherAvatar;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, final int position) {
        final User user = mUsers.get(position);
        messageViewHolder.tvSenders.setText(user.username);
        if(user.avatar != null) {
            messageViewHolder.imgUsers.setImageBitmap(user.getAvatar());
        }
        messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currKey = userKeys.get(position);
                username = user.username;
                other = otherAvatar.get(position);
//                Log.w("TAG", currKey);
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("userId", currKey);
                intent.putExtra("username",username);
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
        public ImageView imgUsers;


        public MessageViewHolder(View itemView) {
            super(itemView);
            tvSenders = itemView.findViewById(R.id.tvSenders);
            imgUsers = itemView.findViewById(R.id.imgUsers);
        }

    }
}

