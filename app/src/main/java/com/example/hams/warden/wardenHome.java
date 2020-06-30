package com.example.hams.warden;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hams.R;
import com.example.hams.general.AppState;
import com.example.hams.general.MaintainStudent;
import com.google.firebase.firestore.FirebaseFirestore;

public class wardenHome extends AppCompatActivity {

    private ImageView imgAtt, imgRep, imgMaintain, imgList, imgAcc, imgLog;
    private Intent intent;
    private Long lastRollNo = null;// to get last registered roll number
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String UserName;
    private String Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warden_home);

        imgAtt = findViewById(R.id.imgAttend);
        imgRep = findViewById(R.id.imgReport);
        imgList = findViewById(R.id.imgList);
        imgLog = findViewById(R.id.imgLogout);
        imgAcc = findViewById(R.id.imgAccount);
        imgMaintain = findViewById(R.id.imgReg);
        intent = getIntent();
        UserName = intent.getStringExtra("iUserName");
        Password = intent.getStringExtra("iPassword");

        imgAtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(wardenHome.this, attendance.class);
                startActivity(intent);
            }
        });
        // //redirect to report  page
        imgRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(wardenHome.this, getContext.class);
                startActivity(intent);
            }
        });
        imgMaintain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(wardenHome.this, MaintainStudent.class);
                startActivity(intent);
            }
        });
        //  //redirect to Student list page
        imgList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(wardenHome.this, wardenSList.class);
                startActivity(intent);
            }
        });
        // //redirect to account manage page
        imgAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(wardenHome.this, wardenAccount.class);
                intent.putExtra("iUserName1", UserName);
                intent.putExtra("iPassword1", Password);
                startActivity(intent);
            }
        });
        //  logout performed
        imgLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(wardenHome.this);
                builder.setTitle(getString(R.string.logout_title));
                builder.setPositiveButton(getString(R.string.logout_positive), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences myPrefs = getSharedPreferences("MyPref",
                                MODE_PRIVATE);
                        SharedPreferences.Editor editor = myPrefs.edit();
                        editor.putBoolean("login", false);
                        editor.apply();
                        finish();
                        AppState.getSingleInstance().setLoggingOut(true);
                        Intent intent = new Intent(wardenHome.this,
                                AppState.Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton(getString(R.string.logout_negative), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.exit_title));
        builder.setPositiveButton(getString(R.string.exit_positive), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                moveTaskToBack(true);
                System.exit(1);
            }
        });
        builder.setNegativeButton(getString(R.string.exit_negative), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.show();
    }
}