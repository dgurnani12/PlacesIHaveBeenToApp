package com.example.dgurnani.placesihavebeentoapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    public LocationListener locationListener;
    public LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();

        if(intent.getIntExtra("placeNumber",0)==0) {

            // initialization list-entry case:
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location)  {
                    centerMapOnLocation(location, "current location");
                }
                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                    // do nothing
                }
                @Override
                public void onProviderEnabled(String s) {
                    // do nothing
                }
                @Override
                public void onProviderDisabled(String s) {
                    // do nothing
                }
            };

            if (Build.VERSION.SDK_INT < 23) {
                // avoids some warnings:
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            } else {
                // check and ask permission:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    centerMapOnLocation(lastKnownLocation, "current location");

                } else {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                }
            }

        } else {

            // default list-entry case:
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MainActivity.locations.get(intent.getIntExtra("placeNumber", 0)).latitude);
            placeLocation.setLongitude(MainActivity.locations.get(intent.getIntExtra("placeNumber", 0)).longitude);

            centerMapOnLocation(placeLocation, MainActivity.places.get(intent.getIntExtra("placeNumber", 0)));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // check just in case:
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLocation(lastKnownLocation, "current location");

            }
        }

    }

    // TODO: fix this bug:
    public void centerMapOnLocation(Location location, String title) {

        //LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        //mMap.clear();

        //if (title != "current location") {
            //mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        //}

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));

    }

    @Override
    public void onMapLongClick(LatLng point) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";

        try {
            List<Address> listAddresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);

            if (listAddresses != null && listAddresses.size() > 0) {
                if (listAddresses.get(0).getThoroughfare() != null) {
                    if (listAddresses.get(0).getSubThoroughfare() != null) {
                        address += listAddresses.get(0).getSubThoroughfare() + " ";
                    }
                    address += listAddresses.get(0).getThoroughfare();
                }
            }

        } catch (IOException e) {

            e.printStackTrace();

        }

        if (address.equals("")) {

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-MM-dd");
            address = sdf.format(new Date());

        }

        mMap.addMarker(new MarkerOptions().position(point).title(address));

        MainActivity.places.add(address);
        MainActivity.locations.add(point);
        MainActivity.arrayAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show();
    }

}
