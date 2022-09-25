package com.example.epocketemergencyresponder;

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
import android.widget.EditText;
import android.widget.ImageView;

import com.example.epocketemergencyresponder.Model.User_Info;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileSpecificUserActivity extends AppCompatActivity {
    private ImageView profileImage,back_button;
    private EditText firstname_text, lastname_text, birthday_text, contact_text, email_text, address_text;
    private Context context = this;
    private Intent intent;
    private String ID_user = "", Profile_URL = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_specific_user);
        intent = getIntent();
        ID_user = intent.getStringExtra("ID_user");

        back_button = findViewById(R.id.back_button);
        firstname_text = findViewById(R.id.firstname_text);
        lastname_text = findViewById(R.id.lastname_text);
        birthday_text = findViewById(R.id.birthday_text);
        contact_text = findViewById(R.id.contact_text);
        email_text = findViewById(R.id.email_text);
        address_text = findViewById(R.id.address_text);
        profileImage = findViewById(R.id.profileImage);

        back_button.setOnClickListener(view -> finish());

        profileImage.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfilePictureShowActivity.class);
            intent.putExtra("Profile_URL", Profile_URL);
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(profileImage, "image1");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
            startActivity(intent, options.toBundle());
        });

        getInformationUser(ID_user);
    }

    private void getInformationUser(String id) {
        FirebaseDatabase.getInstance().getReference("User_Info").child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User_Info user_info = snapshot.getValue(User_Info.class);
                            firstname_text.setText(user_info.getFirst_Name());
                            lastname_text.setText(user_info.getLast_Name());
                            birthday_text.setText(user_info.getBirthday());
                            contact_text.setText(user_info.getContact());
                            email_text.setText(user_info.getEmail());
                            address_text.setText(user_info.getAddress());
                            Profile_URL = user_info.getProfile_URL();
                            try {
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
}