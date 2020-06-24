package com.example.hams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class wardenHome extends AppCompatActivity {

    private ImageView imgAtt,imgRep,imgMaintain,imgList,imgAcc,imgLog;
    private Intent intent;
    Long  lastRollNo=null;// to get last registered roll number
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String UserName;
    private long RollNo;
    private String Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warden_home);

        imgAtt=(ImageView)findViewById(R.id.imgAttend);
        imgRep=(ImageView)findViewById(R.id.imgReport);
        imgList=(ImageView)findViewById(R.id.imgList);
        imgLog=(ImageView)findViewById(R.id.imgLogout);
        imgAcc=(ImageView)findViewById(R.id.imgAccount);
        imgMaintain=(ImageView)findViewById(R.id.imgReg);


        intent=getIntent();
        UserName=intent.getStringExtra("iUserName");
        RollNo=intent.getLongExtra("iRollNo",0);
        Password=intent.getStringExtra("iPassword");

        db.collection("StudentLastRollNo").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        lastRollNo= document.getLong("lastRollNo");
                    }}}});
        //redirect to attendance page

        imgAtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(wardenHome.this,attendance.class);
                startActivity(intent);
            }
        });

        // //redirect to report  page
        imgRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(wardenHome.this,wardenReport.class);
                startActivity(intent);
            }
        });
        imgMaintain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(wardenHome.this,MaintainStudent.class);
                startActivity(intent);
            }
        });
        //  //redirect to Student list page
        imgList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(wardenHome.this,wardenSList.class);
                startActivity(intent);
            }
        });

        // //redirect to account manage page
        imgAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(wardenHome.this,wardenAccount.class);
                intent.putExtra("iUserName1",UserName);
                intent.putExtra("iPassword1",Password);
                startActivity(intent);
            }
        });

        //  logout performed
        imgLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(wardenHome.this);
                builder.setTitle("You Want to Logout ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences myPrefs = getSharedPreferences("MY",
                                MODE_PRIVATE);
                        SharedPreferences.Editor editor = myPrefs.edit();
                        editor.clear();
                        editor.commit();
                        AppState.getSingleInstance().setLoggingOut(true);
                        Intent intent = new Intent(wardenHome.this,
                                Login.class);
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
    //if user press back button after login it will not allowed to go back
    //it will ask for exit directly
    @Override
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