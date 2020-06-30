package com.example.hams.general;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hams.R;
import com.example.hams.warden.wardenHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MaintainStudent extends AppCompatActivity {
    private Button reg, update, del, newSem;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Intent intent;
    private EditText rollnum;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain);
        reg = findViewById(R.id.btnStuRegister);
        update = findViewById(R.id.btnStuUpdate);
        del = findViewById(R.id.btnstuDel);
        rollnum = findViewById(R.id.edRollNoToMaintain);
        newSem = findViewById(R.id.btnstuPromot);
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
                    intent = new Intent(MaintainStudent.this, wardenHome.updateStudent.class);
                    intent.putExtra("rollNo", rollnum.getText().toString());
                    startActivity(intent);
                }
            }
        });
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(rollnum.getText())) {
                    rollnum.setError("Roll No Must Required");
                } else if ((!TextUtils.isEmpty(rollnum.getText()))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MaintainStudent.this);
                    builder.setTitle(getString(R.string.student_maintain_delete));
                    builder.setPositiveButton(getString(R.string.exit_positive), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            db.collection("StudentDetails")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (document.getString("RollNo").contains("" + rollnum.getText().toString())) {
                                                        mProgress.show();
                                                        Toast.makeText(MaintainStudent.this, "" + document.getId(), Toast.LENGTH_SHORT).show();
                                                        db.collection("StudentDetails").document(document.getId()).delete();
                                                        db.collection("student").document(document.getId()).delete();
                                                        Toast.makeText(MaintainStudent.this, "Student Details Deleted", Toast.LENGTH_SHORT).show();
                                                        mProgress.dismiss();
                                                    } else {
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
                    builder.setNegativeButton(getString(R.string.exit_negative), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.show();
                }
            }

        });
        newSem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("StudentDetails")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.getString("RollNo").contains("" + rollnum.getText().toString())) {
                                            mProgress.show();

                                            mProgress.dismiss();
                                        } else {
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