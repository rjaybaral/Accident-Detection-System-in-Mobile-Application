package com.example.epocketemergency.Authentication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.epocketemergency.Loading.LoadingDialog;
import com.example.epocketemergency.MainActivity;
import com.example.epocketemergency.R;
import com.example.epocketemergency.SubmitIDActivity;
import com.example.epocketemergency.WaitingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private Context context = this;
    private ImageView r1;
    private String datePick = "";
    private int year, month, day;
    private Spinner spinnerGender;
    private ImageView showPassword, hidePassword, pictureBtn;
    private ActivityResultLauncher<String> mGetContent;
    private EditText password_text, firstname_text, lastname_text, address_text, contact_text, email_text;
    private TextView birthday_text;
    private String gender_text = "", IDType = "";
    private Button validIdButton, registerBtn;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        firebaseAuth = FirebaseAuth.getInstance();
        registerBtn = findViewById(R.id.registerBtn);
        validIdButton = findViewById(R.id.validIdButton);
        birthday_text = findViewById(R.id.birthday_text);
        firstname_text = findViewById(R.id.firstname_text);
        lastname_text = findViewById(R.id.lastname_text);
        address_text = findViewById(R.id.address_text);
        contact_text = findViewById(R.id.contact_text);
        email_text = findViewById(R.id.email_text);
        showPassword = findViewById(R.id.showPassword);
        hidePassword = findViewById(R.id.hidePassword);
        password_text = findViewById(R.id.password_text);
        r1 = findViewById(R.id.r1);
        pictureBtn = findViewById(R.id.pictureBtn);

        registerBtn.setOnClickListener(view -> {
            String fname = firstname_text.getText().toString().trim();
            String lname = lastname_text.getText().toString().trim();
            String address = address_text.getText().toString().trim();
            String birthday = birthday_text.getText().toString().trim();
            String contact = contact_text.getText().toString().trim();
            String gender = gender_text;
            String email = email_text.getText().toString().trim();
            String password = password_text.getText().toString().trim();

            if (IDType.trim().isEmpty() || fname.isEmpty() || lname.isEmpty() || address.isEmpty() ||
                    birthday.isEmpty() || contact.isEmpty() || gender.isEmpty() ||
                    email.isEmpty() || password.isEmpty() || resultURIProfile == null || resultURIID == null) {
                Toast.makeText(context, "All fields are required!", Toast.LENGTH_LONG).show();
            } else {
                boolean emailCheck = false, passwordLength = false, contactValid = false;
                if (isEmailValid(email)) {
                    emailCheck = true;
                } else {
                    emailCheck = false;
                    Toast.makeText(context, "Invalid Email Address!", Toast.LENGTH_LONG).show();
                }

                if (password.length() >= 6) {
                    passwordLength = true;
                } else {
                    passwordLength = false;
                    Toast.makeText(context, "Password must atleast 6 characters!", Toast.LENGTH_LONG).show();
                }

                if (contact.length() == 11){
                    contactValid = true;
                }else {
                    contactValid = false;
                    Toast.makeText(context, "11 Digits for Contact Number", Toast.LENGTH_LONG).show();
                }

                //Processing Uploading - Creating Account
                if (emailCheck && passwordLength && contactValid) {
                    LoadingDialog progressDialog = new LoadingDialog(context, "Registering...");
                    progressDialog.showDialog();
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    StorageReference storageReference1 = storageReference.child(String.valueOf(System.currentTimeMillis()));
                                    storageReference1.putFile(resultURIProfile)
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    storageReference1.getDownloadUrl()
                                                            .addOnCompleteListener(task2 -> {
                                                                StorageReference storageReference2 = storageReference.child(String.valueOf(System.currentTimeMillis()));
                                                                storageReference2.putFile(resultURIID)
                                                                        .addOnCompleteListener(task3 -> {
                                                                            if (task3.isSuccessful()) {
                                                                                storageReference2.getDownloadUrl()
                                                                                        .addOnCompleteListener(task4 -> {
                                                                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_Info");
                                                                                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                                                                            String ID_user = firebaseUser.getUid();
                                                                                            HashMap<String, String> parameters = new HashMap<>();
                                                                                            parameters.put("ID_user", ID_user);
                                                                                            parameters.put("First_Name", fname);
                                                                                            parameters.put("Last_Name", lname);
                                                                                            parameters.put("Birthday", birthday);
                                                                                            parameters.put("Address", address);
                                                                                            parameters.put("Contact", contact);
                                                                                            parameters.put("Gender", gender);
                                                                                            parameters.put("Email", email);
                                                                                            parameters.put("Latitude", "0");
                                                                                            parameters.put("Longitude", "0");
                                                                                            parameters.put("Valid_ID_URL", String.valueOf(task4.getResult()));
                                                                                            parameters.put("Profile_URL", String.valueOf(task2.getResult()));
                                                                                            parameters.put("Current_Location_Address", "");
                                                                                            parameters.put("DirectionFront", "");
                                                                                            parameters.put("IDType", IDType);
                                                                                            parameters.put("Type", "User");
                                                                                            parameters.put("Status", "Pending");
                                                                                            databaseReference.child(ID_user).setValue(parameters)
                                                                                                    .addOnCompleteListener(task5 -> {
                                                                                                        if (task5.isSuccessful()) {
                                                                                                            progressDialog.dismissDialog();
                                                                                                            Intent intent = new Intent(context, WaitingActivity.class);
                                                                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                                            startActivity(intent);
                                                                                                            Toast.makeText(context, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                                                                                                        } else {
                                                                                                            progressDialog.dismissDialog();
                                                                                                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                                                                                                        }
                                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                            progressDialog.dismissDialog();
                                                                                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                                                                                        }
                                                                                                    });
                                                                                        });
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
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismissDialog();
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                }
            }

        });

        //Picture Function Profile
        pictureBtn.setOnClickListener(view -> mGetContent.launch("image/*"));

        //Valid ID
        validIdButton.setOnClickListener(view -> {
//            mGetContentValidId.launch("image/*");
            Intent intent = new Intent(context, SubmitIDActivity.class);
            startActivityForResult(intent, 1);
        });

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


        //Get Gender List
        spinnerGender = findViewById(R.id.spinnerGender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.genderList, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender_text = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Show and Hide Password
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
    }


    private Uri resultURIProfile, resultURIID;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 143) {
            resultURIProfile = UCrop.getOutput(data);
            pictureBtn.setImageURI(resultURIProfile);

        } else if (requestCode == UCrop.RESULT_ERROR) {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
        } else if (requestCode == 1) {
            String res = data.getStringExtra("resultURIID");
            String IDTypeData = data.getStringExtra("IDType");
            if (!(res.isEmpty() || res == null)) {
                IDType = IDTypeData;
                resultURIID = Uri.parse(res);
                validIdButton.setText(res);
            }
        }
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
        } else if (i == 11) {
            return "December";
        } else {
            return "";
        }
    }


    public boolean isEmailValid(String email) {
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        final Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}