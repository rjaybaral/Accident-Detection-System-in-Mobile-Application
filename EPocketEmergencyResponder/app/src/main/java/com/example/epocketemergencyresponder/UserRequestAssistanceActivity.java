package com.example.epocketemergencyresponder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.epocketemergencyresponder.Adapter.Emergency_Pending_Adapter;
import com.example.epocketemergencyresponder.Model.Emergency_Pending_Info;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserRequestAssistanceActivity extends AppCompatActivity {

    private Context context = this;
    private Emergency_Pending_Adapter emergency_pending_adapter;
    private List<Emergency_Pending_Info> emergency_pending_infoList;
    private RecyclerView recyclerView;
    private ImageView back_button,distance_sort_button;
    private String Type = "", Latitude = "", Longitude= "";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_request_assistance);
        intent = getIntent();
        Type = intent.getStringExtra("Type");
        Latitude = intent.getStringExtra("Latitude");
        Longitude = intent.getStringExtra("Longitude");
        back_button = findViewById(R.id.back_button);
        emergency_pending_infoList = new ArrayList<>();
        distance_sort_button = findViewById(R.id.distance_sort_button);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        emergency_pending_adapter = new Emergency_Pending_Adapter(context, emergency_pending_infoList);
        recyclerView.setAdapter(emergency_pending_adapter);

        back_button.setOnClickListener(view -> finish());
        getEmergencyListAssistance(Type);

        distance_sort_button.setOnClickListener(view -> {
            if (res){
                Collections.sort(emergency_pending_infoList, (o1, o2) -> (int) Double.parseDouble(o1.getDistanceLocation()) - (int) Double.parseDouble(o2.getDistanceLocation()));
            }else{
                Collections.sort(emergency_pending_infoList, (o1, o2) -> (int) Double.parseDouble(o1.getDistanceLocation()) - (int) Double.parseDouble(o2.getDistanceLocation()));
                Collections.reverse(emergency_pending_infoList);
            }
            emergency_pending_adapter.notifyDataSetChanged();
            res = !res;
        });
    }
    boolean res = true;

    private void getEmergencyListAssistance(String type) {
        FirebaseDatabase.getInstance().getReference("Emergency_Pending").orderByChild("Status_Type_Rescue").equalTo("Pending_" + type)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        emergency_pending_infoList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                Emergency_Pending_Info einfo = snapshot1.getValue(Emergency_Pending_Info.class);
                                float results[] = new float[10];
                                Location.distanceBetween(
                                        Double.parseDouble(Latitude),
                                        Double.parseDouble(Longitude),
                                        Double.parseDouble(einfo.getLatitude()),
                                        Double.parseDouble(einfo.getLongitude()),
                                        results
                                );
                                einfo.setDistanceLocation(results[0]+"");
                                emergency_pending_infoList.add(einfo);
                            }
                        }

//                        Collections.sort(emergency_pending_infoList, (o1, o2) -> (int) Double.parseDouble(o1.getDistanceLocation()) - (int) Double.parseDouble(o2.getDistanceLocation()));
                        Collections.reverse(emergency_pending_infoList);
                        emergency_pending_adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}