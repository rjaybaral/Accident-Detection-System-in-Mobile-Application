package com.example.epocketemergency;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ChooseRescuerActivity extends AppCompatActivity {
    private Context context = this;
    private ImageView back_button;
    private LinearLayout ambulance_btn, fighter_btn, police_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_rescuer);
        ambulance_btn = findViewById(R.id.ambulance_btn);
        fighter_btn = findViewById(R.id.firefighter_btn);
        police_btn = findViewById(R.id.police_btn);
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> finish());

        ambulance_btn.setOnClickListener(view -> startActivity(new Intent(context, AmbulanceOTWActivity.class)));
        fighter_btn.setOnClickListener(view -> startActivity(new Intent(context, FirefighterOTWActivity.class)));
        police_btn.setOnClickListener(view -> startActivity(new Intent(context, PoliceOTWActivity.class)));
    }
}