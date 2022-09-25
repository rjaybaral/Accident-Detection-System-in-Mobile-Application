package com.example.epocketemergency;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.epocketemergency.Model.User_Info;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowMapLocationUserActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private boolean meet = true;
    private Marker userLocationMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map_location_user);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        FirebaseDatabase.getInstance().getReference("User_Info").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User_Info user_info = snapshot.getValue(User_Info.class);
                    LatLng latLng = new LatLng(Double.parseDouble(user_info.getLatitude()), Double.parseDouble(user_info.getLongitude()));
                    if (userLocationMarker == null) {
                        MarkerOptions p = new MarkerOptions().position(latLng).title("I'm here!");
//                        p.rotation(Float.parseFloat(user_info.getDirectionFront()));
//                        p.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon));
                        userLocationMarker = mMap.addMarker(p);

                    } else {
                        userLocationMarker.setPosition(latLng);
//                        userLocationMarker.setRotation(Float.parseFloat(user_info.getDirectionFront()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                    if (meet) {
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(user_info.getLatitude()), Double.parseDouble(user_info.getLongitude())), 17);
                        mMap.animateCamera(cameraUpdate);
                    }
                    meet = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}