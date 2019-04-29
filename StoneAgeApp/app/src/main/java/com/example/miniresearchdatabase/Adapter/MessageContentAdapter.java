package com.example.miniresearchdatabase.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.miniresearchdatabase.ChatActivity;
import com.example.miniresearchdatabase.ImageUtils;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageContentAdapter extends RecyclerView.Adapter<MessageContentAdapter.MessageViewHolder>{
    private Context mContext;
    private List<Message> messageRead;
    private String user;
    private  Bitmap curr;
    private  Bitmap other;



    public MessageContentAdapter(Context mContext, List<Message> messageRead, Bitmap curr, Bitmap other) {
        this.messageRead = messageRead;
        this.mContext = mContext;
        this.user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.curr = curr;
        this.other = other;

//        DatabaseReference receive = FirebaseDatabase.getInstance().getReference().child("users").child(otherUser);
//        receive.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                assert user != null;
//                if(user.avatar != null) {
//                    curr = user.getAvatar();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//
//        DatabaseReference send = FirebaseDatabase.getInstance().getReference().child("users").child(user);
//        send.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                assert user != null;
//                if(user.avatar != null) {
//                    other = user.getAvatar();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });

    }

    @NonNull
    @Override
    public MessageContentAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_content, parent, false);
        return new MessageContentAdapter.MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageContentAdapter.MessageViewHolder messageViewHolder, int position) {
        if(getItemCount() != 0) {
            if(user.equals(messageRead.get(position).receiver)) {
                if(curr != null) {
                    messageViewHolder.receiverImageView.setImageBitmap(curr);
                }
                if(messageRead.get(position).image != null) {
                    messageViewHolder.receiverLayout.setVisibility(LinearLayout.VISIBLE);
                    messageViewHolder.senderLayout.setVisibility(LinearLayout.GONE);
                    messageViewHolder.timeTextView.setText(messageRead.get(position).time);
                    messageViewHolder.messageTextView.setVisibility(ImageView.GONE);
                    messageViewHolder.messageImageView.setVisibility(View.VISIBLE);
                    messageViewHolder.messageImageView.setImageBitmap(messageRead.get(position).getAvatar());
                }
                else {
                    messageViewHolder.receiverLayout.setVisibility(LinearLayout.VISIBLE);
                    messageViewHolder.senderLayout.setVisibility(LinearLayout.GONE);
                    messageViewHolder.timeTextView.setText(messageRead.get(position).time);
                    messageViewHolder.messageTextView.setText(messageRead.get(position).message);
                    messageViewHolder.messageImageView.setVisibility(ImageView.GONE);
                }

            }
            else if(user.equals(messageRead.get(position).sender)) {
                if(other != null) {
                    messageViewHolder.senderImageView.setImageBitmap(other);
                }
                if(messageRead.get(position).image != null) {
                    messageViewHolder.receiverLayout.setVisibility(LinearLayout.GONE);
                    messageViewHolder.senderLayout.setVisibility(LinearLayout.VISIBLE);
                    messageViewHolder.sendTimeTextView.setText(messageRead.get(position).time);
                    messageViewHolder.sendImageView.setVisibility(View.VISIBLE);
                    messageViewHolder.sendTextView.setVisibility(ImageView.GONE);
                    messageViewHolder.sendImageView.setImageBitmap(messageRead.get(position).getAvatar());
                    Log.w("IMG", messageRead.get(position).image);
                }
                else {
                    messageViewHolder.receiverLayout.setVisibility(LinearLayout.GONE);
                    messageViewHolder.senderLayout.setVisibility(LinearLayout.VISIBLE);
                    messageViewHolder.sendTimeTextView.setText(messageRead.get(position).time);
                    messageViewHolder.sendTextView.setText(messageRead.get(position).message);
                    messageViewHolder.sendImageView.setVisibility(ImageView.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageRead.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout receiverLayout;
        private LinearLayout senderLayout;
        private TextView messageTextView;
        private CircleImageView receiverImageView;
        private ImageView messageImageView;
        private TextView timeTextView;
        private TextView sendTextView;
        private ImageView sendImageView;
        private TextView sendTimeTextView;
        private CircleImageView senderImageView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            receiverLayout = (LinearLayout) itemView.findViewById(R.id.receiverLayout) ;
            senderLayout = (LinearLayout) itemView.findViewById(R.id.senderLayout) ;
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messageImageView = (ImageView) itemView.findViewById(R.id.receiveImageView);
            timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
            receiverImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            sendTextView = (TextView) itemView.findViewById(R.id.sendTextView);
            sendImageView = (ImageView) itemView.findViewById(R.id.sendImageView);
            sendTimeTextView = (TextView) itemView.findViewById(R.id.sendTimeTextView);
            senderImageView = (CircleImageView) itemView.findViewById(R.id.senderImageView);
        }
    }
}
