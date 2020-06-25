package com.example.hams;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class studentHome extends AppCompatActivity {
    private ImageView imgAtt, imgAccount, imgLogout;
    private Intent intent;
    Long RollNo;
    String UserName, Password, docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        imgAtt = (ImageView) findViewById(R.id.imgAttend);
        imgAccount = (ImageView) findViewById(R.id.imgAccount);
        imgLogout = (ImageView) findViewById(R.id.imgLogout);

        intent = getIntent();
        UserName = intent.getStringExtra("iUserName");
        RollNo = intent.getLongExtra("iRollNo", 0);
        Password = intent.getStringExtra("iPassword");
        docId = intent.getStringExtra("");
        imgAtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), StudentReport.class);
                intent.putExtra("iRollNo1", RollNo);
                startActivity(intent);

            }
        });
        imgAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), studentAccount.class);
                intent.putExtra("iUserName1", UserName);
                intent.putExtra("iPassword1", Password);
                intent.putExtra("iUname1", docId);
                startActivity(intent);
            }
        });
        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(studentHome.this);

                builder.setTitle(getString(R.string.logout_title));
                builder.setPositiveButton(getString(R.string.logout_positive), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences myPrefs = getSharedPreferences("MY",
                                MODE_PRIVATE);
                        SharedPreferences.Editor editor = myPrefs.edit();
                        editor.clear();
                        editor.apply();
                        AppState.getSingleInstance().setLoggingOut(true);
                        Intent intent = new Intent(studentHome.this,
                                Login.class);
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