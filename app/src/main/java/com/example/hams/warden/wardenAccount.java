package com.example.hams.warden;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hams.R;
import com.example.hams.general.AppState;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class wardenAccount extends AppCompatActivity {
    private Button btnSubmit, btnNewAdmin;
    private EditText edOldPassword, edNewPassword, edConfirmPassword;
    private String uname, dbPassword;
    private Intent intent;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Map<String, String> LoginData = new HashMap<>();
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warden_account);

        btnSubmit = findViewById(R.id.btnChangePasswd);
        edNewPassword = findViewById(R.id.edNewPassword);
        edOldPassword = findViewById(R.id.edOldPass2);
        edConfirmPassword = findViewById(R.id.edConfPassword);
        btnNewAdmin = findViewById(R.id.btnAddNewWarden);
        //initilize progress dialog for login
        mProgress = new ProgressDialog(wardenAccount.this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        intent = getIntent();
        dbPassword = intent.getStringExtra("iPassword1");
        uname = intent.getStringExtra("iUserName1");
        btnNewAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(wardenAccount.this, wardenHome.NewWarden.class));
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edOldPassword.getText())) {
                    edOldPassword.setError("Enter Password");
                }
                if (!TextUtils.equals(dbPassword, edOldPassword.getText())) {
                    edOldPassword.setError("Enter Correct Password");
                }
                if (TextUtils.isEmpty(edNewPassword.getText())) {
                    edNewPassword.setError("Enter New Password");
                }
                if (TextUtils.isEmpty(edConfirmPassword.getText())) {
                    edConfirmPassword.setError("Confirm Password");
                }
                if (!TextUtils.equals(edNewPassword.getText().toString(), edConfirmPassword.getText().toString())) {
                    edConfirmPassword.setError("Password Do Not Match");
                } else if ((!TextUtils.isEmpty(edConfirmPassword.getText()) && (!TextUtils.isEmpty(edOldPassword.getText()) && (!TextUtils.isEmpty(edNewPassword.getText()))))) {
                    mProgress.show();
                    LoginData.put("password", edConfirmPassword.getText().toString());
                    db.collection("warden").document(uname).update("password", edConfirmPassword.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProgress.dismiss();
                                    Toast.makeText(wardenAccount.this, "Updated Successfully \n Please Login with New Password", Toast.LENGTH_LONG).show();
                                    intent = new Intent(getApplicationContext(), AppState.Login.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(wardenAccount.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    mProgress.dismiss();
                                }
                            });
                }
            }
        });
    }
}