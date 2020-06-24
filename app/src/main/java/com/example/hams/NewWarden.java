package com.example.hams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class NewWarden extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText adminUname, adminPass;
    private Map<String, Object> user;
    private Button btnSubmit;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_warden);
        adminUname = (EditText) findViewById(R.id.edWUsername);
        adminPass = (EditText) findViewById(R.id.edWPassword);
        btnSubmit = (Button) findViewById(R.id.btNewWarden);

        mProgress = new ProgressDialog(NewWarden.this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user = new HashMap<>();
                user.put("username", adminUname.getText().toString());
                user.put("password", adminPass.getText().toString());

                if (TextUtils.isEmpty(adminUname.getText())) {
                    adminUname.setError("User Name Required");
                }

                if (TextUtils.isEmpty(adminPass.getText())) {
                    adminPass.setError("Password Must Required");
                } else if ((!TextUtils.isEmpty(adminUname.getText())) && (!TextUtils.isEmpty(adminPass.getText()))) {
                    mProgress.show();
                    db.collection("warden").document(adminUname.getText().toString())
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(NewWarden.this, "Warden Added SuccessFully", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(NewWarden.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }
}