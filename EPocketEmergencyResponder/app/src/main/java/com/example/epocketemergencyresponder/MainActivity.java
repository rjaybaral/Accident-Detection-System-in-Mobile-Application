package com.example.epocketemergencyresponder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.epocketemergencyresponder.Authentication.LoginActivity;
import com.example.epocketemergencyresponder.Model.OTW_Info;
import com.example.epocketemergencyresponder.Model.User_Info;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button profileBtn, userRequestBtn,logoutBtn;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private Context context = this;
    private String Type = "";
    private FloatingActionButton actionRescuerButton;
    private String ID_user = "", ID_otw = "", Type_Rescue = "", Latitude = "", Longitude = "", ID_emergency = "";
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getVisibleActionRescuer();
        actionRescuerButton = findViewById(R.id.actionRescuerButton);
        profileBtn = findViewById(R.id.profileBtn);
        userRequestBtn = findViewById(R.id.userRequestBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        logoutBtn.setOnClickListener(view -> {
            stopLocationService();
            finish();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(context, LoginActivity.class));
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
            startLocationService();
        }

        actionRescuerButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, RescuerActionWithMapActivity.class);
            intent.putExtra("ID_otw", ID_otw);
            intent.putExtra("ID_user", ID_user);
            intent.putExtra("Type_Rescue", Type_Rescue);
            intent.putExtra("Latitude", Latitude);
            intent.putExtra("Longitude", Longitude);
            intent.putExtra("ID_emergency", ID_emergency);
            startActivity(intent);
        });

        userRequestBtn.setOnClickListener(view -> {
            FirebaseDatabase.getInstance().getReference("OTW_Info").orderByChild("ID_response").equalTo(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Toast.makeText(context, "Sorry, you are already assisting someone", Toast.LENGTH_LONG).show();
                            }else{
                                Intent intent = new Intent(context, UserRequestAssistanceActivity.class);
                                intent.putExtra("Type", Type);
                                intent.putExtra("Latitude", CurrentLatitude);
                                intent.putExtra("Longitude", CurrentLongitude);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        });
        profileBtn.setOnClickListener(view -> startActivity(new Intent(context, ProfileWithEditActivity.class)));

        getInformationResponder();
    }

    private void getVisibleActionRescuer() {
        FirebaseDatabase.getInstance().getReference("OTW_Info").orderByChild("ID_response").equalTo(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            actionRescuerButton.setVisibility(View.VISIBLE);
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                OTW_Info otw_info = snapshot1.getValue(OTW_Info.class);
                                ID_otw = otw_info.getID_otw();
                                ID_user = otw_info.getID_user();
                                Type_Rescue = otw_info.getType_Rescue();
                                Latitude = otw_info.getLatitude();
                                Longitude = otw_info.getLongitude();
                                ID_emergency = otw_info.getID_emergency();
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
    public String CurrentLatitude ="", CurrentLongitude = "";
    private void getInformationResponder() {
        FirebaseDatabase.getInstance().getReference("User_Info").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User_Info user_info = snapshot.getValue(User_Info.class);
                            Type = user_info.getType();
                            CurrentLongitude = user_info.getLongitude();
                            CurrentLatitude = user_info.getLatitude();
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
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_LONG).show();
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

}