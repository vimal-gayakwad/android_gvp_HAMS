package com.example.hams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentReport extends Activity {
    ///Variable Declaration

    private TextView textView, months;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> list = new ArrayList<>();
    private Map<String, String> ABSDate = new HashMap<>();
    private WebView webView, webViewReport;
    private String header = "", body = "", footer = "", reportData = "";
    private long roll = 0;
    private int totalM = 0, totalE = 0, totalTime = 0;
    private Intent intent;
    private ProgressDialog mProgress;
    private Map<String, String> MonthList = new HashMap();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_report);
        //Initilization
        textView = findViewById(R.id.txtTitleReport);
        webView = findViewById(R.id.webviewAbs);
        webViewReport = findViewById(R.id.webViewReport);
        months = findViewById(R.id.txtMonth);

        mProgress = new ProgressDialog(StudentReport.this);
        mProgress.setTitle(getString(R.string.mprogress_title));
        mProgress.setMessage(getString(R.string.mprogress_msg));
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        //mProgress.show();

        intent = getIntent();
        roll = intent.getLongExtra("iRollNo1", 0);

        //roll=uc.getRollNo();

        header = "<html><body><center><table border='1'" +
                "padding='10'  style='border-color:blue;font-family: arial, sans-serif;border-collapse: collapse;width: 100%; text-align:center'>" +
                "<tr ><th colspan='2'>"+getString(R.string.html_overall_absent)+"</th></tr><tr><th>"+getString(R.string.html_absent)+"</th><th>"+getString(R.string.html_absent_datetime)+"</th></tr>";
        footer = "</table></center></body></html>";
        textView.setText(getString(R.string.student_report_title));
        db.collection("attendance")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
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

                            int MPresent = 0, MAbsent = 0, EPresent = 0, EAbsent = 0;
                            int count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(document.getId());
                                if (document.contains("" + roll) && document.getId().contains("morning") && document.getString("" + roll).contains("p")) {
                                    MPresent++;
                                    totalM++;
                                }
                                if (document.contains("" + roll) && document.getId().contains("morning") && document.getString("" + roll).contains("a")) {
                                    count++;
                                    body += "<tr><td>" + count + "</td><td>" + document.getId() + "</td></tr>";
                                    ABSDate.put("\n" + count, " " + document.getId());
                                    MAbsent++;
                                    totalM++;
                                }
                                if (document.contains("" + roll) && document.getId().contains("evening") && document.getString("" + roll).contains("p")) {
                                    EPresent++;
                                    totalE++;
                                }
                                if (document.contains("" + roll) && document.getId().contains("evening") && document.getString("" + roll).contains("a")) {
                                    count++;
                                    body += "<tr><td>" + count + "</td><td>" + document.getId() + "</td></tr>";
                                    ABSDate.put("\n" + count, " " + document.getId());
                                    EAbsent++;
                                    totalE++;
                                }
                                totalTime++;
                            }
                            int totalpresent = MPresent + EPresent;
                            float percentage = 0;
                            float mpercent = 0;
                            float epercent = 0;
                            if ((totalE == 0) || (totalTime == 0) || (totalM == 0)) {
                                Toast.makeText(StudentReport.this, getString(R.string.student_report_toast_nodata), Toast.LENGTH_SHORT).show();

                            }
                            try {
                                percentage = ((totalpresent * 100) / totalTime);
                                mpercent = ((MPresent * 100) / totalM);
                                epercent = ((EPresent * 100) / totalE);
                            } catch (Exception e) {

                            }


                            reportData = "<html>" +
                                    "<body>" +
                                    "<table border='1' style='border-color:blue;font-family: arial, sans-serif;border-collapse: collapse;width: 100%; text-align:center'>" +
                                    "<tr><th>&nbsp&nbsp&nbsp</th><th>"+getString(R.string.html_morning)+"</th><th>"+getString(R.string.html_evening)+"</th><th>"+getString(R.string.html_overall)+"</th></tr>" +
                                    "<tr><th>"+getString(R.string.html_total_working_time)+"</th><th>" + totalM + "</th><th>" + totalE + "<th rowspan='4'>" + percentage + "%</th></tr>" +
                                    "<tr><th>"+getString(R.string.html_present)+"</th><th>" + MPresent + "</th><th>" + EPresent + "</tr>" +
                                    "<tr><th>"+getString(R.string.html_absent)+"</th><th>" + MAbsent + "</th><th>" + EAbsent + "</tr>" +
                                    "<tr><th>"+getString(R.string.html_percentage)+"</th><th>" + mpercent + "%</th><th>" + epercent + "%</tr>" +
                                    "</table>" +
                                    "</body>" +
                                    "</html>";
                            months.setText(getString(R.string.report_including) + MonthList.values());
                            webViewReport.loadData(reportData, "text/html; charset=utf-8", "UTF-8");
                            if (ABSDate.size() == 0) {
                            } else {
                                webView.loadData(header + body + footer, "text/html; charset=utf-8", "UTF-8");
                                mProgress.dismiss();
                            }
                        } else {
                            Toast.makeText(StudentReport.this, getString(R.string.student_report_toast_nodata), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}