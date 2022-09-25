package com.example.epocketemergencyresponder;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.epocketemergencyresponder.Model.Message_Info;
import com.example.epocketemergencyresponder.Model.User_Info;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class RescuerActionWithMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Context context = this;
    private Intent intent;
    private String ID_user = "", ID_otw = "", Type_Rescue = "", Latitude = "", Longitude = "",
            ID_emergency = "", Victim_Fullname = "";
    private boolean meet = true;
    private Marker userLocationMarker = null;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private ImageView back_button, message_button;
    private TextView count_message_notif;
    private ImageView profileImage, done_button;
    private TextView fullname_text, address_text;
    private LinearLayout clickMe;
    private String profileURIString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescuer_action_with_map);
        intent = getIntent();
        ID_user = intent.getStringExtra("ID_user");
        ID_otw = intent.getStringExtra("ID_otw");
        Type_Rescue = intent.getStringExtra("Type_Rescue");
        Latitude = intent.getStringExtra("Latitude");
        Longitude = intent.getStringExtra("Longitude");
        ID_emergency = intent.getStringExtra("ID_emergency");
        count_message_notif = findViewById(R.id.count_message_notif);
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> finish());
        clickMe = findViewById(R.id.clickMe);
        fullname_text = findViewById(R.id.fullname_text);
        address_text = findViewById(R.id.address_text);
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
            intent.putExtra("ID_other", ID_user);
            startActivity(intent);
        });

        clickMe.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfileSpecificUserActivity.class);
            intent.putExtra("ID_user", ID_user);
            context.startActivity(intent);
        });

        done_button = findViewById(R.id.done_button);
        done_button.setOnClickListener(view -> {
            //Gagawa na ng Dialog para sa submit report and done rescue!
            Dialog myDialog = new Dialog(context);
            View view1 = LayoutInflater.from(context).inflate(R.layout.dialog_submit_report, null);
            TextView cancelBtn = view1.findViewById(R.id.cancelBtn);
            cancelBtn.setOnClickListener(view2 -> myDialog.dismiss());
            TextView reportBtn = view1.findViewById(R.id.reportBtn);
            reportBtn.setOnClickListener(view22 -> {
                FirebaseDatabase.getInstance().getReference("Submit_Report_Info").orderByChild("ID_otw")
                        .equalTo(ID_otw).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!(snapshot.exists())) {
                                    myDialog.dismiss();
                                    Intent intent = new Intent(context, SubmitReportActivity.class);
                                    intent.putExtra("ID_user", ID_user);
                                    intent.putExtra("ID_other", firebaseUser.getUid());
                                    intent.putExtra("ID_otw", ID_otw);
                                    intent.putExtra("ID_emergency", ID_emergency);
                                    intent.putExtra("Victim_Fullname", Victim_Fullname);
                                    intent.putExtra("Latitude", Latitude);
                                    intent.putExtra("Longitude", Longitude);
                                    intent.putExtra("Type_Rescue", Type_Rescue);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(context, "Sorry, You are already submit report!", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            });
            TextView successBtn = view1.findViewById(R.id.successBtn);
            successBtn.setOnClickListener(view23 -> FirebaseDatabase.getInstance().getReference("Submit_Report_Info").orderByChild("ID_otw")
                    .equalTo(ID_otw)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("");
                                HashMap<String, Object> parameters = new HashMap<>();
                                parameters.put("OTW_Info/" + ID_otw + "/ID_response", "Done_" + firebaseUser.getUid());
                                parameters.put("OTW_Info/" + ID_otw + "/ID_user", "Done_" + ID_user);
                                parameters.put("Emergency_Pending/" + ID_emergency + "/Status", "Done");
                                parameters.put("Emergency_Pending/" + ID_emergency + "/Status_Type_Rescue", "Done_" + Type_Rescue);
                                parameters.put("Emergency_Pending/" + ID_emergency + "/Status_ID_user", "Done_" + ID_user);
                                ref.updateChildren(parameters)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Rescue Operation Succeed!", Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show());
                            } else {
                                Toast.makeText(context, "Please submit report before to proceed!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    }));
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.setCancelable(false);
            myDialog.setContentView(view1);
            myDialog.show();
        });

        getInformationUser(ID_user);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getCountMessage();
    }

    private void getInformationUser(String id_user) {
        FirebaseDatabase.getInstance().getReference("User_Info").child(id_user)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User_Info user_info = snapshot.getValue(User_Info.class);
                            profileURIString = user_info.getProfile_URL();
                            try {
                                Picasso.get().load(Uri.parse(user_info.getProfile_URL())).into(profileImage);
                            } catch (Exception ex) {
                                profileImage.setImageResource(R.drawable.pic);
                            }
                            fullname_text.setText((user_info.getGender().equals("Male") ? "Sir. " : "Ma'am. ") + user_info.getFirst_Name() + " " + user_info.getLast_Name());
                            Victim_Fullname = user_info.getFirst_Name() + " " + user_info.getLast_Name();

                            address_text.setText(user_info.getAddress());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getCountMessage() {
        FirebaseDatabase.getInstance().getReference("Message_Info").child(firebaseUser.getUid())
                .child(ID_user)
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
        FirebaseDatabase.getInstance().getReference("User_Info").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
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
                        MarkerOptions p1 = new MarkerOptions().position(s).title("Customer here");
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