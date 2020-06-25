package com.example.hams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MaintainStudent extends AppCompatActivity {
    private Button reg, update, del;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Intent intent;
    private EditText rollnum;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain);
        reg = (Button) findViewById(R.id.btnStuRegister);
        update = (Button) findViewById(R.id.btnStuUpdate);
        del = (Button) findViewById(R.id.btnstuDel);
        rollnum = (EditText) findViewById(R.id.edRollNoToMaintain);

        mProgress = new ProgressDialog(MaintainStudent.this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), StudentRegister.class));
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(rollnum.getText())) {
                    rollnum.setError("Enter Roll Number");
                } else if (!TextUtils.isEmpty(rollnum.getText())) {
                    intent = new Intent(MaintainStudent.this, updateStudent.class);
                    intent.putExtra("rollNo", rollnum.getText().toString());
                    startActivity(intent);
                }
            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(rollnum.getText())) {
                    rollnum.setError("Password Must Required");
                } else if ((!TextUtils.isEmpty(rollnum.getText())))
                    mProgress.show();
                db.collection("StudentDetails")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if(document.getString("RollNo").contains(""+rollnum.getText().toString())){
                                            Toast.makeText(MaintainStudent.this, ""+document.getId(), Toast.LENGTH_SHORT).show();
                                            db.collection("StudentDetails").document(document.getId()).delete();
                                            db.collection("student").document(document.getId()).delete();
                                            Toast.makeText(MaintainStudent.this, "Student Details Deleted", Toast.LENGTH_SHORT).show();
                                            mProgress.dismiss();
                                        }
                                        else{
                                            Toast.makeText(MaintainStudent.this, "No Student Found", Toast.LENGTH_SHORT).show();
                                            mProgress.dismiss();
                                        }
                                    }
                                } else {
                                    Toast.makeText(MaintainStudent.this, "No Record Found", Toast.LENGTH_SHORT).show();
                                    mProgress.dismiss();
                                }
                            }
                        });

            }
        });
    }
}
