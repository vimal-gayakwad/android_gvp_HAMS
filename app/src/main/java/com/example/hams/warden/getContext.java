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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.hams.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class getContext extends Activity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Long> rollno = new ArrayList<>();
    private Map<String, String> MonthList = new HashMap();
    private WebView webView;
    private String header = "", body = "", footer = "";
    private TextView title, months;
    private ProgressDialog mProgress;
    private Button printPDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_report);

        title =  findViewById(R.id.txtTitleReport);
        months =  findViewById(R.id.txtMonth);
        webView =  findViewById(R.id.webviewAbs);
        printPDF = findViewById(R.id.btnPrint);
        printPDF.setVisibility(View.VISIBLE);
        mProgress = new ProgressDialog(getContext.this);
        mProgress.setTitle(getString(R.string.mprogress_title));
        mProgress.setMessage(getString(R.string.mprogress_msg));
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        mProgress.show();
        header = "<html><body><center><table border='1'" +
                "padding='10'  style='border-color:blue;font-family: arial, sans-serif;border-collapse: collapse;width: 100%; text-align:center'>" +
                "<tr><th>Sr.No</th><th>"+getString(R.string.html_rollno)+"</th><th>"+getString(R.string.html_morning)+"</th><th>"+getString(R.string.html_evening)+"</th></tr>";
        footer = "</table></center>" +
                "</body></html>";
        title.setText(getString(R.string.student_report));
        db.collection("attendanceList").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                rollno.add(Long.valueOf(document.getId()));
                            }
                        } else {
                            Toast.makeText(getContext.this, "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        Collections.sort(rollno);
                        db.collection("attendance")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                if (document.getId().contains("Jan")) {
                                                    MonthList.put("1", "Jan");
                                                }
                                                if (document.getId().contains("Feb")) {
                                                    MonthList.put("2", "Feb");
                                                }
                                                if (document.getId().contains("Mar")) {
                                                    MonthList.put("3", "Mar");
                                                }
                                                if (document.getId().contains("Apr")) {
                                                    MonthList.put("4", "Apr");
                                                }
                                                if (document.getId().contains("May")) {
                                                    MonthList.put("5", "May");
                                                }
                                                if (document.getId().contains("Jun")) {
                                                    MonthList.put("6", "Jun");
                                                }
                                                if (document.getId().contains("Jul")) {
                                                    MonthList.put("7", "Jul");
                                                }
                                                if (document.getId().contains("Aug")) {
                                                    MonthList.put("8", "Aug");
                                                }
                                                if (document.getId().contains("Sep")) {
                                                    MonthList.put("9", "Sep");
                                                }
                                                if (document.getId().contains("Oct")) {
                                                    MonthList.put("10", "Oct");
                                                }
                                                if (document.getId().contains("Nov")) {
                                                    MonthList.put("11", "Nov");
                                                }
                                                if (document.getId().contains("Dec")) {
                                                    MonthList.put("12", "Dec");
                                                }
                                            }
                                            int MAbsent = 0, EAbsent = 1;
                                            for (int i = 0; i < rollno.size(); i++) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (document.contains("" + rollno.get(i)) && document.getId().contains("morning") && Objects.requireNonNull(document.getString("" + rollno.get(i))).contains("a")) {
                                                        MAbsent++;
                                                    }
                                                    if (document.contains("" + rollno.get(i)) && document.getId().contains("evening") && Objects.requireNonNull(document.getString("" + rollno.get(i))).contains("a")) {
                                                        EAbsent++;
                                                    }
                                                }
                                                body += "<tr><td>" + (i + 1) + "</td> <td>" + rollno.get(i) + "</td><td>" + MAbsent + "</td><td>" + EAbsent + "</td></tr>";
                                                MAbsent = 0;
                                                EAbsent = 0;
                                            }
                                            months.setText("    Including : " + MonthList.values());
                                            webView.loadData(header + body + footer, "text/html; charset=utf-8", "UTF-8");
                                            mProgress.dismiss();
                                        }
                                    }
                                });
                    }
                });
        printPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintManager printManager = (PrintManager) getContext.this.getSystemService(Context.PRINT_SERVICE);
                String jobName = getString(R.string.app_name) + "Document";
                PrintDocumentAdapter printDocumentAdapter = webView.createPrintDocumentAdapter(jobName);
                if (printManager != null) {
                    PrintJob printJob = printManager.print(jobName, printDocumentAdapter, new PrintAttributes.Builder().build());
                }
            }
        });
    }
}