package com.example.miniresearchdatabase.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miniresearchdatabase.ImageUtils;
import com.example.miniresearchdatabase.R;
import com.example.miniresearchdatabase.models.Offer;
import com.example.miniresearchdatabase.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// Help recyclerView to show appropriate information to each offer to specific post
public class PostOfferViewHolder extends RecyclerView.ViewHolder {
    public ImageView postOfferAuthorPhoto;
    public TextView titlePostOfferView;
    public TextView authorPostOfferView;
    public TextView bodyPostOfferView;
    public TextView addressPostOfferView;
    public ImageView picturePostOfferView;
    public Button button_accept;
    private DatabaseReference mDatabase;
    public Context postoffer_context;
    public TextView starTextView;
    public PostOfferViewHolder(View itemView, Context postoffer_context) {
        super(itemView);

        titlePostOfferView = itemView.findViewById(R.id.offerTitle);
        authorPostOfferView = itemView.findViewById(R.id.offerAuthor);
        bodyPostOfferView = itemView.findViewById(R.id.offerBody);
        addressPostOfferView = itemView.findViewById(R.id.offerAddress);
        picturePostOfferView = itemView.findViewById(R.id.offerImageView);
        button_accept = itemView.findViewById(R.id.button_accept);
        postOfferAuthorPhoto = itemView.findViewById(R.id.offerAuthorPhoto);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        starTextView = itemView.findViewById(R.id.starTextView);
        this.postoffer_context = postoffer_context;
    }

    public void bindToPostOffer(Offer offer, View.OnClickListener acceptClickListener) {
        titlePostOfferView.setText(offer.title);
        authorPostOfferView.setText(offer.author);
        bodyPostOfferView.setText(offer.description);
        addressPostOfferView.setText(offer.address);
        if (offer.picture!=null && (!offer.picture.equals(""))) {
            picturePostOfferView.setImageBitmap(ImageUtils.stringToBitmap(offer.picture));
            picturePostOfferView.setVisibility(View.VISIBLE);
        }
        else{
            picturePostOfferView.setVisibility(View.GONE);
        }
        button_accept.setOnClickListener(acceptClickListener);

        if (offer.uid!=null) {
            Log.w("TAG", offer.uid);
            DatabaseReference ref = mDatabase.child("users").child(offer.uid);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Serialize retrieved data to a User object
                    User user = dataSnapshot.getValue(User.class);
                    //Now you have an object of the User class and can use its getters like this
                    if (user != null) {
                        // Set the user profile picture
                        if (user.avatar != null) {
                            postOfferAuthorPhoto.setImageBitmap(user.getAvatar());
                        }
                        starTextView.setText( String.format("%.1f", user.rate)+"/5.0");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                }
            });
        }
        else{
            postOfferAuthorPhoto.setImageDrawable(postoffer_context.getDrawable(R.drawable.ic_baseline_person_24px));
        }

    }
}