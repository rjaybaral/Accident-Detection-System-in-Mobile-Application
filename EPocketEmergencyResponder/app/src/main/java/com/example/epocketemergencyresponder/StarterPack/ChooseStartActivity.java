package com.example.epocketemergencyresponder.StarterPack;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.epocketemergencyresponder.Authentication.LoginActivity;
import com.example.epocketemergencyresponder.Authentication.RegisterActivity;
import com.example.epocketemergencyresponder.MainActivity;
import com.example.epocketemergencyresponder.R;
import com.example.epocketemergencyresponder.WaitingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChooseStartActivity extends AppCompatActivity {

    private Button registerBtn, loginBtn;
    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_start);
        registerBtn = findViewById(R.id.registerBtn);
        loginBtn = findViewById(R.id.loginBtn);

        registerBtn.setOnClickListener(view -> {
            startActivity(new Intent(context, RegisterActivity.class));
            finish();
        });
        loginBtn.setOnClickListener(view -> {
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            startActivity(new Intent(context, WaitingActivity.class));
            finish();
        }
    }
}