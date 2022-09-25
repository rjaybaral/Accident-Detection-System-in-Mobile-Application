package com.example.epocketemergency;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epocketemergency.Authentication.LoginActivity;
import com.example.epocketemergency.Model.Emergency_Contact_Info;
import com.example.epocketemergency.Model.Emergency_Pending_Info;
import com.example.epocketemergency.Model.OTW_Info;
import com.example.epocketemergency.Model.User_Info;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private Button start, stop;
    private ImageView profileImage, showCurrentLocationMap;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private TextView currentLocation, fullname_text;
    private Context context = this;
    private String Profile_URL = "";
    private Toolbar toolbar;
    private NavigationView navBar;
    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout swipe;
    private Emergency_Pending_Info emergency_pending_info = new Emergency_Pending_Info();
    private boolean meet = false;
    private FloatingActionButton actionRescuerButton;
    private String ID_response = "", ID_otw = "", Type_Rescue = "", Latitude = "", Longitude = "";
    private MediaPlayer myAlert;


    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private boolean isAccelerometerSensorAvailable, isNotFirstTime = false;
    private float currentX, currentY, currentZ, lastX, lastY, lastZ;
    private float diffX, diffY, diffZ;
    private float shakeThreshold = 5f;
    boolean fMeet = false, lMeet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myAlert = MediaPlayer.create(context, R.raw.alarm_sound);
        getPendingStatus();
        getVisibleActionRescuer();
        actionRescuerButton = findViewById(R.id.actionRescuerButton);
        swipe = findViewById(R.id.swipe);
        fullname_text = findViewById(R.id.fullname_text);
        showCurrentLocationMap = findViewById(R.id.showCurrentLocationMap);
        currentLocation = findViewById(R.id.currentLocation);
        profileImage = findViewById(R.id.profileImage);
        drawerLayout = findViewById(R.id.drawerLayout);
        navBar = findViewById(R.id.navBar);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setTitle("");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navBar.setNavigationItemSelectedListener(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerSensorAvailable = true;
        } else {
            Toast.makeText(context, "Acceleromter sensor is not available", Toast.LENGTH_LONG).show();
            isAccelerometerSensorAvailable = false;
        }

        //Get Permission in SEND SMS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

            } else {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        }

        actionRescuerButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, RescuerActionWithMapActivity.class);
            intent.putExtra("ID_otw", ID_otw);
            intent.putExtra("ID_response", ID_response);
            intent.putExtra("Type_Rescue", Type_Rescue);
            intent.putExtra("Latitude", Latitude);
            intent.putExtra("Longitude", Longitude);
            startActivity(intent);
        });


//        police_btn.setOnClickListener(view -> {
//            disabaleButton();
//            FirebaseDatabase.getInstance().getReference("Emergency_Pending").orderByChild("Status_ID_user").equalTo("OTW_" + firebaseUser.getUid())
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (!snapshot.exists()) {
//                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Emergency_Pending");
//                                String ID_emergency = reference.push().getKey();
//                                HashMap<String, String> parameters = new HashMap<>();
//                                parameters.put("ID_emergency", ID_emergency);
//                                parameters.put("ID_user", firebaseUser.getUid());
//                                parameters.put("First_Name", emergency_pending_info.getFirst_Name());
//                                parameters.put("Last_Name", emergency_pending_info.getLast_Name());
//                                parameters.put("Birthday", emergency_pending_info.getBirthday());
//                                parameters.put("Contact_No", emergency_pending_info.getContact_No());
//                                parameters.put("Address", emergency_pending_info.getAddress());
//                                parameters.put("Current_Location_Address", emergency_pending_info.getCurrent_Location_Address());
//                                parameters.put("Latitude", emergency_pending_info.getLatitude());
//                                parameters.put("Longitude", emergency_pending_info.getLongitude());
//                                parameters.put("Type_Rescue", "Police");
//                                parameters.put("Status", "Pending");
//                                parameters.put("Status_Type_Rescue", "Pending_Police");
//                                parameters.put("Status_ID_user", "Pending_" + firebaseUser.getUid());
//                                reference.child(ID_emergency).setValue(parameters)
//                                        .addOnCompleteListener(task -> {
//                                            if (task.isSuccessful()) {
//                                                //Please wait for responder
//                                            }
//                                        }).addOnFailureListener(e -> {
//                                            enableButton();
//                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
//                                        });
//                            } else {
//                                Toast.makeText(context, "Sorry, you are already request rescuer", Toast.LENGTH_LONG).show();
//                            }
//                            enableButton();
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            enableButton();
//                        }
//                    });
//
//        });

        //Refreshing Data
        swipe.setOnRefreshListener(() -> {
            getProfileImage();
            swipe.setRefreshing(false);
            openAlert = true;
            fMeet = false;
            lMeet = false;
        });

        //Auto Update Location Service
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION
            );
        } else {
            permissionSENDSMS();
            startLocationService();
        }

        //Show Specific Profile Image
        profileImage.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfilePictureShowActivity.class);
            intent.putExtra("Profile_URL", Profile_URL);
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(profileImage, "image1");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
            startActivity(intent, options.toBundle());
        });

        getProfileImage();
        getCurrentLocation();


        showCurrentLocationMap.setOnClickListener(view -> startActivity(new Intent(context, ShowMapLocationUserActivity.class)));


//        start = findViewById(R.id.startButton);
//        stop = findViewById(R.id.stopButton);
//
//        start.setOnClickListener(view -> {
//            if (ContextCompat.checkSelfPermission(getApplicationContext(),
//                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(
//                        MainActivity.this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        REQUEST_CODE_LOCATION_PERMISSION
//                );
//            }else{
//                startLocationService();
//            }
//        });
//        stop.setOnClickListener(view -> stopLocationService());
    }

    private void permissionSENDSMS() {
        //Get Permission in SEND SMS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

            } else {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        }

    }

    private void getVisibleActionRescuer() {
        FirebaseDatabase.getInstance().getReference("OTW_Info").orderByChild("ID_user").equalTo(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            actionRescuerButton.setVisibility(View.VISIBLE);
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                OTW_Info otw_info = snapshot1.getValue(OTW_Info.class);
                                ID_otw = otw_info.getID_otw();
                                ID_response = otw_info.getID_response();
                                Type_Rescue = otw_info.getType_Rescue();
                                Latitude = otw_info.getLatitude();
                                Longitude = otw_info.getLongitude();
                            }
                        } else {
                            actionRescuerButton.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void getPendingStatus() {
        try {
            Dialog myDialog1 = new Dialog(context);
            View view1 = LayoutInflater.from(context).inflate(R.layout.dialog_pending, null);
            myDialog1.setContentView(view1);
            valueEventListener = ref
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    Emergency_Pending_Info einfo = snapshot1.getValue(Emergency_Pending_Info.class);
                                    if (einfo.getStatus().equals("Pending") && firebaseUser.getUid().equals(einfo.getID_user())) {
                                        if (!meet) {
                                            myAlert.start();
                                            TextView falseButton = view1.findViewById(R.id.falseButton);
                                            falseButton.setOnClickListener(view2 -> {
                                                myDialog1.dismiss();
                                                snapshot1.getRef().removeValue();
                                                myAlert.pause();
                                                openAlert = true;
                                                fMeet = false;
                                                lMeet = false;

                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                                                        try {
                                                            FirebaseDatabase.getInstance().getReference("Emergency_Contact").child(firebaseUser.getUid())
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            if (snapshot.exists()) {
                                                                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                                                    Emergency_Contact_Info emergency_contact_info = snapshot1.getValue(Emergency_Contact_Info.class);
                                                                                    SmsManager smsManager = SmsManager.getDefault();
                                                                                    String message = "Hi " + emergency_contact_info.getName() + ", It's a FALSE ALARM.\n\nI apologize!" + "\n\n\n" +
                                                                                            "https://www.google.com/maps?q=" + emergency_pending_info.getLatitude() + "," + emergency_pending_info.getLongitude();
                                                                                    smsManager.sendTextMessage(emergency_contact_info.getContact_Number(), null, message, null, null);
//                                                                    "https://www.google.com/maps/place/14%C2%B029'40.5%22N+121%C2%B002'17.8%22E/@"+emergency_pending_info.getLatitude()+","+emergency_pending_info.getLongitude();
                                                                                }
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                                        }
                                                                    });
                                                        } catch (Exception e) {
                                                            Toast.makeText(context, "Not Send", Toast.LENGTH_LONG).show();
                                                        }
                                                    } else {
                                                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 123);
                                                    }
                                                }

                                            });
                                            myDialog1.setCancelable(false);
                                            myDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            myDialog1.show();
                                            meet = true;
                                        }
                                    } else {
                                        if (firebaseUser.getUid().equals(einfo.getID_user())) {
                                            meet = false;
                                            myDialog1.dismiss();
                                            openAlert = true;
                                            fMeet = false;
                                            lMeet = false;
                                        }
                                    }
                                }
                            } else {
                                meet = false;
                                myDialog1.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Cancelled!", Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    Query ref = FirebaseDatabase.getInstance().getReference("Emergency_Pending").orderByChild("Status_ID_user").equalTo("Pending_" + firebaseUser.getUid());
    ValueEventListener valueEventListener;

    @Override
    protected void onStart() {
        super.onStart();
        ref.addValueEventListener(valueEventListener);
    }

    private void getCurrentLocation() {
        FirebaseDatabase.getInstance().getReference("User_Info").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User_Info user_info = snapshot.getValue(User_Info.class);
                            currentLocation.setText(user_info.getCurrent_Location_Address());
                            emergency_pending_info.setLatitude(user_info.getLatitude());
                            emergency_pending_info.setLongitude(user_info.getLongitude());
                            emergency_pending_info.setCurrent_Location_Address(user_info.getCurrent_Location_Address());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void getProfileImage() {
        FirebaseDatabase.getInstance().getReference("User_Info").child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User_Info user_info = snapshot.getValue(User_Info.class);
                            emergency_pending_info.setFirst_Name(user_info.getFirst_Name());
                            emergency_pending_info.setLast_Name(user_info.getLast_Name());
                            fullname_text.setText(user_info.getFirst_Name() + " " + user_info.getLast_Name() + "!");
                            emergency_pending_info.setAddress(user_info.getAddress());
                            emergency_pending_info.setBirthday(user_info.getBirthday());
                            emergency_pending_info.setContact_No(user_info.getContact());
                            emergency_pending_info.setID_user(firebaseUser.getUid());
                            try {
                                Profile_URL = user_info.getProfile_URL();
                                Picasso.get().load(Uri.parse(user_info.getProfile_URL())).into(profileImage);
                            } catch (Exception ex) {
                                profileImage.setImageResource(R.drawable.user_default_icon);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
                permissionSENDSMS();
            } else {
                Toast.makeText(this, "Location Permission Denied!", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == 123 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    FirebaseDatabase.getInstance().getReference("Emergency_Contact").child(firebaseUser.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                            Emergency_Contact_Info emergency_contact_info = snapshot1.getValue(Emergency_Contact_Info.class);
                                            SmsManager smsManager = SmsManager.getDefault();
                                            String message = "Hi " + emergency_contact_info.getName() + ", I need HELP! \n\n" + emergency_pending_info.getLatitude() + "," + emergency_pending_info.getLongitude() + "\n\n\n\n" +
                                                    "https://www.google.com/maps?q=" + emergency_pending_info.getLatitude() + "," + emergency_pending_info.getLongitude();
                                            smsManager.sendTextMessage(emergency_contact_info.getContact_Number(), null, message, null, null);
//                                                                    "https://www.google.com/maps/place/14%C2%B029'40.5%22N+121%C2%B002'17.8%22E/@"+emergency_pending_info.getLatitude()+","+emergency_pending_info.getLongitude();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                } catch (Exception e) {
                    Toast.makeText(context, "Not Send", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "SMS Permission Denied!", Toast.LENGTH_LONG).show();
            }
        }


    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(serviceInfo.service.getClassName())) {
                    if (serviceInfo.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
        }
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.account:
                Intent intent = new Intent(context, ProfileWithEditActivity.class);
                intent.putExtra("Profile_URL", Profile_URL);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(profileImage, "image1");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
                startActivity(intent, options.toBundle());
                break;
            case R.id.logout:
                ref.removeEventListener(valueEventListener);
                FirebaseAuth.getInstance().signOut();
                stopLocationService();
                finishAndRemoveTask();
                startActivity(new Intent(context, LoginActivity.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ref.removeEventListener(valueEventListener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        ref.removeEventListener(valueEventListener);
//        myDialog1.dismiss();
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        currentX = sensorEvent.values[0];
        currentY = sensorEvent.values[1];
        currentZ = sensorEvent.values[2];

        if (((currentX >= 5f && currentY <= 5f && currentZ <= 3) || currentZ <= -5f || currentY <= -5f || currentX >= 4f ||
                currentX <= -4f ||
                (currentX <= 5f && currentY <= 1f && currentZ >= 7f) ||
                (currentX >= 3f && currentY <= 6f && currentZ <= 6f) ||
                (currentX >= 3f && currentY <= 8f && currentZ <= 6f)) && openAlert) {
            if (!fMeet) {
                Thread timer = new Thread() {
                    public void run() {
                        try {
                            sleep(3000);
                            fMeet = true;
                            if (fMeet) {
                                if (!lMeet) {
                                    Thread timer1 = new Thread() {
                                        public void run() {
                                            try {
                                                sleep(3000);
                                                lMeet = true;
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    };
                                    timer1.start();
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                };
                timer.start();
            }

            if (fMeet && lMeet) {
                if (openAlert) {
                    FirebaseDatabase.getInstance().getReference("Emergency_Pending").orderByChild("Status_ID_user").equalTo("OTW_" + firebaseUser.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Emergency_Pending");
                                        String ID_emergency = reference.push().getKey();
                                        HashMap<String, String> parameters = new HashMap<>();
                                        parameters.put("ID_emergency", ID_emergency);
                                        parameters.put("ID_user", firebaseUser.getUid());
                                        parameters.put("First_Name", emergency_pending_info.getFirst_Name());
                                        parameters.put("Last_Name", emergency_pending_info.getLast_Name());
                                        parameters.put("Birthday", emergency_pending_info.getBirthday());
                                        parameters.put("Contact_No", emergency_pending_info.getContact_No());
                                        parameters.put("Address", emergency_pending_info.getAddress());
                                        parameters.put("Current_Location_Address", emergency_pending_info.getCurrent_Location_Address());
                                        parameters.put("Latitude", emergency_pending_info.getLatitude());
                                        parameters.put("Longitude", emergency_pending_info.getLongitude());
                                        parameters.put("Type_Rescue", "Ambulance");
                                        parameters.put("Status", "Pending");
                                        parameters.put("Status_Type_Rescue", "Pending_Ambulance");
                                        parameters.put("Status_ID_user", "Pending_" + firebaseUser.getUid());
                                        reference.child(ID_emergency).setValue(parameters)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        //Please wait for responder
                                                    }
                                                }).addOnFailureListener(e -> {
                                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                                });
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                                                try {
                                                    FirebaseDatabase.getInstance().getReference("Emergency_Contact").child(firebaseUser.getUid())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists()) {
                                                                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                                            Emergency_Contact_Info emergency_contact_info = snapshot1.getValue(Emergency_Contact_Info.class);
                                                                            SmsManager smsManager = SmsManager.getDefault();
                                                                            String message = "Hi " + emergency_contact_info.getName() + ", I need HELP! \n\n" + emergency_pending_info.getLatitude() + "," + emergency_pending_info.getLongitude() + "\n\n\n\n" +
                                                                                    "https://www.google.com/maps?q=" + emergency_pending_info.getLatitude() + "," + emergency_pending_info.getLongitude();
                                                                            smsManager.sendTextMessage(emergency_contact_info.getContact_Number(), null, message, null, null);
//                                                                    "https://www.google.com/maps/place/14%C2%B029'40.5%22N+121%C2%B002'17.8%22E/@"+emergency_pending_info.getLatitude()+","+emergency_pending_info.getLongitude();
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                }
                                                            });
                                                } catch (Exception e) {
                                                    Toast.makeText(context, "Not Send", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 123);
                                            }
                                        }

                                    } else {
//                                Toast.makeText(context, "Sorry, you are already request rescuer", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                    openAlert = false;
                    //Sample Send Message Contact Emergency

                }
            }
        } else {
            fMeet = false;
            lMeet = false;
        }
    }

    private boolean openAlert = true;

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAccelerometerSensorAvailable) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isAccelerometerSensorAvailable) {
            sensorManager.unregisterListener(this);
        }
    }
}