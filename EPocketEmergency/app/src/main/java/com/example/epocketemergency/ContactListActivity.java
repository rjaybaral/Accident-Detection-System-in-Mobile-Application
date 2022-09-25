package com.example.epocketemergency;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epocketemergency.Adapter.Emergency_Contact_Adapter;
import com.example.epocketemergency.Model.Emergency_Contact_Info;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {

    private Context context = this;
    private ImageView back_button,add_emergency_contact;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private RecyclerView recyclerView;
    private List<Emergency_Contact_Info> emergency_contact_infoList;
    private Emergency_Contact_Adapter emergency_contact_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        emergency_contact_infoList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        add_emergency_contact = findViewById(R.id.add_emergency_contact);
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> finish());

        emergency_contact_adapter = new Emergency_Contact_Adapter(context, emergency_contact_infoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(emergency_contact_adapter);
        getContactList();

        add_emergency_contact.setOnClickListener(view -> {
            Dialog myDialog = new Dialog(context);
            View view1 = LayoutInflater.from(context).inflate(R.layout.dialog_add_emergency, null);
            TextView cancelButton = view1.findViewById(R.id.cancelButton);
            cancelButton.setOnClickListener(view2 -> {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                myDialog.dismiss();
            });
            EditText name_text = view1.findViewById(R.id.name_text);
            EditText contact_text = view1.findViewById(R.id.contact_text);
            TextView addButton = view1.findViewById(R.id.addButton);
            addButton.setOnClickListener(view22 -> {
                String name = name_text.getText().toString().trim();
                String phone = contact_text.getText().toString().trim();
                if (name.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(context, "All fields are required!", Toast.LENGTH_LONG).show();
                } else {
                    if (phone.length() == 11) {

                        Query query = FirebaseDatabase.getInstance().getReference("Emergency_Contact")
                                .child(firebaseUser.getUid()).orderByChild("Contact_Number").equalTo(phone);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Toast.makeText(context, "Sorry, Already exist number!", Toast.LENGTH_LONG).show();
                                } else {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Emergency_Contact")
                                            .child(firebaseUser.getUid());
                                    String ID_contact = reference.push().getKey();
                                    HashMap<String, String> parameters = new HashMap<>();
                                    parameters.put("ID_contact", ID_contact);
                                    parameters.put("Name", name);
                                    parameters.put("Contact_Number", phone);
                                    reference.child(ID_contact).setValue(parameters)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                    imm.hideSoftInputFromWindow(view22.getWindowToken(), 0);
                                                    myDialog.dismiss();
                                                    Toast.makeText(context, "Add Contact Succeed!", Toast.LENGTH_LONG).show();
                                                }
                                            }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });


                    } else {
                        Toast.makeText(context, "Sorry, Invalid Number", Toast.LENGTH_LONG).show();
                    }
                }
            });
            myDialog.setCancelable(false);
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.setContentView(view1);
            myDialog.show();
        });
    }

    private void getContactList() {
        FirebaseDatabase.getInstance().getReference("Emergency_Contact")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        emergency_contact_infoList.clear();
                        if (snapshot.exists()){
                            for (DataSnapshot snapshot1: snapshot.getChildren()){
                                Emergency_Contact_Info emergency_contact_info = snapshot1.getValue(Emergency_Contact_Info.class);
                                emergency_contact_infoList.add(emergency_contact_info);
                            }
                        }else{
                            emergency_contact_infoList.clear();
                            emergency_contact_adapter.notifyDataSetChanged();
                        }
                        Collections.sort(emergency_contact_infoList, (emergency_contact_info, t1) -> emergency_contact_info.getName().compareTo(t1.getName()));
                        emergency_contact_adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}