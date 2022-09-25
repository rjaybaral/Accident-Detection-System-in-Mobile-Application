package com.example.epocketemergencyresponder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ProfilePictureShowActivity extends AppCompatActivity {
    private Context context = this;
    private ImageView back_button, profileImage;
    private Intent intent;
    private String Profile_URL = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture_show);
        intent = getIntent();
        Profile_URL = intent.getStringExtra("Profile_URL");
        back_button = findViewById(R.id.back_button);
        profileImage = findViewById(R.id.profileImage);
        back_button.setOnClickListener(view -> onBackPressed());
        try {
            Picasso.get().load(Uri.parse(Profile_URL)).into(profileImage);
        } catch (Exception ex) {
            profileImage.setImageResource(R.drawable.user_default_icon);
        }
    }
}