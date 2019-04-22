package com.example.miniresearchdatabase;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

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

import java.util.Arrays;

public class MapsActivity_selectAddress extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker mVisitingMarker = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_select_address);


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
                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14.0f));
                if (mVisitingMarker == null)
                    mVisitingMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                else
                    mVisitingMarker.setPosition(place.getLatLng());
                mVisitingMarker.setTitle(place.getAddress());
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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mVisitingMarker.setPosition(latLng);
                String address = (String) new LocationToAddress().getAddress(latLng.latitude, latLng.longitude);
                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                mVisitingMarker.setTitle(address);
//                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.getPosition();
                return false;
            }
        });

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
}
