package com.example.miniresearchdatabase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.miniresearchdatabase.R;
import com.example.miniresearchdatabase.models.Post;
import com.example.miniresearchdatabase.models.User;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import rebus.bottomdialog.BottomDialog;

public class NewPostActivity extends BaseActivity {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";

    // declare database_ref
    private DatabaseReference mDatabase;


    private EditText mTitleField;
    private EditText mAddressField;
    private FloatingActionButton mSubmitButton;
    private FloatingActionButton mCencelButton;
    // -------
    private final int PICK_IMAGE_REQUEST = 71;
    private final int SELECT_ADDRESS_ON_MAP = 118;
    private final int CAMERA_REQUEST = 1888;
    private ImageButton button_selectAddress;
    private ImageView imageView;
    private EditText editText_description;
    private String address = "";
    private BottomDialog dialog;
    private Uri filePath;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        // initialize database_ref
        mDatabase = FirebaseDatabase.getInstance().getReference();


        mTitleField = findViewById(R.id.fieldTitle);
        mSubmitButton = findViewById(R.id.fabSubmitPost);
        mCencelButton = findViewById(R.id.fabCancelPost);
        mAddressField = findViewById(R.id.addressEditText);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
        mCencelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button_selectAddress = findViewById(R.id.selectAddressBtn);
        imageView = findViewById(R.id.uploadImageView);
        editText_description = findViewById(R.id.editText_description);
        dialog = new BottomDialog(NewPostActivity.this);
        dialog.canceledOnTouchOutside(true);
        dialog.cancelable(true);
        dialog.inflateMenu(R.menu.menu_choose_picture);
        dialog.setOnItemSelectedListener(new BottomDialog.OnItemSelectedListener() {
            @Override
            public boolean onItemSelected(int id) {
                switch (id) {
                    case R.id.action_choose:
                        chooseImage();
                        return true;
                    case R.id.action_new:
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        return true;
                    default:
                        return false;
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }

        button_selectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(NewPostActivity.this, MapsActivity_selectAddress.class), SELECT_ADDRESS_ON_MAP);
            }
        });
    }

    private void chooseImage() {
        // set up intent to choose a picture from phone
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private String convertImage() {
        String data;
        if(bitmap != null)
        {
            data = ImageUtils.bitmapToString(bitmap);
        }
        else data = null;
        return data;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // get image
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null)
        {
            Log.w("TAG", data.toString());
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
        // get address
        else if (requestCode == SELECT_ADDRESS_ON_MAP && resultCode == RESULT_OK && data != null) {
            address = data.getExtras().getString("selectAddress"); //get the data from new Activity when it finished
            mAddressField.setText(address);
        }
    }

    private void submitPost() {
        final String title = mTitleField.getText().toString();
        final String address = mAddressField.getText().toString();

        final String description = editText_description.getText().toString();
        final String originalType = "originalType";
        final String targetType = "targetType";
        final String status = "open";

        // Title is required
        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return;
        }


        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();




//        getQuery2(mDatabase);



        // single value read
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

//                        double[] estimatedPrices = new HttpRequest().request(title);
                        EbayConnection ebay = new EbayConnection();
                        String response;
                        try{
                            response = ebay.sendGet(title,10);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                            response = null;
                        }
                        List<Double> pricesList = ebay.getPrices(response);

                        double avgPrice = 0.0;
                        if(pricesList != null) {
                            double priceSum = 0.0;
                            for (int i = 0; i < pricesList.size(); i++) {
                                double curPrice = pricesList.get(i);
                                priceSum += curPrice;
                            }
                            avgPrice = priceSum / pricesList.size();
                        }


                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            String picture = convertImage();
                            if (picture == null) {
                                picture = "";
                            }
                            writeNewPost(userId, user.username, title, address,
                                    description, originalType, targetType, picture, pricesList, avgPrice, status);

                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
    }

    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.show();
        } else {
            mSubmitButton.hide();
        }
    }

    // write_fan_out
    private void writeNewPost(String userId, String username, String title,
                              String address,
                              String description, String originalType, String targetType, String picture,
                              List<Double> estimatedPrices, double avgPrice, String status) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

        try {
            String key = mDatabase.child("posts").push().getKey();
            Post post = new Post(userId, username, title, address,
                    description, originalType, targetType, picture, estimatedPrices, avgPrice, status);
            Map<String, Object> postValues = post.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/posts/" + key, postValues);
            childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

            mDatabase.updateChildren(childUpdates);
        } catch(Exception e) {
            Toast.makeText(NewPostActivity.this, e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

    }

    public Query getQuery2(DatabaseReference databaseReference) {
        final String userId = getUid();
        DatabaseReference databaseReference2 = databaseReference.child("user-posts").child(userId);

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String titles = "";
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    titles += post.title + ",";

                }
                Log.e("testQuery2", titles);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return null;

    }



}

/*
class HttpRequest {

    private final String TOKEN = "CUJYSOVFIARKJWJXSXSBEFQIVEDPZUXQILFCXHPSNLEMOFGYQTIYTJGCDPDWNECZ";



    private String firstURL = null;
    private String secondURL = null;
    private String thirdURL = null;

    public double[] request(String itemName){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        firstURL = "https://api.priceapi.com/v2/jobs?token=" + TOKEN + "&source=google_shopping&country=us&topic=product_and_offers&key=term&values=" + itemName;

        try{

            // first step
            HttpEntity firstEntity = GetPostUrl.post(firstURL);

            if (firstEntity != null) { // success

                String jobIdValue = getValue(firstEntity, "job_id");

                // second step
                secondURL = "https://api.priceapi.com/v2/jobs/" + jobIdValue + "?token=" + TOKEN;
                thirdURL = "https://api.priceapi.com/v2/jobs/" + jobIdValue + "/download?token=" + TOKEN;
                HttpEntity secondEntity = null;
                while(true) {
                    secondEntity = GetPostUrl.get(secondURL);
                    if(secondEntity == null) {
                        return new double[0];
                    } else {
                        Thread.sleep(1000);
                        String secondStatusValue = getValue(secondEntity, "status");

                        if(secondStatusValue == null) {
                            return new double[0];
                        }
                        if(secondStatusValue.equals("finished")) {
                            break;
                        }
                    }
                }

                // third step

                HttpEntity thirdEntity = GetPostUrl.get(thirdURL);
                String thirdEntityString = EntityUtils.toString(thirdEntity);
                JSONObject thirdObject = new JSONObject(thirdEntityString);

//                        JSONObject object = thirdObject.getJSONObject("results");      //通过getString("cartypes")取出里面的信息
                JSONArray resultsArray = thirdObject.getJSONArray("results");
                JSONObject resultsObject = resultsArray.getJSONObject(0);
                JSONObject contentObject = resultsObject.getJSONObject("content");
                JSONArray offersArray = contentObject.getJSONArray("offers");

                double[] prices = new double[offersArray.length()];
                for(int i = 0; i < offersArray.length(); i++) {
                    JSONObject offerObject = offersArray.getJSONObject(i);
                    String price = offerObject.getString("price");
                    prices[i] = Double.valueOf(price);
                }

//                if(count >= 1) {
//                    JSONObject offerObject0 = offersArray.getJSONObject(0);
//                    editText_name1.setText(offerObject0.getString("shop_name"));
//                    editText_url1.setText(offerObject0.getString("url"));
//                    editText_price1.setText(offerObject0.getString("price"));
//                }
//
//                if(count >= 2) {
//                    JSONObject offerObject1 = offersArray.getJSONObject(1);
//                    editText_name2.setText(offerObject1.getString("shop_name"));
//                    editText_url2.setText(offerObject1.getString("url"));
//                    editText_price2.setText(offerObject1.getString("price"));
//                }

            }
        } catch(Exception e) {
            return new double[0];
        }

        return new double[0];

    }

    private String getValue (HttpEntity entity, String key) {
        String value = null;
        try {
            String entityString = EntityUtils.toString(entity);
            JSONObject jsonObject = new JSONObject(entityString);
            value = jsonObject.getString(key);
        } catch (Exception e) {
            value = null;
        }
        return value;
    }


}

*/