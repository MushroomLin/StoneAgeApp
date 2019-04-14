package com.example.miniresearchdatabase.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.miniresearchdatabase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.miniresearchdatabase.models.User;

public class UserInformationFragment extends Fragment {

    private static final String TAG = "UserInformationFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]


    private OnFragmentInteractionListener mListener;
    private TextView UserNameTextView;
    private TextView UserSignTextView;
//    private TextView UserInterestingTextView;
//    private RatingBar ratingBar;
//    private TextView ScoreTextView;
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
        Log.w("TAG", UserNameTextView.getText().toString());
        UserSignTextView = rootView.findViewById(R.id.UserSignTextView);
        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(getUid());
        // [END create_database_reference]
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Serialize retrieved data to a User object
                User user = dataSnapshot.getValue(User.class);
                //Now you have an object of the User class and can use its getters like this
                if (user!=null){
                    UserNameTextView.setText(user.username);
                    UserSignTextView.setText(user.email);
                    Log.w("TAG", user.email);
                    Log.w("TAG", user.username);
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
