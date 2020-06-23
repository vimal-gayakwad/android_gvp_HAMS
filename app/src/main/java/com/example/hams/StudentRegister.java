package com.example.hams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StudentRegister extends AppCompatActivity {

    EditText sName,Address,Email,Contact,uname,password,birthdate,RollNo;
    Button submit;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    Spinner dept;
    String department;
    Long LastRollNo;
    String[] arraySpinner = new String[] {"-select Department-","AUDIO AND VISUAL","ECONOMICS","GUJARATI","HISTORY","M.C.A.","M.S.W"};     //for users Spinner Control
    private DatePickerDialog.OnDateSetListener mDatesetListener;
    Calendar cal=Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // control Variables

        sName=(EditText)findViewById(R.id.edsName);
        Address=(EditText)findViewById(R.id.edAddress);
        submit=(Button)findViewById(R.id.btnSubmit);
        dept=(Spinner)findViewById(R.id.spDept);
        birthdate=(EditText)findViewById(R.id.edBDate);
        Contact=(EditText)findViewById(R.id.edContactNo);
        Email=(EditText)findViewById(R.id.edEmail);
        uname=(EditText)findViewById(R.id.edSUsername);
        password=(EditText)findViewById(R.id.edSPassword);
        RollNo=(EditText)findViewById(R.id.edRollNo);

        birthdate.setText("dd-mm-yyyy");

        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int year=cal.get(Calendar.YEAR);
                int month=cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog=new DatePickerDialog(StudentRegister.this,android.R.style.Theme_Holo_Dialog_MinWidth,mDatesetListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });
        mDatesetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                cal.set(year, month, dayOfMonth);
                String dateString = sdf.format(cal.getTime());
                birthdate.setText(dateString);
            }
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dept.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        db.collection("StudentLastRollNo").document("LastRollNo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot=task.getResult();
                LastRollNo=documentSnapshot.getLong("RollNo");
                    RollNo.setText(""+LastRollNo);
                }
                else{
                    Toast.makeText(StudentRegister.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });



        // todo UPLOAD DATA TO FIREBASE
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo CREATE STRING VARIABLE TO STORE EDITTEXT VALUE
                if(TextUtils.isEmpty(RollNo.getText())){
                    sName.setError("RollNo Required");
                }
                if(TextUtils.isEmpty(sName.getText())){
                    sName.setError("StudentName Required");
                }
                if(TextUtils.isEmpty(Address.getText())){
                    Address.setError("Address Required");
                }
                if(TextUtils.equals(birthdate.getText().toString(),"dd-mm-yyyy")){
                    birthdate.setError("Select Birthdate");
                }
                if(TextUtils.isEmpty(Contact.getText())){
                    Contact.setError("Contact Number Requiered");
                }
                if(TextUtils.isEmpty(Email.getText())){
                    Email.setError("Email required");
                }
                if(TextUtils.isEmpty(uname.getText())){
                    uname.setError("StudentName Required");
                }
                if(TextUtils.isEmpty(password.getText())){
                    password.setError("Password Required");
                }
                if(dept.getSelectedItem().equals("-select Department-")){
                    Toast.makeText(StudentRegister.this, "Select Department", Toast.LENGTH_SHORT).show();;
                }
                else {
                    String sname = sName.getText().toString(),
                            rollNo=RollNo.getText().toString(),
                            address = Address.getText().toString(),
                            bdate = birthdate.getText().toString(),
                            cont = Contact.getText().toString(),
                            email = Email.getText().toString(),
                            Uname = uname.getText().toString(),
                            passwd = password.getText().toString();

                            department=dept.getSelectedItem().toString();

                    // Add studentDetails to studentDetails Collecction
                    Map<String, Object> user = new HashMap<>();
                    user.put("StudentName", sname);
                    user.put("Address", address);
                    user.put("Birthdate", bdate);
                    user.put("Department", department);
                    user.put("Contact", cont);
                    user.put("Email", email);
                    user.put("RollNo",rollNo);
                    //Add Stdunet UserName And Password to student Table
                    final Map<String, Object> LoginData = new HashMap<>();
                    LoginData.put("RollNo",rollNo);
                    LoginData.put("username", Uname);
                    LoginData.put("password", passwd);
                    final Map<String, Object> AttData = new HashMap<>();
                    AttData.put("RollNo",rollNo);
                    AttData.put("username", Uname);


///get last added rollnumber


// Add a new document with a generated ID
                    db.collection("StudentDetails").document(Uname)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    final Map<String,Long> LRollNo = new HashMap<>();
                                    LRollNo.put("RollNo", LastRollNo + 1);
                                    db.collection("StudentLastRollNo").document("LastRollNo").set(LRollNo);
                                    Toast.makeText(StudentRegister.this, "Added Successfully", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(StudentRegister.this, "Error While Adding " + e.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            });

                    db.collection("student").document(uname.getText().toString()).set(LoginData);
                    db.collection("attendanceList").document(rollNo).set(AttData);

                    //Update Roll Number After SuccessFully Added


                    sName.setText("");
                    Address.setText("");
                    birthdate.setText("dd-mm-yyyy");
                    Contact.setText("");
                    Email.setText("");
                    uname.setText("");
                    password.setText("");
                    RollNo.setText("");

                }
            }
        });
    }
}
