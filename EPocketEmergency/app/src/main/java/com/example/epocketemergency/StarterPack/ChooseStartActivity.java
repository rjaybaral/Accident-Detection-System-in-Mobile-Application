package com.example.epocketemergency.StarterPack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.epocketemergency.Authentication.LoginActivity;
import com.example.epocketemergency.Authentication.RegisterActivity;
import com.example.epocketemergency.MainActivity;
import com.example.epocketemergency.R;
import com.example.epocketemergency.WaitingActivity;
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