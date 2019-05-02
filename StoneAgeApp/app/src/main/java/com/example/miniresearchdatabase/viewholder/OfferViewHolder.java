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

// Help recyclerView to show appropriate information to each offer item
public class OfferViewHolder extends RecyclerView.ViewHolder {
    public ImageView offerAuthorPhoto;
    public TextView titleOfferView;
    public TextView authorOfferView;
    public TextView bodyOfferView;
    public TextView addressOfferView;
    public ImageView pictureOfferView;
    private DatabaseReference mDatabase;
    public Context offer_context;
    public TextView starTextView;
    public OfferViewHolder(View itemView, Context offer_context) {
        super(itemView);

        titleOfferView = itemView.findViewById(R.id.offerTitle);
        authorOfferView = itemView.findViewById(R.id.offerAuthor);
        bodyOfferView = itemView.findViewById(R.id.offerBody);
        addressOfferView = itemView.findViewById(R.id.offerAddress);
        pictureOfferView = itemView.findViewById(R.id.offerImageView);
        offerAuthorPhoto = itemView.findViewById(R.id.offerAuthorPhoto);
        starTextView = itemView.findViewById(R.id.starTextView);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.offer_context = offer_context;
    }

    public void bindToOffer(Offer offer) {
        titleOfferView.setText(offer.title);
        authorOfferView.setText(offer.author);
        bodyOfferView.setText(offer.description);
        addressOfferView.setText(offer.address);
        if (offer.picture!=null && (!offer.picture.equals(""))) {
            pictureOfferView.setImageBitmap(ImageUtils.stringToBitmap(offer.picture));
            pictureOfferView.setVisibility(View.VISIBLE);
        }
        else{
            pictureOfferView.setVisibility(View.GONE);
        }
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
                            offerAuthorPhoto.setImageBitmap(user.getAvatar());
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
            offerAuthorPhoto.setImageDrawable(offer_context.getDrawable(R.drawable.ic_baseline_person_24px));
        }

    }
}