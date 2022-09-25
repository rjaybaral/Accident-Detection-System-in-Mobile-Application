package com.example.epocketemergencyresponder.Loading;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.epocketemergencyresponder.R;


public class LoadingDialog {

    Dialog loadingDialog;
    private Context context;
    private String message;
    public LoadingDialog(Context context, String message){
        this.context = context;
        this.message = message;
        loadingDialog = new Dialog(context);
    }

    public void showDialog(){
        View view = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null);
        TextView messagetext = view.findViewById(R.id.textmessage);
        messagetext.setText(message);
        loadingDialog.setContentView(view);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }
    public void dismissDialog(){
        loadingDialog.dismiss();
    }
}
