package com.example.miniresearchdatabase.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.miniresearchdatabase.R;
import com.example.miniresearchdatabase.models.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

// adapter for recycler view in chatting room where you can see then content you have chatted.
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
    }

    @NonNull
    @Override
    public MessageContentAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
//      create view holder.
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_content, parent, false);
        return new MessageContentAdapter.MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageContentAdapter.MessageViewHolder messageViewHolder, int position) {
//      bind view holder with the users' chat contents gotten from ChatActivity.
        if(getItemCount() != 0) {
//          decide whether this message from message list belongs to receiver and bind it to sender's item view.
            if(user.equals(messageRead.get(position).receiver)) {
                if(curr != null) {
                    messageViewHolder.receiverImageView.setImageBitmap(curr);
                }
//              got two users' avatars.
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
//          decide whether this message from message list belongs to sender and bind it to receiver's item view.
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
