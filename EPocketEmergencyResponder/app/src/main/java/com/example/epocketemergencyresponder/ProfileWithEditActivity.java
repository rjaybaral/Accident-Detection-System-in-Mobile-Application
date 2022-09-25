package com.example.epocketemergencyresponder;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.epocketemergencyresponder.Loading.LoadingDialog;
import com.example.epocketemergencyresponder.Model.User_Info;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class ProfileWithEditActivity extends AppCompatActivity {
    private ImageView back_button, profileImage, r1;
    private Context context = this;
    private String datePick = "";
    private EditText firstname_text, lastname_text, birthday_text, contact_text, email_text, address_text;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private LinearLayout choosePhotoBtn;
    private ActivityResultLauncher<String> mGetContent;
    private Uri resultURIProfile;
    private Button updateBtn;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_with_edit);
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        updateBtn = findViewById(R.id.updateBtn);
        choosePhotoBtn = findViewById(R.id.choosePhotoBtn);
        firstname_text = findViewById(R.id.firstname_text);
        lastname_text = findViewById(R.id.lastname_text);
        birthday_text = findViewById(R.id.birthday_text);
        contact_text = findViewById(R.id.contact_text);
        email_text = findViewById(R.id.email_text);
        address_text = findViewById(R.id.address_text);
        r1 = findViewById(R.id.r1);
        back_button = findViewById(R.id.back_button);
        profileImage = findViewById(R.id.profileImage);
        back_button.setOnClickListener(view -> onBackPressed());



        //For MGetContext Uri Profile
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                Uri uri = Uri.parse(result.toString());
                String dest_uri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
                UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), dest_uri)))
                        .withOptions(new UCrop.Options())
                        .withAspectRatio(0, 0)
                        .withMaxResultSize(2000, 2000)
                        .useSourceImageAspectRatio()
                        .start((Activity) context, 143);
            }
        });




        choosePhotoBtn.setOnClickListener(view -> mGetContent.launch("image/*"));


        //Maximum Date for Birthday
        Calendar calendar = Calendar.getInstance();
        r1.setOnClickListener(view -> {
            DatePickerDialog d = new DatePickerDialog(context, (datePicker, year, month, day) -> {
                datePick = nameMonth(month) + " " + day + " " + year;
                birthday_text.setText(datePick);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            calendar.add(Calendar.YEAR, 0);
            d.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            d.show();
        });

        updateBtn.setOnClickListener(view -> {
            String fname = firstname_text.getText().toString().trim();
            String lname = lastname_text.getText().toString().trim();
            String birthday = birthday_text.getText().toString().trim();
            String contact = contact_text.getText().toString().trim();
            String address = address_text.getText().toString().trim();
            if (fname.isEmpty() || lname.isEmpty() || birthday.isEmpty() ||
                    contact.isEmpty() || address.isEmpty()) {
                Toast.makeText(context, "All fields are required!", Toast.LENGTH_LONG).show();
            } else {
                boolean contactValid = false;
                if (contact.length() == 11) {
                    contactValid = true;
                } else {
                    contactValid = false;
                    Toast.makeText(context, "11 Digits for Contact Number", Toast.LENGTH_LONG).show();
                }
                if (contactValid) {
                    if (String.valueOf(resultURIProfile).equals(Profile_URL)) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_Info");
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String ID_user = firebaseUser.getUid();
                        HashMap<String, Object> parameters = new HashMap<>();
                        parameters.put("First_Name", fname);
                        parameters.put("Last_Name", lname);
                        parameters.put("Birthday", birthday);
                        parameters.put("Address", address);
                        parameters.put("Contact", contact);
                        databaseReference.child(ID_user).updateChildren(parameters)
                                .addOnCompleteListener(task5 -> {
                                    if (task5.isSuccessful()) {
                                        Toast.makeText(context, "Update information has been succeed!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    } else {
                        LoadingDialog progressDialog = new LoadingDialog(context, "Updating...");
                        progressDialog.showDialog();
                        StorageReference storageReference1 = storageReference.child(String.valueOf(System.currentTimeMillis()));
                        storageReference1.putFile(resultURIProfile)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        storageReference1.getDownloadUrl()
                                                .addOnCompleteListener(task1 -> {
                                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_Info");
                                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                                    String ID_user = firebaseUser.getUid();
                                                    HashMap<String, Object> parameters = new HashMap<>();
                                                    parameters.put("First_Name", fname);
                                                    parameters.put("Last_Name", lname);
                                                    parameters.put("Birthday", birthday);
                                                    parameters.put("Address", address);
                                                    parameters.put("Contact", contact);
                                                    parameters.put("Profile_URL", String.valueOf(task1.getResult()));
                                                    databaseReference.child(ID_user).updateChildren(parameters)
                                                            .addOnCompleteListener(task5 -> {
                                                                if (task5.isSuccessful()) {
                                                                    progressDialog.dismissDialog();
                                                                    resultURIProfile = null;
                                                                    Toast.makeText(context, "Update information has been succeed!", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    progressDialog.dismissDialog();
                                                                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                                                                }
                                                            }).addOnFailureListener(e -> {
                                                                progressDialog.dismissDialog();
                                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                                            });
                                                });
                                    }
                                }).addOnFailureListener(e -> {
                                    progressDialog.dismissDialog();
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    }
                }
            }
        });

        profileImage.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfilePictureShowActivity.class);
            intent.putExtra("Profile_URL", String.valueOf(resultURIProfile));
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(profileImage, "image1");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
            startActivity(intent, options.toBundle());
        });

        getInformationUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 143) {
            resultURIProfile = UCrop.getOutput(data);
            profileImage.setImageURI(resultURIProfile);

        } else if (requestCode == UCrop.RESULT_ERROR) {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
        }
    }

    private String Profile_URL = "";
    private void getInformationUser() {
        FirebaseDatabase.getInstance().getReference("User_Info").child(firebaseUser.getUid())
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
                            email_text.setEnabled(false);
                            resultURIProfile = Uri.parse(user_info.getProfile_URL());
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

    private String nameMonth(int i) {
        if (i == 0) {
            return "January";
        } else if (i == 1) {
            return "February";
        } else if (i == 2) {
            return "March";
        } else if (i == 3) {
            return "April";
        } else if (i == 4) {
            return "May";
        } else if (i == 5) {
            return "June";
        } else if (i == 6) {
            return "July";
        } else if (i == 7) {
            return "August";
        } else if (i == 8) {
            return "September";
        } else if (i == 9) {
            return "October";
        } else if (i == 10) {
            return "November";
        } else if (i == 10) {
            return "December";
        } else {
            return "";
        }
    }
}