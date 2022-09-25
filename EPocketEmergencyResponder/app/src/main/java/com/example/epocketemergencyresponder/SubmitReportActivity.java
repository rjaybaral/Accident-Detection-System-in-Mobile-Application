package com.example.epocketemergencyresponder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.epocketemergencyresponder.Model.User_Info;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class SubmitReportActivity extends AppCompatActivity {

    private Context context = this;
    private ImageView back_button;
    private Button submitBtn;
    private EditText victim_fullname_text, from_fullname_text, subject_text, message_report_text;
    private Intent intent;
    private String ID_user = "", ID_other = "", ID_otw = "", ID_emergency = "",
            Latitude = "", Longitude = "", Type_Rescue = "", Victim_Fullname = "", Responder_Name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_report);
        intent = getIntent();
        ID_user = intent.getStringExtra("ID_user");
        ID_other = intent.getStringExtra("ID_other");
        ID_otw = intent.getStringExtra("ID_otw");
        ID_emergency = intent.getStringExtra("ID_emergency");
        Latitude = intent.getStringExtra("Latitude");
        Longitude = intent.getStringExtra("Longitude");
        Type_Rescue = intent.getStringExtra("Type_Rescue");
        Victim_Fullname = intent.getStringExtra("Victim_Fullname");
        victim_fullname_text = findViewById(R.id.victim_fullname_text);
        from_fullname_text = findViewById(R.id.from_fullname_text);
        subject_text = findViewById(R.id.subject_text);
        message_report_text = findViewById(R.id.message_report_text);
        back_button = findViewById(R.id.back_button);
        submitBtn = findViewById(R.id.submitBtn);
        back_button.setOnClickListener(view -> finish());



        victim_fullname_text.setText(Victim_Fullname);
        victim_fullname_text.setEnabled(false);
        getResponderFullname(ID_other);

        submitBtn.setOnClickListener(view -> {
            String message_report = message_report_text.getText().toString();
            String subject = subject_text.getText().toString().trim();
            if (message_report.isEmpty() || subject.isEmpty()) {
                Toast.makeText(context, "All fields are required!", Toast.LENGTH_LONG).show();
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Submit_Report_Info");
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("");
                String ID_report = reference.push().getKey();
                HashMap<String, Object> parameters = new HashMap<>();
                parameters.put("Submit_Report_Info/"+ID_report+"/ID_report", ID_report);
                parameters.put("Submit_Report_Info/"+ID_report+"/ID_user", ID_user);
                parameters.put("Submit_Report_Info/"+ID_report+"/ID_other", ID_other);
                parameters.put("Submit_Report_Info/"+ID_report+"/ID_otw", ID_otw);
                parameters.put("Submit_Report_Info/"+ID_report+"/ID_emergency", ID_emergency);
                parameters.put("Submit_Report_Info/"+ID_report+"/Latitude", Latitude);
                parameters.put("Submit_Report_Info/"+ID_report+"/Longitude", Longitude);
                parameters.put("Submit_Report_Info/"+ID_report+"/Type_Rescue", Type_Rescue);
                parameters.put("Submit_Report_Info/"+ID_report+"/Victim_Fullname", Victim_Fullname);
                parameters.put("Submit_Report_Info/"+ID_report+"/Subject", subject);
                parameters.put("Submit_Report_Info/"+ID_report+"/Message_Report", message_report);
                parameters.put("Submit_Report_Info/"+ID_report+"/Date_Report", dateFormat.format(new Date()));
                parameters.put("Submit_Report_Info/"+ID_report+"/Time_Report", timeFormat.format(new Date()));
                parameters.put("Submit_Report_Info/"+ID_report+"/Responder_Name", Responder_Name);
                ref.updateChildren(parameters)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                Toast.makeText(context, "Successfully Submit Report!", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show());

            }
        });
    }

    private void getResponderFullname(String id_other) {
        FirebaseDatabase.getInstance().getReference("User_Info").child(id_other)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User_Info user_info = snapshot.getValue(User_Info.class);
                            from_fullname_text.setText(user_info.getFirst_Name() + " " + user_info.getLast_Name());
                            from_fullname_text.setEnabled(false);
                            Responder_Name =user_info.getFirst_Name() + " " + user_info.getLast_Name();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}