package com.example.miniresearchdatabase.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miniresearchdatabase.ImageUtils;
import com.example.miniresearchdatabase.R;
import com.example.miniresearchdatabase.models.Offer;

public class OfferViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public TextView bodyView;
    public TextView addressView;
    public ImageView pictureView;

    public OfferViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.offerTitle);
        authorView = itemView.findViewById(R.id.offerAuthor);

        bodyView = itemView.findViewById(R.id.offerBody);
        addressView = itemView.findViewById(R.id.offerAddress);
        pictureView = itemView.findViewById(R.id.offerImageView);
    }

    public void bindToOffer(Offer offer, View.OnClickListener starClickListener) {
        titleView.setText(offer.title);
        authorView.setText(offer.author);
        bodyView.setText(offer.description);
        addressView.setText(offer.address);
        if (offer.picture!=null)
            pictureView.setImageBitmap(ImageUtils.stringToBitmap(offer.picture));

    }
}