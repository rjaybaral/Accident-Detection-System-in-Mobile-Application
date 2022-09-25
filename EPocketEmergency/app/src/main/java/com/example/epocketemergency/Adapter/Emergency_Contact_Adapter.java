package com.example.epocketemergency.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epocketemergency.Model.Emergency_Contact_Info;
import com.example.epocketemergency.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Emergency_Contact_Adapter extends RecyclerView.Adapter<Emergency_Contact_Adapter.ViewHolder> {

    private Context context;
    private List<Emergency_Contact_Info> emergency_contact_infoList;

    public Emergency_Contact_Adapter(Context context, List<Emergency_Contact_Info> emergency_contact_infoList) {
        this.context = context;
        this.emergency_contact_infoList = emergency_contact_infoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_contact_list, null, false));
    }

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int []colorList = {R.color.c1, R.color.c2, R.color.c3,
                R.color.c4, R.color.c5, R.color.c6};
        Emergency_Contact_Info emergency_contact_info = emergency_contact_infoList.get(position);

        holder.first_text.setText(emergency_contact_info.getName().substring(0, 1));
        holder.first_text.setBackgroundResource(colorList[position%6]);
        holder.name_text.setText(emergency_contact_info.getName());
        holder.contact_text.setText(emergency_contact_info.getContact_Number());
        holder.deleteButton.setOnClickListener(view -> {
            Dialog myDialog = new Dialog(context);
            View view1 = LayoutInflater.from(context).inflate(R.layout.dialog_delete_contact, null);
            TextView cancelButton = view1.findViewById(R.id.cancelButton);
            cancelButton.setOnClickListener(view22 -> myDialog.dismiss());
            TextView yesButton = view1.findViewById(R.id.yesButton);
            yesButton.setOnClickListener(view2 -> {
                myDialog.dismiss();
                FirebaseDatabase.getInstance().getReference("Emergency_Contact")
                        .child(firebaseUser.getUid()).child(emergency_contact_info.getID_contact())
                        .removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Delete Contact Succeed!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                            }
                        });
            });
            myDialog.setContentView(view1);
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.setCancelable(false);
            myDialog.show();

        });
    }

    @Override
    public int getItemCount() {
        return emergency_contact_infoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView first_text, name_text, contact_text, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            first_text = itemView.findViewById(R.id.first_text);
            name_text = itemView.findViewById(R.id.name_text);
            contact_text = itemView.findViewById(R.id.contact_text);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

}
