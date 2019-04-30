package com.example.miniresearchdatabase;


import com.example.miniresearchdatabase.models.Post;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;

import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class MapsActivity extends AppCompatActivity
        implements
        OnInfoWindowClickListener,
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

//    private SeekBar sbDistance;
    private GoogleMap mMap;
    private int restrictDistance = 2000;
    // used to set multiple markers on the map
    private MarkerOptions options = new MarkerOptions();
    private Query mPostReference;
    private HashMap<Marker, String> marker2post = new HashMap<Marker, String>();
    private HashMap<String, Marker> post2marker = new HashMap<String, Marker>();
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    Location mLastKnownLocation;

    private Marker lastLocation = null;
    private int onlyShowNearby = 0; // click my location button again will show the far away posts
    private boolean savedState = false; // To record whether this activity has been initialized yet
    private final String KEY_LOCATION = "loc";
    CircleOptions circleOptions = new CircleOptions();
    Circle circleAddress = null;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

//        sbDistance = findViewById(R.id.seekbar_distance);
//        sbDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                restrictDistance = progress;
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                drawCircle(lastLocation.getPosition(), restrictDistance);
//                Toast.makeText(MapsActivity.this, "restrict distance: "+String.valueOf(restrictDistance)+" meters", Toast.LENGTH_SHORT).show();
//            }
//        });

        // initialize api key
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCaUcdLLm4ifpW9ZYMhcCm_6RMvArAz-hA");
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place place) {
                if (lastLocation == null) {
                    lastLocation = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }
                else {
                    lastLocation.setPosition(place.getLatLng());
                }
                lastLocation.setVisible(true);
                lastLocation.setTitle(place.getAddress());
                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 13.7f));
//                drawCircle(place.getLatLng(), restrictDistance);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.e("Map", "An error occurred: " + status);
            }
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentByTag("map_tag");

        mapFragment.getMapAsync(this);

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            savedState = true;
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        // initialize lastLocation marker
        lastLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(0.0, 0.0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        lastLocation.setVisible(false);

        // when click on map, a blue marker will be added there
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String address = new LocationToAddress().getAddress(latLng.latitude, latLng.longitude);
                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng, 13.7f));
                lastLocation.setPosition(latLng);
                lastLocation.setVisible(true);
                lastLocation.setTitle(address);
//                drawCircle(latLng, restrictDistance);
                Toast.makeText(MapsActivity.this, "select location:\n" + address, Toast.LENGTH_SHORT).show();
//                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                Toast.makeText(MapsActivity.this, "location:\n" + marker.getTitle(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // initialize listener for users locate
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        enableMyLocation();


        if (savedState) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 14.0f));
        }
        else {
            // set default map location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.3496, -71.0997), 14.0f));
        }
        // connect to firebase for posts collection
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("posts").orderByChild("status").equalTo("open");

        // get posts and extract some key info
        mPostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    // get the key of current post and store for future use
                    DatabaseReference marker2postReference =  postSnapshot.getRef();
                    final String postkey = marker2postReference.getKey();
                    // get current position for distance computing
                    double postLatitude = post.latitude;
                    double postLongitude = post.longitude;
                    if (!marker2post.containsValue(postkey)) {
                        LatLng postPoint = new LatLng(postLatitude, postLongitude);
                        options.position(postPoint);
                        options.title(post.title);
                        options.snippet(post.description);
                        Marker mId = mMap.addMarker(options);
                        // store marker id and post key for future use
                        // Log.w("marker", mId.getId() + " " + postkey);
                        marker2post.put(mId, postkey);
                        post2marker.put(postkey, mId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).

        // when users enable their location, this method will read the
        // posts from firebase then show nearby posts on the map
        // get the self-location info
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // get current location
                currentLatitude = mMap.getCameraPosition().target.latitude;
                currentLongitude = mMap.getCameraPosition().target.longitude;
                Log.w("distance", String.valueOf(currentLatitude));
                Log.w("distance", String.valueOf(currentLongitude));
                //Toast.makeText(MapsActivity.this, "Current location:\n" + mMap.getCameraPosition().target.latitude, Toast.LENGTH_LONG).show();
            }
        });

        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
            mPostReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Post post = postSnapshot.getValue(Post.class);
                        double postLatitude = post.latitude;
                        double postLongitude = post.longitude;

                        // judge whether this post is near to current location or not
                        if (!computeDistance(currentLatitude, currentLongitude, postLatitude, postLongitude, restrictDistance)) {
                            // get the key of current post and store for future use
                            DatabaseReference marker2postReference = postSnapshot.getRef();
                            final String postkey = marker2postReference.getKey();
                            if (marker2post.containsValue(postkey)) {
                                if (onlyShowNearby % 2 == 0)
                                    post2marker.get(postkey).setVisible(false);
                                else
                                    post2marker.get(postkey).setVisible(true);
                            } else {
                                // if near the current then draw the marker on the map
                                LatLng postPoint = new LatLng(postLatitude, postLongitude);
                                options.position(postPoint);
                                options.title(post.title);
                                options.snippet("Author: " + post.author + " Address: " + post.address
                                        + " " + post.description);
                                mMap.addMarker(options);
                                Marker mId = mMap.addMarker(options);
                                // store marker id and post key for future use
                                marker2post.put(mId, postkey);
                                post2marker.put(postkey, mId);
                                //Log.w("marker", mId.getId() + " " + postkey);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            ++onlyShowNearby;
    }


    /*
     * when users click the info window of a marker, this click will
     * direct users to the postDetail page of the stuff so that users
     * can add a comment to the stuff or review the comment of the stuff
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        if (marker2post.containsKey(marker)) {
            Log.w("marker", marker.getId() + " " + marker2post.get(marker));
            String postKey = marker2post.get(marker);
            Intent intent = new Intent(MapsActivity.this, PostDetailActivity.class);
            intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
            startActivity(intent);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_LOCATION, mMap.getMyLocation());
            super.onSaveInstanceState(outState);
        }
    }


    // these codes are for computing distance of two locations
    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * compute distance (meters)
     */
    private boolean computeDistance(double lat1, double lng1, double lat2,
                                    double lng2, double distanceRestrict) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s*1000;

//        Log.w("distance", String.valueOf(s));
        if (s > distanceRestrict) return false;
        else return true;
    }

    private void drawCircle(LatLng point, int distance){

        // Instantiating CircleOptions to draw a circle around the marker
        circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(distance);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // remove last circle
        if (circleAddress != null)
            circleAddress.setVisible(false);

        // Adding the circle to the GoogleMap
        circleAddress = mMap.addCircle(circleOptions);
    }
}

