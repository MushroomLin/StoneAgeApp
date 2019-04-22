package com.example.miniresearchdatabase.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.miniresearchdatabase.MainActivity;
import com.example.miniresearchdatabase.MyOfferActivity;
import com.example.miniresearchdatabase.MyPostActivity;
import com.example.miniresearchdatabase.NewPostActivity;
import com.example.miniresearchdatabase.R;
import com.example.miniresearchdatabase.SignInActivity;
import com.example.miniresearchdatabase.UserEditActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.miniresearchdatabase.models.User;
import org.w3c.dom.Text;

public class UserInformationFragment extends Fragment {

    private static final String TAG = "UserInformationFragment";

    // [START define_database_reference]

    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private OnFragmentInteractionListener mListener;
    private TextView UserNameTextView;
    private TextView UserEmailTextView;
    private RatingBar UserRatingBar;
    private TextView UserRateTextView;
    private ImageView UserAvatarImageView;
    private FrameLayout UserInformationLayout;
    private RelativeLayout MeMenuLayout;
    private RelativeLayout EditMenuItem;
    private RelativeLayout LikeMenuItem;
    private RelativeLayout PostMenuItem;
    private RelativeLayout OfferMenuItem;
    private RelativeLayout SignoutMenuItem;
    public UserInformationFragment() {

        // Required empty public constructor
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_user_information, container, false);

        // Information Part
        UserInformationLayout  = rootView.findViewById(R.id.UserInformationLayout);
        UserRateTextView = UserInformationLayout.findViewById(R.id.UserRateTextView);
        UserNameTextView = UserInformationLayout.findViewById(R.id.UserNameTextView);
        UserEmailTextView = UserInformationLayout.findViewById(R.id.UserEmailTextView);
        UserRatingBar = UserInformationLayout.findViewById(R.id.ratingBar);
        UserAvatarImageView = UserInformationLayout.findViewById(R.id.AvatarImageView);

        // Menu Part

        MeMenuLayout = rootView.findViewById(R.id.MeMenuLayout);
        EditMenuItem = MeMenuLayout.findViewById(R.id.EditMenuItem);
        PostMenuItem = MeMenuLayout.findViewById(R.id.PostMenuItem);
        OfferMenuItem = MeMenuLayout.findViewById(R.id.OfferMenuItem);
        LikeMenuItem = MeMenuLayout.findViewById(R.id.LikeMenuItem);
        SignoutMenuItem = rootView.findViewById(R.id.SignoutMenuItem);
        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(getUid());

        // [END create_database_reference]

        EditMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserEditActivity.class));
            }

        });

        PostMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyPostActivity.class));

            }

        });

        OfferMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyOfferActivity.class));

            }

        });

        SignoutMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), SignInActivity.class));
            }

        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Serialize retrieved data to a User object
                User user = dataSnapshot.getValue(User.class);
                //Now you have an object of the User class and can use its getters like this
                if (user!=null){
                    Log.w("TAG", user.email);
                    Log.w("TAG", user.username);
                    Log.w("TAG",Double.toString(user.rate));
                    UserNameTextView.setText(user.username);
                    UserEmailTextView.setText(user.email);

//                    UserAddressTextView.setText("Address: "+user.address);

//                    UserPhoneTextView.setText("Phone: "+user.phone);

//                    UserIntroductionTextView.setText('"'+user.intro+'"');

                    UserRateTextView.setText(String.format("%.1f",user.rate)+"/5.0");
                    UserRatingBar.setRating((float)user.rate);

                    // Set the user profile picture
                    if(user.avatar!=null) {
                        UserAvatarImageView.setImageBitmap(user.getAvatar());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        });
        return rootView;

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }

}