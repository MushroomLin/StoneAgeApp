package com.example.miniresearchdatabase.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.miniresearchdatabase.MainActivity;
import com.example.miniresearchdatabase.MyPostActivity;
import com.example.miniresearchdatabase.NewPostActivity;
import com.example.miniresearchdatabase.R;
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
//    private TextView UserAddressTextView;
//    private TextView UserPhoneTextView;
//    private TextView UserIntroductionTextView;
//    private TextView UserRateTextView;
    private Button EditButton;
    private Button button_mypost;
    private ImageView UserAvatarImageView;
    public UserInformationFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_user_information, container, false);
        UserNameTextView = rootView.findViewById(R.id.UserNameTextView);
        UserEmailTextView = rootView.findViewById(R.id.UserEmailTextView);
//        UserAddressTextView = rootView.findViewById(R.id.UserAddressTextView);
//        UserPhoneTextView = rootView.findViewById(R.id.UserPhoneTextView);
//        UserIntroductionTextView = rootView.findViewById(R.id.UserIntroductionTextView);
//        UserRateTextView = rootView.findViewById(R.id.UserRateTextView);
        UserRatingBar = rootView.findViewById(R.id.ratingBar);
        UserAvatarImageView = rootView.findViewById(R.id.AvatarImageView);
        EditButton =  rootView.findViewById(R.id.EditButton);
        button_mypost = rootView.findViewById(R.id.button_mypost);
        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(getUid());
        // [END create_database_reference]
        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserEditActivity.class));
            }
        });
        button_mypost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyPostActivity.class));
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
                    UserEmailTextView.setText("Email: "+user.email);
//                    UserAddressTextView.setText("Address: "+user.address);
//                    UserPhoneTextView.setText("Phone: "+user.phone);
//                    UserIntroductionTextView.setText('"'+user.intro+'"');
//                    UserRateTextView.setText("Rate: "+Double.toString(user.rate)+"/5");
                    UserRatingBar.setRating((float)user.rate);
                    if(user.avatar!=null) UserAvatarImageView.setImageBitmap(user.getAvatar());
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
