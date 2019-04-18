package com.example.miniresearchdatabase.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miniresearchdatabase.ImageUtils;
import com.example.miniresearchdatabase.R;
import com.example.miniresearchdatabase.models.Post;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;
    public TextView addressView;
    public ImageView pictureView;

    public PostViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.postTitle);
        authorView = itemView.findViewById(R.id.postAuthor);

        starView = itemView.findViewById(R.id.star);
        numStarsView = itemView.findViewById(R.id.postNumStars);
        bodyView = itemView.findViewById(R.id.postBody);
        addressView = itemView.findViewById(R.id.postAddress);
        pictureView = itemView.findViewById(R.id.pictureImageView);
    }

    public void bindToPost(Post post, View.OnClickListener starClickListener) {
        titleView.setText(post.title);
        authorView.setText(post.author);
        numStarsView.setText(String.valueOf(post.starCount));
        bodyView.setText(post.body);
        addressView.setText(post.address);
        if (post.picture!=null)
            pictureView.setImageBitmap(ImageUtils.stringToBitmap(post.picture));
        starView.setOnClickListener(starClickListener);
    }
}
