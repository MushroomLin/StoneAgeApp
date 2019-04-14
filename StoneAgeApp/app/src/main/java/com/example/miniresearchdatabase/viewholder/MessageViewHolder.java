package com.example.miniresearchdatabase.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miniresearchdatabase.R;
import com.example.miniresearchdatabase.models.Message;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView tvSender;
    public ImageView imgUser;


    public MessageViewHolder(View itemView) {
        super(itemView);


        tvSender = itemView.findViewById(R.id.tvSenders);
        imgUser = itemView.findViewById(R.id.imgUsers);
    }

    public void bindToMessage(Message message) {
        tvSender.setText(message.sender);
    }
}
