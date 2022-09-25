package com.example.epocketemergencyresponder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epocketemergencyresponder.Adapter.Message_Adapter;
import com.example.epocketemergencyresponder.Loading.LoadingDialog;
import com.example.epocketemergencyresponder.Model.Message_Info;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MessageActivity extends AppCompatActivity {
    private Context context = this;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private RecyclerView recyclerView;
    private Message_Adapter message_adapter;
    private List<Message_Info> message_infoList;
    private ImageView back_btn, send_message_btn, camera_btn_down;
    private EditText input_message;
    private String ID_other = "";
    private Intent intent;
    private StorageReference storageReference;

    private ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        storageReference = FirebaseStorage.getInstance().getReference("Images_Message");
        intent = getIntent();
        ID_other = intent.getStringExtra("ID_other");
        message_infoList = new ArrayList<>();
        camera_btn_down = findViewById(R.id.camera_btn_down);
        send_message_btn = findViewById(R.id.send_message_btn);
        input_message = findViewById(R.id.input_message);
        back_btn = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(layoutManager);
        message_adapter = new Message_Adapter(context, message_infoList);
        recyclerView.setAdapter(message_adapter);

        back_btn.setOnClickListener(view -> finish());
        send_message_btn.setOnClickListener(view -> {
            String message = input_message.getText().toString().trim();
            input_message.setText("");
            if (message.equals("")) {
                Toast.makeText(context, "Message is blank!", Toast.LENGTH_LONG).show();
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Message_Info").child(firebaseUser.getUid());
                String ID_message = reference.push().getKey();
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("ID_message", ID_message);
                parameters.put("Sender", firebaseUser.getUid());
                parameters.put("Receiver", ID_other);
                parameters.put("Message", message);
                parameters.put("Message_Time", timeFormat.format(new Date()));
                parameters.put("Message_Date", dateFormat.format(new Date()));
                parameters.put("Message_Seen", "Seen");
                parameters.put("Picture_URL", "");
                reference.child(ID_other).child(ID_message).setValue(parameters)
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                            }
                        });


                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Message_Info").child(ID_other);
                HashMap<String, String> param_other = new HashMap<>();
                param_other.put("ID_message", ID_message);
                param_other.put("Sender", firebaseUser.getUid());
                param_other.put("Receiver", ID_other);
                param_other.put("Message", message);
                param_other.put("Message_Time", timeFormat.format(new Date()));
                param_other.put("Message_Date", dateFormat.format(new Date()));
                param_other.put("Message_Seen", "Seen");
                param_other.put("Picture_URL", "");
                ref.child(firebaseUser.getUid()).child(ID_message).setValue(parameters)
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                            }
                        });
            }
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

        //Send Picture Message
        camera_btn_down.setOnClickListener(view ->
                mGetContent.launch("image/*")
        );

        getAllConversation();
    }

    private Uri resultURIPicture;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 143) {
            LoadingDialog progressDialog = new LoadingDialog(context, "Sending...");
            progressDialog.showDialog();
            resultURIPicture = UCrop.getOutput(data);
            StorageReference storageReference1 = storageReference.child(String.valueOf(System.currentTimeMillis()));
            storageReference1.putFile(resultURIPicture)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            storageReference1.getDownloadUrl()
                                    .addOnCompleteListener(task1 -> {
                                        if (task.isSuccessful()) {
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy");
                                            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Message_Info").child(firebaseUser.getUid());
                                            String ID_message = reference.push().getKey();
                                            HashMap<String, String> parameters = new HashMap<>();
                                            parameters.put("ID_message", ID_message);
                                            parameters.put("Sender", firebaseUser.getUid());
                                            parameters.put("Receiver", ID_other);
                                            parameters.put("Message", "Sent a photo");
                                            parameters.put("Message_Time", timeFormat.format(new Date()));
                                            parameters.put("Message_Date", dateFormat.format(new Date()));
                                            parameters.put("Message_Seen", "Seen");
                                            parameters.put("Picture_URL", String.valueOf(task1.getResult()));
                                            reference.child(ID_other).child(ID_message).setValue(parameters)
                                                    .addOnCompleteListener(task3 -> {
                                                        if (!task3.isSuccessful()) {
                                                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                                                        }
                                                    });


                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Message_Info").child(ID_other);
                                            HashMap<String, String> param_other = new HashMap<>();
                                            param_other.put("ID_message", ID_message);
                                            param_other.put("Sender", firebaseUser.getUid());
                                            param_other.put("Receiver", ID_other);
                                            param_other.put("Message", "Sent a photo");
                                            param_other.put("Message_Time", timeFormat.format(new Date()));
                                            param_other.put("Message_Date", dateFormat.format(new Date()));
                                            param_other.put("Message_Seen", "Seen");
                                            param_other.put("Picture_URL", String.valueOf(task1.getResult()));
                                            ref.child(firebaseUser.getUid()).child(ID_message).setValue(parameters)
                                                    .addOnCompleteListener(task2 -> {
                                                        if (!task2.isSuccessful()) {
                                                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                            progressDialog.dismissDialog();
                                        }
                                    }).addOnFailureListener(e -> {
                                        progressDialog.dismissDialog();
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        }
                    }).addOnFailureListener(e -> {
                        progressDialog.dismissDialog();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        } else if (requestCode == UCrop.RESULT_ERROR) {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
        }
    }

    private void getAllConversation() {
        FirebaseDatabase.getInstance().getReference("Message_Info").child(firebaseUser.getUid())
                .child(ID_other)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        message_infoList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                Message_Info message_info = snapshot1.getValue(Message_Info.class);
                                message_infoList.add(message_info);
                            }
                        }
                        Collections.reverse(message_infoList);
                        message_adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}