package com.example.hams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private EditText edUsername;
    private EditText edPassword;
    private Button btnLogin;
    private Spinner spinner;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Intent intent;
    private int userType = 0;                                       //for Selected User Type index
    private String utype = "";                                      //for selected User Type String selection
    private ProgressDialog mProgress;                      //progress Dialog for login process

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        edUsername = (EditText) findViewById(R.id.edusername);
        edPassword = (EditText) findViewById(R.id.edpassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        spinner = (Spinner) findViewById(R.id.spUsers);

        //add String to spinner
        String[] arraySpinner = new String[]{"-Select User Type-", "Warden", "Student"};     //for users Spinner Control

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //initilize progress dialog for login
        mProgress = new ProgressDialog(Login.this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);


        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(Login.this, "Please Select User Type !", Toast.LENGTH_SHORT).show();
                userType = 0;
                return;
            }
        });
        //////////////////btnLogin/////////////
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to check internet Connectivity
                if (netCheck.checkConnection(getApplicationContext())) {
                    mProgress.show();

                    final String username = edUsername.getText().toString();
                    final String password = edPassword.getText().toString();

                    //check the number of spinner value and set user type
                    if (userType == 0) {
                        Toast.makeText(Login.this, "Please Select User Type !", Toast.LENGTH_SHORT).show();
                        spinner.setFocusable(true);
                        mProgress.dismiss();
                    }
                    if (userType == 1) {
                        utype = "warden";
                        intent = new Intent(Login.this, wardenHome.class);
                    }
                    if (userType == 2) {
                        utype = "student";
                        intent = new Intent(Login.this, studentHome.class);
                    }

                    if (TextUtils.isEmpty(edUsername.getText())) {
                        edUsername.setError("Username Required");
                        mProgress.dismiss();
                    } else if (TextUtils.isEmpty(edPassword.getText())) {
                        edPassword.setError("Password Required");
                        mProgress.dismiss();
                    }

                    ////get Value from database and Check The Value///////////////////////
                    else if (userType != 0) {
                        db.collection(utype).document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();

                                    String Fusername = documentSnapshot.getString("username");//Firebase Username
                                    String Fpassword = documentSnapshot.getString("password");//Firebase Password
                                    if (username.equals(Fusername) && password.equals(Fpassword)) {
                                        mProgress.dismiss();
                                        String un = username,
                                                ps = password,
                                                ut = utype;

                                        if (userType == 2) {
                                            Long rl = Long.parseLong((String) documentSnapshot.get("RollNo"));
                                            intent.putExtra("iRollNo", rl);
                                        }
                                        intent.putExtra("docId", documentSnapshot.getId());
                                        intent.putExtra("iUserName", un);//edUsername.getText().toString());
                                        intent.putExtra("iPassword", ps);//,edPassword.getText().toString());
                                        intent.putExtra("iUtype", ut);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(Login.this, "Incorrect Username Or Password !", Toast.LENGTH_SHORT).show();
                                        mProgress.dismiss();
                                    }
                                } else {
                                    mProgress.dismiss();
                                    Toast.makeText(Login.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(Login.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "You Are Offline  Please Check Your Internet And Retry.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


