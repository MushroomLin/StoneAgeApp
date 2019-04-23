package com.example.miniresearchdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.miniresearchdatabase.fragment.MessageListFragment;
import com.example.miniresearchdatabase.fragment.UserInformationFragment;
import com.example.miniresearchdatabase.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.example.miniresearchdatabase.R;
import com.example.miniresearchdatabase.fragment.MyPostsFragment;
import com.example.miniresearchdatabase.fragment.MyTopPostsFragment;
import com.example.miniresearchdatabase.fragment.RecentPostsFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Based on the quickstart codes provided by Google.
 * https://github.com/firebase/quickstart-android/tree/7fe25b8f403f48045983680148958a2759d75e61/database
 *
 * Add the Google sign-in function.
 * Linked this app to Firebase database.
 * All user information and posts will be stored in my Firebase database.
 *
 * In order to use, please sign up a new account or use Google account to login.
 * Click the post icon in the bottom-right corner to post your offers.
 * View all the offers in the discover tab and view your own offers in My Offers or My top Offers tab.
 * Click the top-right corner to sign-out.
 */
public class  MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";
    private DatabaseReference mDatabase;
    private double minPrice = 999999999.99;
    private double maxPrice = 0.0;

    Fragment currentFragment = null;
    FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        final String userId = getUid();
//        mDatabase = mDatabase.child("user-posts").child(userId);
//
//        mDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.e("ooo", "enter onDataChange");
//                List<Double> avgPriceList = new LinkedList<>();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    Post post = postSnapshot.getValue(Post.class);
//                    List<Double> pricesList = post.estimatedPrices;
//                    if(pricesList != null) {
//                        double priceSum = 0.0;
//                        for(int i = 0; i < pricesList.size(); i++) {
//                            double curPrice = pricesList.get(i);
//                            priceSum += curPrice;
//                        }
//                        double priceAvg = priceSum / pricesList.size();
//                        // update minPrice and maxPrice
//                        if (priceAvg < minPrice)
//                            minPrice = priceAvg;
//                        if (priceAvg > maxPrice)
//                            maxPrice = priceAvg;
//                        avgPriceList.add(priceAvg);
//                        Log.e("pricequery", String.valueOf(priceAvg));
//                        Log.e("pricequery", "minPrice:"+String.valueOf(minPrice)+" maxPrice:"+String.valueOf(maxPrice));
//                        Log.e("pricequery", "--------------------------");
//                    }
//                }
//                // if the range of minPrice and maxPrcie isn't big enough
//                if (minPrice - 20.0 > 0.0)
//                    minPrice = minPrice - 20.0;
//                else
//                    minPrice = 0.0;
//                maxPrice = maxPrice + 20.0;
////                avgPricesList = avgPriceList;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });



        ft = getSupportFragmentManager().beginTransaction();
        currentFragment = new RecentPostsFragment();
//        Bundle bundle = new Bundle();
//        bundle.putDouble("minPrice", minPrice);
//        bundle.putDouble("maxPrice", maxPrice);
//        Log.e("ooo", String.valueOf(minPrice));
//        Log.e("ooo", String.valueOf(maxPrice));
//        currentFragment.setArguments(bundle);
        ft.replace(R.id.container, currentFragment);
        ft.commit();
        // Set up the BottomNavigationView
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return true;
        }
        else if (i == R.id.action_setting){
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                currentFragment = new RecentPostsFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putDouble("minPrice", minPrice);
                bundle1.putDouble("maxPrice", maxPrice);
                Log.e("ooo", String.valueOf(minPrice)+" "+String.valueOf(maxPrice));
                currentFragment.setArguments(bundle1);
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, currentFragment);
                ft.commit();
                break;
            case R.id.navigation_map:
                startActivity(new Intent(this, MapsActivity.class));
//                startActivityForResult(new Intent(MainActivity.this, MapsActivity_selectAddress.class), 1);
//                startActivity(new Intent(this, MapsActivity_selectAddress.class));
                break;
            case R.id.navigation_post:
                startActivity(new Intent(MainActivity.this, NewPostActivity.class));
                break;
            case R.id.navigation_message:
                currentFragment = new MessageListFragment();
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, currentFragment);
                ft.commit();

                break;
            case R.id.navigation_me:
                currentFragment = new UserInformationFragment();
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, currentFragment);
                ft.commit();
                break;
            default:
                return false;
        }
        return true;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        String selectAddress = data.getExtras().getString("selectAddress"); //get the data from new Activity when it finished
//        Toast.makeText(this, "select location:\n" + selectAddress, Toast.LENGTH_SHORT).show();
//    }
}