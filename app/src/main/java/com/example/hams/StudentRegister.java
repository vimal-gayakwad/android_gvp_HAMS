package com.example.hams;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StudentRegister extends AppCompatActivity {

    private EditText sName, Address, Email, Contact, uname, password, birthdate, RollNo;
    private Button submit;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner dept, semeter;
    private String department;
    private ProgressDialog mProgress;
    private DatePickerDialog.OnDateSetListener mDatesetListener;
    Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // control Variables
        sName = findViewById(R.id.edsName);
        Address = findViewById(R.id.edAddress);
        submit = findViewById(R.id.btnSubmit);
        dept = findViewById(R.id.spDept);
        birthdate = findViewById(R.id.edBDate);
        Contact = findViewById(R.id.edContactNo);
        Email = findViewById(R.id.edEmail);
        uname = findViewById(R.id.edSUsername);
        password = findViewById(R.id.edSPassword);
        RollNo = findViewById(R.id.edRollNo);
        semeter = findViewById(R.id.spSem);

        birthdate.setText("dd-mm-yyyy");
        mProgress = new ProgressDialog(StudentRegister.this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        mProgress.show();
        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(StudentRegister.this, android.R.style.Theme_Holo_Dialog_MinWidth, mDatesetListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });
        mDatesetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                cal.set(year, month, dayOfMonth);
                String dateString = sdf.format(cal.getTime());
                birthdate.setText(dateString);
            }
        };

//        db.collection("StudentLastRollNo").document("LastRollNo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//                    LastRollNo = documentSnapshot.getLong("RollNo");
//                    RollNo.setText("" + LastRollNo);
//                    mProgress.dismiss();
//                } else {
//                    mProgress.dismiss();
//                    Toast.makeText(StudentRegister.this, "Error", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo CREATE STRING VARIABLE TO STORE EDITTEXT VALUE
                if (TextUtils.isEmpty(RollNo.getText())) {
                    sName.setError(getString(R.string.register_setError_rollno));
                }
                if (TextUtils.isEmpty(sName.getText())) {
                    sName.setError(getString(R.string.register_serError_sname));
                }
                if (TextUtils.isEmpty(Address.getText())) {
                    Address.setError(getString(R.string.register_setError_address));
                }
                if (TextUtils.equals(birthdate.getText().toString(), "dd-mm-yyyy")) {
                    birthdate.setError(getString(R.string.register_setError_birthdate));
                }
                if (TextUtils.isEmpty(Contact.getText())) {
                    Contact.setError(getString(R.string.register_setError_contact));
                }
                if (TextUtils.isEmpty(Email.getText())) {
                    Email.setError(getString(R.string.register_setError_email));
                }
                if (TextUtils.isEmpty(uname.getText())) {
                    uname.setError(getString(R.string.register_setError_uname));
                }
                if (TextUtils.isEmpty(password.getText())) {
                    password.setError(getString(R.string.register_setError_password));
                }
                if (dept.getSelectedItem().equals("Choose")) {
                    Toast.makeText(StudentRegister.this, getString(R.string.register_toast_select_dept), Toast.LENGTH_SHORT).show();
                }
                if (semeter.getSelectedItem().equals("Choose")) {
                    Toast.makeText(StudentRegister.this, "", Toast.LENGTH_SHORT).show();
                } else {
                    mProgress.show();
                    String sname = sName.getText().toString(),
                            rollNo = RollNo.getText().toString(),
                            address = Address.getText().toString(),
                            bdate = birthdate.getText().toString(),
                            cont = Contact.getText().toString(),
                            email = Email.getText().toString(),
                            Uname = uname.getText().toString(),
                            passwd = password.getText().toString(),
                            semestersp = semeter.getSelectedItem().toString();

                    department = dept.getSelectedItem().toString();
                    // Add studentDetails to studentDetails Collection
                    Map<String, Object> user = new HashMap<>();
                    user.put("StudentName", sname);
                    user.put("Address", address);
                    user.put("Birthdate", bdate);
                    user.put("Department", department);
                    user.put("Contact", cont);
                    user.put("Email", email);
                    user.put("RollNo", rollNo);
                    user.put("Semester", semestersp);
                    //Add Stdunet UserName And Password to student Table
                    final Map<String, Object> LoginData = new HashMap<>();
                    LoginData.put("RollNo", rollNo);
                    LoginData.put("username", Uname);
                    LoginData.put("password", passwd);
                    final Map<String, Object> AttData = new HashMap<>();
                    AttData.put("RollNo", rollNo);
                    AttData.put("username", Uname);
                    // Add a new document with a generated ID
                    db.collection("StudentDetails").document(Uname)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //final Map<String, Long> LRollNo = new HashMap<>();
                                    //LRollNo.put("RollNo", LastRollNo + 1);
                                    //db.collection("StudentLastRollNo").document("LastRollNo").set(LRollNo);
                                    db.collection("student").document(uname.getText().toString()).set(LoginData);
                                    db.collection("attendanceList").document(RollNo.getText().toString()).set(AttData);

                                    mProgress.dismiss();
                                    Toast.makeText(StudentRegister.this, getString(R.string.register_toast_added_sucessfully), Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mProgress.dismiss();
                                    Toast.makeText(StudentRegister.this, getString(R.string.register_toast_added_failed) + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                    sName.setText("");
                    Address.setText("");
                    birthdate.setText("dd-mm-yyyy");
                    Contact.setText("");
                    Email.setText("");
                    uname.setText("");
                    password.setText("");
                    RollNo.setText((""));
                }
            }
        });
    }
}