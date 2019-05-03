package com.example.miniresearchdatabase;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;

import java.util.Arrays;

import static android.view.View.*;

/**
 * Reference: https://developers.google.com/maps/documentation/android-sdk
 */

public class MapsActivity_selectAddress extends AppCompatActivity
        implements
        OnMapReadyCallback,
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    // initialize the map and select marker
    private GoogleMap mMap;
    private Marker mVisitingMarker = null;
    private String selectAddress = "";
    private Button confirm;

    // permission set
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_select_address);

        confirm = findViewById(R.id.bt_confirm);
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // create new Intent to return data
                Intent intent = new Intent();
                intent.putExtra("selectAddress", selectAddress);
                // set return value
                MapsActivity_selectAddress.this.setResult(RESULT_OK, intent);
                // close Activity
                MapsActivity_selectAddress.this.finish();
            }
        });

        // initialize api key
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCaUcdLLm4ifpW9ZYMhcCm_6RMvArAz-hA");
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS));

        // set event listener for autocomplete search bar
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place place) {
                // initialize the camera position
                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14.0f));
                // set marker position for search
                if (mVisitingMarker == null) {
                    mVisitingMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    mVisitingMarker.setVisible(true);
                }
                else {
                    mVisitingMarker.setPosition(place.getLatLng());
                    mVisitingMarker.setVisible(true);
                }
                // set title and info for the marker
                mVisitingMarker.setTitle(place.getAddress());
                selectAddress = place.getAddress();
                Toast.makeText(MapsActivity_selectAddress.this, getString(R.string.select_location) +" "+ selectAddress, Toast.LENGTH_SHORT).show();
//                // change the color of the marker
//                Marker tem = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14.0f));
//                if (lastLocation != null) {
//                    // once enter a new address the last marker will disappear
//                    lastLocation.setVisible(false);
//                }
//                lastLocation = tem;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.e("Map", "An error occurred: " + status);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // initialize the marker
        mVisitingMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(0.0, 0.0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mVisitingMarker.setVisible(false);

        // tap on map then a blue will be drawn and show the address
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String address = new LocationToAddress().getAddress(latLng.latitude, latLng.longitude);
                selectAddress = address;
                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                mVisitingMarker.setPosition(latLng);
                mVisitingMarker.setVisible(true);
                mVisitingMarker.setTitle(address);
                Toast.makeText(MapsActivity_selectAddress.this, "select location:\n" + address, Toast.LENGTH_SHORT).show();
//                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MapsActivity_selectAddress.this, "location:\n" + marker.getTitle(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // initialize listener for users locate
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.3496, -71.0997), 14.0f));


//        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
//            @Override
//            public void onMarkerDragStart(Marker arg0) {
//            }
//
//            @SuppressWarnings("unchecked")
//            @Override
//            public void onMarkerDragEnd(Marker arg0) {
//                Log.d("System out", "onMarkerDragEnd...");
//                mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
//            }
//
//            @Override
//            public void onMarkerDrag(Marker arg0) {
//            }
//        });
    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(MapsActivity_selectAddress.this, LOCATION_PERMISSION_REQUEST_CODE,
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
                double currentLatitude = mMap.getCameraPosition().target.latitude;
                double currentLongitude = mMap.getCameraPosition().target.longitude;
                String address = new LocationToAddress().getAddress(currentLatitude, currentLongitude);
                selectAddress = address;
                mVisitingMarker.setPosition(new LatLng(currentLatitude, currentLongitude));
                mVisitingMarker.setVisible(true);
                mVisitingMarker.setTitle(selectAddress);
//                Toast.makeText(MapsActivity_selectAddress.this, "Current location:\n" + address, Toast.LENGTH_SHORT).show();
                //Toast.makeText(MapsActivity.this, "Current location:\n" + mMap.getCameraPosition().target.latitude, Toast.LENGTH_LONG).show();
            }
        });
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

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
}
