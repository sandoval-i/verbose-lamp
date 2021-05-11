package com.example.taller3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taller3.databinding.ActivityUserTrackingAcitivityBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class UserTrackingAcitivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private ActivityUserTrackingAcitivityBinding binding;

    private final int LOCATION_PERMISSION_CODE = 1;
    private final int REQUEST_LOCATION_SETTINGS = 1;
    private LocationRequest locationRequest = null;
    private LocationCallback locationCallback = null;
    private FusedLocationProviderClient fusedLocationProviderClient = null;
    private Marker ownMarkerLastLocation = null;
    private Marker objectiveMarkerLastLocation = null;

    private String objectiveUid;
    private EditText distanciaEditText;

    private final ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            if (objectiveMarkerLastLocation != null) objectiveMarkerLastLocation.remove();
            double latitud = Double.parseDouble(snapshot.child("latitud").getValue().toString());
            double longitud = Double.parseDouble(snapshot.child("longitud").getValue().toString());
            objectiveMarkerLastLocation = map.addMarker(new MarkerOptions()
                    .position(new LatLng(latitud, longitud))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                    .title(null));
            updateDistance();
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserTrackingAcitivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationRequest = new LocationRequest().setInterval(1000).setFastestInterval(500)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    if (ownMarkerLastLocation != null) ownMarkerLastLocation.remove();
                    ownMarkerLastLocation = map.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude())).title(null));
                    updateDistance();
                }
            }
        };

        objectiveUid = getIntent().getStringExtra("uid");
        distanciaEditText = findViewById(R.id.distanciaEditText);
        Log.i("LOL", "Haciendo seguimiento a " + objectiveUid);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LogInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (!PermissionHelper.hasPermission(this, permissions)) {
            PermissionHelper.requestPermission(this, permissions, LOCATION_PERMISSION_CODE);
            return;
        }
        if (map == null) return;

        LocationSettingsRequest.Builder builder = new
                LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        client.checkLocationSettings(builder.build()).addOnSuccessListener(locationSettingsResponse -> {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            FirebaseDatabase.getInstance().getReference(PATHSDB.USERS).child(objectiveUid).addValueEventListener(eventListener);
        }).addOnFailureListener(e -> {
            int statusCode = ((ApiException) e).getStatusCode();
            if (statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
                try {
                    ((ResolvableApiException) e).
                            startResolutionForResult(UserTrackingAcitivity.this, REQUEST_LOCATION_SETTINGS);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateDistance() {
        if (ownMarkerLastLocation == null || objectiveMarkerLastLocation == null) return;
        distanciaEditText.setText(Utils.calculateDistance(ownMarkerLastLocation.getPosition(), objectiveMarkerLastLocation.getPosition()) + " KM");
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        FirebaseDatabase.getInstance().getReference(PATHSDB.USERS).child(objectiveUid).removeEventListener(eventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION_SETTINGS) {
            if (resultCode == RESULT_OK) {
                startLocationUpdates();
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        startLocationUpdates();
    }
}