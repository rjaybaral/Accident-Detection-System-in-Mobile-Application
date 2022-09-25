package com.example.epocketemergencyresponder.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epocketemergencyresponder.Model.Message_Info;
import com.example.epocketemergencyresponder.ProfilePictureShowActivity;
import com.example.epocketemergencyresponder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class Message_Adapter extends RecyclerView.Adapter<Message_Adapter.ViewHolder> {

    private Context context;
    private List<Message_Info> message_infoList;
    private final int LEFT = 0;
    private final int RIGHT = 1;
    public Message_Adapter(Context context, List<Message_Info> message_infoList){
        this.context = context;
        this.message_infoList = message_infoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == RIGHT){
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.content_message_right, parent, false));
        }else{
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.content_message_left, parent, false));
        }
    }

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message_Info message_info = message_infoList.get(position);
        holder.message_text.setText(message_info.getMessage());
        if (message_info.getMessage().equals("Sent a photo") && !(message_info.getPicture_URL().equals(""))){
            holder.message_text.setVisibility(View.GONE);
            holder.send_pic.setVisibility(View.VISIBLE);
            holder.show_pic.setVisibility(View.VISIBLE);
            try{
                Picasso.get().load(Uri.parse(message_info.getPicture_URL())).into(holder.send_pic);
            }catch (Exception ex){
            }
        }else{
            holder.message_text.setVisibility(View.VISIBLE);
            holder.send_pic.setVisibility(View.GONE);
            holder.show_pic.setVisibility(View.GONE);
        }

        holder.send_pic.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfilePictureShowActivity.class);
            intent.putExtra("Profile_URL", message_info.getPicture_URL());
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(holder.send_pic, "image1");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
            context.startActivity(intent, options.toBundle());
        });


        if (firebaseUser.getUid().equals(message_info.getReceiver())){
            HashMap<String, Object> param = new HashMap<>();
            param.put("Message_Seen", "Sent");
            FirebaseDatabase.getInstance()
                    .getReference("Message_Info").child(firebaseUser.getUid())
                    .child(message_info.getSender()).child(message_info.getID_message())
                    .updateChildren(param);
        }

//        DatabaseReference ref =
//        ref.updateChildren()
    }

    @Override
    public int getItemCount() {
        return message_infoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser.getUid().equals(message_infoList.get(position).getSender())){
            return RIGHT;
        }else{
            return LEFT;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView message_text;
        private ImageView profile_image,send_pic;
        private CardView show_pic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_pic = itemView.findViewById(R.id.show_pic);
            message_text = itemView.findViewById(R.id.message_text);
            profile_image = itemView.findViewById(R.id.profile_image);
            send_pic = itemView.findViewById(R.id.send_pic);
        }
    }

}
