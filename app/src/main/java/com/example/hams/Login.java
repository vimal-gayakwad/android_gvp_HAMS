package com.example.hams;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private EditText edUsername, edPassword;
    private Button btnLogin;
    private Spinner spinnerUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Intent intent;
    private int userType = 0;
    private String utype = "";
    private ProgressDialog mProgress;
    private SharedPreferences pref;//to retrive data from device
    private SharedPreferences.Editor editor; //to store data from device
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        flag = pref.getBoolean("login", false);

        if (flag == true) {

            if (pref.getString("utype", "").equals("student")) {
                intent = new Intent(getApplicationContext(), studentHome.class);
            } else if (pref.getString("utype", "").equals("warden")) {
                intent = new Intent(getApplicationContext(), wardenHome.class);
            }

            intent.putExtra("iUserName", pref.getString("username", ""));//edUsername.getText().toString());
            intent.putExtra("iPassword", pref.getString("password", ""));//,edPassword.getText().toString());
            intent.putExtra("iUtype", pref.getString("utype", ""));
            intent.putExtra("iRollNo", pref.getLong("RollNo", 0));
            startActivity(intent);
        } else {
            edUsername = findViewById(R.id.edusername);
            edPassword = findViewById(R.id.edpassword);
            btnLogin = findViewById(R.id.btnLogin);
            spinnerUser = findViewById(R.id.spUsers);
            //initilize progress dialog for login
            mProgress = new ProgressDialog(Login.this);
            mProgress.setTitle(getString(R.string.mprogress_title));
            mProgress.setMessage(getString(R.string.mprogress_msg));
            mProgress.setCancelable(false);
            mProgress.setIndeterminate(true);
            spinnerUser.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    userType = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Toast.makeText(Login.this, getString(R.string.login_toast_utype), Toast.LENGTH_SHORT).show();
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
                        //check the number of spinnerUser value and set user type
                        if (userType == 0) {
                            Toast.makeText(Login.this, getString(R.string.login_toast_utype), Toast.LENGTH_SHORT).show();
                            spinnerUser.setFocusable(true);
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
                            edUsername.setError(getString(R.string.login_setError_uname));
                            mProgress.dismiss();
                        } else if (TextUtils.isEmpty(edPassword.getText())) {
                            edPassword.setError(getString(R.string.login_setError_password));
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
                                                long rl = Long.parseLong((String) documentSnapshot.get("RollNo"));
                                                intent.putExtra("iRollNo", rl);
                                                editor.putLong("RollNo", rl);
                                            }
                                            editor.putString("utype", ut);
                                            editor.putBoolean("login", true);
                                            editor.putString("password", ps);
                                            editor.putString("username", un);
                                            editor.apply();
                                            intent.putExtra("docId", documentSnapshot.getId());
                                            intent.putExtra("iUserName", un);//edUsername.getText().toString());
                                            intent.putExtra("iPassword", ps);//,edPassword.getText().toString());
                                            intent.putExtra("iUtype", ut);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(Login.this, getString(R.string.login_toast_incorrect_credentials), Toast.LENGTH_SHORT).show();
                                            mProgress.dismiss();
                                        }
                                    } else {
                                        mProgress.dismiss();
                                        Toast.makeText(Login.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(Login.this, getString(R.string.login_toast_utype), Toast.LENGTH_SHORT).show();
                            spinnerUser.setFocusable(true);
                        }
                    } else {
                        Toast.makeText(Login.this, getString(R.string.login_toast_offline), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}