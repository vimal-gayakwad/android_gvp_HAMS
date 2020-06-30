package com.example.hams.warden;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.hams.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class wardenSList extends Activity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Long> rollno = new ArrayList<Long>();
    private WebView webView;
    private String header = "", body = "", footer = "";
    private TextView title;
    private ProgressDialog mProgress;
    private Button printPDF1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_report);

        //Initilization
        title = findViewById(R.id.txtTitleReport);
        printPDF1 = findViewById(R.id.btnPrint);
        webView = findViewById(R.id.webviewAbs);
        webView.getSettings().setBuiltInZoomControls(true);
        //initilize progress dialog
        mProgress = new ProgressDialog(wardenSList.this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        printPDF1.setVisibility(View.VISIBLE);
       // mProgress.show();
        header = "<html><body><center><table border='1'" +
                "padding='10'  style='border-color:blue;font-family: arial, sans-serif;border-collapse: collapse;width: 100%; text-align:center'>" +
                "<tr><th>Sr.No</th><th>RollNo</th><th>StudentName</th><th>Address</th><th>Email</th><th>Contact</th><th>Department</th><th></tr>";
        footer = "</table></center></body></html>";
        title.setText("Student List");
        db.collection("attendanceList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                count++;
                                rollno.add(Long.valueOf(document.getId()));
                            }
                        }
                        Collections.sort(rollno);
                        db.collection("StudentDetails")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            long RollNo=1;
                                                    String Sname="",Address="",Contact="",Email="",Department="";
                                            int i;
                                            for ( i = 0; i < rollno.size(); i++) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (Long.valueOf(document.getString("RollNo")) == rollno.get(i)) { //&& document.getId().contains("morning") && document.getString("" + rollno.get(i)).contains("a")) {
                                                        Sname = document.getString("StudentName");
                                                        Address = document.getString("Address");
                                                        Contact = document.getString("Contact");
                                                        Email = document.getString("Email");
                                                        Department = document.getString("Department");
                                                        RollNo = Long.parseLong(document.getString("RollNo"));
                                                        body += "<tr><td>" + (i + 1) + "</td><td>" + rollno.get(i) + "</td><td>" + Sname + "</td><td>" + Address + "</td><td>" + Contact + "</td><td>" + Email + "</td><td>" + Department + "</td></tr>";
                                                    }
                                                }
                                            }
                                            }
                                            webView.loadData(header + body + footer, "text/html; charset=utf-8", "UTF-8");
                                            mProgress.dismiss();
                                        }
                                });
                    }
                });
        printPDF1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintManager printManager=(PrintManager)wardenSList.this.getSystemService(Context.PRINT_SERVICE);
                String jobName=getString(R.string.app_name)+"Document";
                PrintDocumentAdapter printDocumentAdapter=webView.createPrintDocumentAdapter(jobName);
                PrintJob printJob=printManager.print(jobName,printDocumentAdapter,new PrintAttributes.Builder().build());
            }
        });
    }
}