package com.example.hams;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class studentHome extends AppCompatActivity {
private ImageView imgAtt,imgAccount,imgLogout;
private Intent intent;
Long RollNo;
String UserName,Password,docId;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        imgAtt=(ImageView)findViewById(R.id.imgAttend);
        imgAccount=(ImageView)findViewById(R.id.imgAccount);
        imgLogout=(ImageView)findViewById(R.id.imgLogout);

        intent=getIntent();
        UserName=intent.getStringExtra("iUserName");
        RollNo=intent.getLongExtra("iRollNo",0);
        Password=intent.getStringExtra("iPassword");
        docId=intent.getStringExtra("");
        imgAtt.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            intent=new Intent(getApplicationContext(), StudentReport.class);
            intent.putExtra("iRollNo1",RollNo);
            startActivity(intent);

        }
    });
    imgAccount.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            intent = new Intent(getApplicationContext(),studentAccount.class);
            intent.putExtra("iUserName1",UserName);
            intent.putExtra("iPassword1",Password);
            intent.putExtra("iUname1",docId);
           startActivity(intent);
        }
    });
        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(studentHome.this);

                builder.setTitle("You Want to Logout ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences myPrefs = getSharedPreferences("MY",
                                MODE_PRIVATE);
                        SharedPreferences.Editor editor = myPrefs.edit();
                        editor.clear();
                        editor.commit();
                        AppState.getSingleInstance().setLoggingOut(true);
                        Intent intent = new Intent(studentHome.this,
                                MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.show();
            }
        });

    }
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);        builder.setTitle("Are You Sure Want to Exit ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                moveTaskToBack(true);
                System.exit(1);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.show();
    }
}