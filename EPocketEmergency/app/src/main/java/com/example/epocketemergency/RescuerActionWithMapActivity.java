package com.example.epocketemergency;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.epocketemergency.Model.Message_Info;
import com.example.epocketemergency.Model.User_Info;
import com.example.epocketemergency.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RescuerActionWithMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Context context = this;
    private Intent intent;
    private String ID_response = "", ID_otw = "", Type_Rescue = "", Latitude = "", Longitude = "";
    private boolean meet = true;
    private Marker userLocationMarker = null;
    private ImageView back_button, message_button;
    private TextView count_message_notif, fullname_text, type_text;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private ImageView profileImage;
    private String profileURIString = "";
    private LinearLayout clickMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescuer_action_with_map);
        intent = getIntent();
        ID_response = intent.getStringExtra("ID_response");
        ID_otw = intent.getStringExtra("ID_otw");
        Type_Rescue = intent.getStringExtra("Type_Rescue");
        Latitude = intent.getStringExtra("Latitude");
        Longitude = intent.getStringExtra("Longitude");
        count_message_notif = findViewById(R.id.count_message_notif);
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> finish());
        clickMe = findViewById(R.id.clickMe);
        fullname_text = findViewById(R.id.fullname_text);
        type_text = findViewById(R.id.type_text);
        profileImage = findViewById(R.id.profileImage);
        profileImage.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfilePictureShowActivity.class);
            intent.putExtra("Profile_URL", profileURIString);
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(profileImage, "image1");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
            startActivity(intent, options.toBundle());
        });
        message_button = findViewById(R.id.message_button);
        message_button.setOnClickListener(view -> {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("ID_other", ID_response);
            startActivity(intent);
        });

        clickMe.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfileSpecificUserActivity.class);
            intent.putExtra("ID_user", ID_response);
            context.startActivity(intent);
        });

        getInformationUser(ID_response);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getCountMessage();
    }

    private void getInformationUser(String id_response) {
        FirebaseDatabase.getInstance().getReference("User_Info").child(id_response)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            User_Info user_info = snapshot.getValue(User_Info.class);
                            profileURIString = user_info.getProfile_URL();
                            try{
                                Picasso.get().load(Uri.parse(user_info.getProfile_URL())).into(profileImage);
                            }catch (Exception ex){
                                profileImage.setImageResource(R.drawable.pic);
                            }
                            fullname_text.setText((user_info.getGender().equals("Male")? "Sir. " : "Ma'am. ")+ user_info.getFirst_Name()+" "+user_info.getLast_Name());
                            type_text.setText("Responder!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getCountMessage() {
        FirebaseDatabase.getInstance().getReference("Message_Info").child(firebaseUser.getUid())
                .child(ID_response)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            int count = 0;
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                Message_Info message_info = snapshot1.getValue(Message_Info.class);
                                if (message_info.getReceiver().equals(firebaseUser.getUid()) && message_info.getMessage_Seen().equals("Seen")) {
                                    count++;
                                }

                            }
                            if (count != 0) {
                                count_message_notif.setText("" + count);
                                count_message_notif.setVisibility(View.VISIBLE);
                            } else {
                                count_message_notif.setText("0");
                                count_message_notif.setVisibility(View.GONE);
                            }
                        } else {
                            count_message_notif.setText("0");
                            count_message_notif.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        FirebaseDatabase.getInstance().getReference("User_Info").child(ID_response).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User_Info user_info = snapshot.getValue(User_Info.class);
                    LatLng latLng = new LatLng(Double.parseDouble(user_info.getLatitude()), Double.parseDouble(user_info.getLongitude()));
                    if (userLocationMarker == null) {
                        MarkerOptions p = new MarkerOptions().position(latLng).title(Type_Rescue);
//                        p.rotation(Float.parseFloat(user_info.getDirectionFront()));
                        if (Type_Rescue.equals("Ambulance")) {
                            p.icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance_icon_map));
                        } else if (Type_Rescue.equals("Firefighter")) {
                            p.icon(BitmapDescriptorFactory.fromResource(R.drawable.firefighter_icon_map));
                        } else if (Type_Rescue.equals("Police")) {
                            p.icon(BitmapDescriptorFactory.fromResource(R.drawable.policeman_icon_map));
                        }
                        userLocationMarker = mMap.addMarker(p);
                        LatLng s = new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude));
                        MarkerOptions p1 = new MarkerOptions().position(s).title("I'm here");
                        mMap.addMarker(p1);

                    } else {
                        userLocationMarker.setPosition(latLng);
//                        userLocationMarker.setRotation(Float.parseFloat(user_info.getDirectionFront()));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(new LatLng(latLng.latitude, latLng.longitude));
                        builder.include(new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude)));
                        LatLngBounds bounds = builder.build();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 400));
                    }
                    if (meet) {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(new LatLng(latLng.latitude, latLng.longitude));
                        builder.include(new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude)));
                        LatLngBounds bounds = builder.build();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 400));
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