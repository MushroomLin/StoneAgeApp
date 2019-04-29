package com.example.miniresearchdatabase;

import android.content.Intent;
import android.content.res.Configuration;
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
import com.example.miniresearchdatabase.fragment.PostListFragment;
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

        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        ft = getSupportFragmentManager().beginTransaction();
        currentFragment = new RecentPostsFragment();
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
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, currentFragment);
                ft.commit();
                break;
            case R.id.navigation_map:
                startActivity(new Intent(this, MapsActivity.class));
//                startActivity(new Intent(this, SettingActivity.class));
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