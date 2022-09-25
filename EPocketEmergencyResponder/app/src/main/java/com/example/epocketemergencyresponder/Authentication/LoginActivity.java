package com.example.epocketemergencyresponder.Authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.epocketemergencyresponder.Loading.LoadingDialog;
import com.example.epocketemergencyresponder.MainActivity;
import com.example.epocketemergencyresponder.Model.User_Info;
import com.example.epocketemergencyresponder.R;
import com.example.epocketemergencyresponder.WaitingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private ImageView showPassword, hidePassword;
    private Context context = this;
    private EditText email_text, password_text;
    private Button loginBtn;
    private FirebaseAuth firebaseAuth;
    private TextView register_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register_btn = findViewById(R.id.register_btn);
        firebaseAuth = FirebaseAuth.getInstance();
        loginBtn = findViewById(R.id.loginBtn);
        showPassword = findViewById(R.id.showPassword);
        hidePassword = findViewById(R.id.hidePassword);
        email_text = findViewById(R.id.email_text);
        password_text = findViewById(R.id.password_text);

        showPassword.setOnClickListener(view -> {
            password_text.setTransformationMethod(null);
            password_text.setSelection(password_text.getText().toString().length());
            hidePassword.setVisibility(View.VISIBLE);
            showPassword.setVisibility(View.GONE);
        });
        hidePassword.setOnClickListener(view -> {
            password_text.setTransformationMethod(new PasswordTransformationMethod());
            password_text.setSelection(password_text.getText().toString().length());
            showPassword.setVisibility(View.VISIBLE);
            hidePassword.setVisibility(View.GONE);
        });

        loginBtn.setOnClickListener(view -> {
            String email = email_text.getText().toString();
            String password = password_text.getText().toString();
            if (email.equals("") || password.equals("")) {
                Toast.makeText(context, "All fields are required!", Toast.LENGTH_LONG).show();
            } else {
                LoadingDialog progressDialog = new LoadingDialog(context, "Authenticating...");
                progressDialog.showDialog();
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                progressDialog.dismissDialog();
                                FirebaseDatabase.getInstance().getReference("User_Info")
                                        .child(firebaseAuth.getCurrentUser().getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    User_Info user_info = snapshot.getValue(User_Info.class);
                                                    if (!(user_info.getType().equals("User"))){
                                                        Intent intent = new Intent(context, WaitingActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }else{
                                                        FirebaseAuth.getInstance().signOut();
                                                        progressDialog.dismissDialog();
                                                        Toast.makeText(context, "Sorry, Wrong email or password!", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                FirebaseAuth.getInstance().signOut();
                                                progressDialog.dismissDialog();
                                                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }).addOnFailureListener(e -> {
                            progressDialog.dismissDialog();
                            if (e.getMessage().equals("The password is invalid or the user does not have a password.")) {
                                Toast.makeText(context, "Sorry, Wrong email or password!", Toast.LENGTH_LONG).show();
                            }else if (e.getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                Toast.makeText(context, "Sorry, Wrong email or password!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        register_btn.setOnClickListener(view -> startActivity(new Intent(context, RegisterActivity.class)));
    }
}