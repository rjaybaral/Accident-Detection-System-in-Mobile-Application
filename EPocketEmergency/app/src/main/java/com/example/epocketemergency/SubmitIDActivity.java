package com.example.epocketemergency;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class SubmitIDActivity extends AppCompatActivity {
    private Context context = this;
    private Button btn, choosePhotoBtn, submitIDPhoto;
    private Spinner spinnerIDType;
    private String IDType = "";
    private ActivityResultLauncher<String> mGetContentValidId;
    private ImageView showPhotoID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_idactivity);
        showPhotoID = findViewById(R.id.showPhotoID);
        spinnerIDType = findViewById(R.id.spinnerIDType);
        choosePhotoBtn = findViewById(R.id.choosePhotoBtn);
        submitIDPhoto = findViewById(R.id.submitIDPhoto);

        //Get ID Type List
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.idTypeList, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIDType.setAdapter(adapter);
        spinnerIDType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                IDType = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mGetContentValidId = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                Uri uri = Uri.parse(result.toString());
                String dest_uri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
                UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), dest_uri)))
                        .withOptions(new UCrop.Options())
                        .withAspectRatio(0, 0)
                        .withMaxResultSize(2000, 2000)
                        .useSourceImageAspectRatio()
                        .start((Activity) context, 144);
            }
        });
        choosePhotoBtn.setOnClickListener(view -> mGetContentValidId.launch("image/*"));

        submitIDPhoto.setOnClickListener(view -> {
            if (IDType.isEmpty() || IDType.equals("Select")) {
                Toast.makeText(context, "Please choose ID type...", Toast.LENGTH_LONG).show();
            } else if (resultURIID == null) {
                Toast.makeText(context, "Please choose photo...", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent();
                intent.putExtra("resultURIID", "" + ((resultURIID == null) ? "" : resultURIID));
                intent.putExtra("IDType", IDType);
                setResult(1, intent);
                Dialog myDialog = new Dialog(context);
                View view1 = LayoutInflater.from(context).inflate(R.layout.dialog_success_submit_id, null);
                TextView okayButton = view1.findViewById(R.id.okayButton);
                okayButton.setOnClickListener(view2 -> {
                    myDialog.dismiss();
                    finish();
                });
                myDialog.setContentView(view1);
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.setCancelable(false);
                myDialog.show();
            }
        });

//        btn = findViewById(R.id.btn);
//        btn.setOnClickListener(view -> {
//            Intent intent = new Intent();
//            intent.putExtra("result","");
//            setResult(1, intent);
//            finish();
//        });
    }

    private Uri resultURIID = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 144) {
            resultURIID = UCrop.getOutput(data);
            showPhotoID.setImageURI(resultURIID);
        } else if (requestCode == UCrop.RESULT_ERROR) {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("resultURIID", "");
        setResult(1, intent);
        super.onBackPressed();
    }
}