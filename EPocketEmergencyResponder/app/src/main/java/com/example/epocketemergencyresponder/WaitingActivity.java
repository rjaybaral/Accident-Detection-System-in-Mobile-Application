package com.example.epocketemergencyresponder;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.epocketemergencyresponder.Model.User_Info;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WaitingActivity extends AppCompatActivity {
    private Context context = this;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference ref=FirebaseDatabase.getInstance().getReference("User_Info")
            .child(firebaseUser.getUid());
    private ValueEventListener valueEventListener;


    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        Dialog myDialog = new Dialog(context);
        View view1 = LayoutInflater.from(context).inflate(R.layout.dialog_status_pending, null);
        TextView cancelButton = view1.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> myDialog.dismiss());
        myDialog.setContentView(view1);
        myDialog.setCancelable(false);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Dialog myDialogBlock = new Dialog(context);
        View view1block = LayoutInflater.from(context).inflate(R.layout.dialog_status_block, null);
        TextView cancelButton1 = view1block.findViewById(R.id.cancelButton);
        cancelButton1.setOnClickListener(view -> myDialogBlock.dismiss());
        myDialogBlock.setContentView(view1block);
        myDialogBlock.setCancelable(false);
        myDialogBlock.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        valueEventListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDialog.dismiss();
                myDialogBlock.dismiss();
                if (snapshot.exists()) {
                    User_Info user_info = snapshot.getValue(User_Info.class);
                    if (user_info.getStatus().equals("Pending")) {
                        myDialog.show();
                    }else if (user_info.getStatus().equals("Block")) {
                        myDialogBlock.show();
                    }else if (user_info.getStatus().equals("Accepted") && i==0) {
                        i++;
                        ref.removeEventListener(valueEventListener);
                        startActivity(new Intent(context, MainActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}