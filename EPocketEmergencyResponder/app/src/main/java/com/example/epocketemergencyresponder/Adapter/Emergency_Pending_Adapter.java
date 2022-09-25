package com.example.epocketemergencyresponder.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epocketemergencyresponder.Model.Emergency_Pending_Info;
import com.example.epocketemergencyresponder.Model.User_Info;
import com.example.epocketemergencyresponder.ProfileSpecificUserActivity;
import com.example.epocketemergencyresponder.R;
import com.example.epocketemergencyresponder.UserRequestAssistanceActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class Emergency_Pending_Adapter extends RecyclerView.Adapter<Emergency_Pending_Adapter.ViewHolder> {

    private Context context;
    private List<Emergency_Pending_Info> emergency_pending_infoList;

    public Emergency_Pending_Adapter(Context context, List<Emergency_Pending_Info> emergency_pending_infoList) {
        this.context = context;
        this.emergency_pending_infoList = emergency_pending_infoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_user_assistance_list, null, false));
    }

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Emergency_Pending_Info einfo = emergency_pending_infoList.get(position);
        //Get Distance Function
        holder.distance_text.setText("Distance       :  "+(((int) Double.parseDouble(einfo.getDistanceLocation())<1000)?  (String.format(java.util.Locale.US,"%.2f", Double.parseDouble(einfo.getDistanceLocation()))+" Meter"): (String.format(java.util.Locale.US,"%.2f", (Double.parseDouble(einfo.getDistanceLocation())/1000))+" Kilometer")));
        holder.fullname_text.setText("Fullname      :  " + einfo.getFirst_Name() + " " + einfo.getLast_Name());
        holder.current_location_address_text.setText(einfo.getCurrent_Location_Address());
        holder.contact_text.setText("Contact No  :  " + einfo.getContact_No());
        holder.location_text.setText("Location       :  " + einfo.getLatitude() + "," + einfo.getLongitude());
        holder.clickMe.setOnClickListener(view -> {
            Dialog myDialog = new Dialog(context);
            View view1 = LayoutInflater.from(context).inflate(R.layout.dialog_user_assistance, null);
            TextView cancelBtn = view1.findViewById(R.id.cancelBtn);
            TextView infoBtn = view1.findViewById(R.id.infoBtn);
            infoBtn.setOnClickListener(view22 -> {
                myDialog.dismiss();
                Intent intent = new Intent(context, ProfileSpecificUserActivity.class);
                intent.putExtra("ID_user", einfo.getID_user());
                context.startActivity(intent);
            });
            cancelBtn.setOnClickListener(view2 -> myDialog.dismiss());

            TextView acceptBtn = view1.findViewById(R.id.acceptBtn);
            acceptBtn.setOnClickListener(view23 -> {
                myDialog.dismiss();
                HashMap<String, Object> parameters = new HashMap<>();
                parameters.put("Status_ID_user", "OTW_"+einfo.getID_user());
                parameters.put("Status", "OTW");
                parameters.put("Status_Type_Rescue","OTW_"+einfo.getType_Rescue());
                FirebaseDatabase.getInstance().getReference("Emergency_Pending").child(einfo.getID_emergency())
                        .updateChildren(parameters);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("OTW_Info");
                String ID_otw = ref.push().getKey();
                HashMap<String, String> param = new HashMap<>();
                param.put("ID_otw", ID_otw);
                param.put("ID_emergency", einfo.getID_emergency());
                param.put("ID_user", einfo.getID_user());
                param.put("ID_response", firebaseUser.getUid());
                param.put("First_Name", einfo.getFirst_Name());
                param.put("Last_Name", einfo.getLast_Name());
                param.put("Birthday", einfo.getBirthday());
                param.put("Contact_No", einfo.getContact_No());
                param.put("Address", einfo.getAddress());
                param.put("Current_Location_Address", einfo.getCurrent_Location_Address());
                param.put("Latitude", einfo.getLatitude());
                param.put("Longitude", einfo.getLongitude());
                param.put("Type_Rescue", einfo.getType_Rescue());
                ref.child(ID_otw).setValue(param);
                UserRequestAssistanceActivity s = (UserRequestAssistanceActivity) context;
                s.finish();
            });
            myDialog.setCancelable(true);
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.setContentView(view1);
            myDialog.show();
        });
    }



    @Override
    public int getItemCount() {
        return emergency_pending_infoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fullname_text, contact_text, current_location_address_text, location_text,distance_text;
        private LinearLayout clickMe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            distance_text = itemView.findViewById(R.id.distance_text);
            clickMe = itemView.findViewById(R.id.clickMe);
            fullname_text = itemView.findViewById(R.id.fullname_text);
            contact_text = itemView.findViewById(R.id.contact_text);
            current_location_address_text = itemView.findViewById(R.id.current_location_address_text);
            location_text = itemView.findViewById(R.id.location_text);
        }
    }
}
