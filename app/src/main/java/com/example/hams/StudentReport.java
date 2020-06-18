package com.example.hams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

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

    private TextView textView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> list = new ArrayList<>();
    private Map<String,String> map=new HashMap<>();
    private Map<String ,String> ABSDate=new HashMap<>();
    private WebView webView,webViewReport;
    private   String header="",body="",footer="",reportData="";
    private long roll=0;
    int totalM=0,totalE=0;
    int totalTime=0;
    private Intent intent;
    private ProgressDialog mProgress;
    private Map<String,String> MonthList = new HashMap();
    private TextView months;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_report);

        //Initilization
        textView=(TextView)findViewById(R.id.txtTitleReport);
        webView=(WebView)findViewById(R.id.webviewAbs);
        webViewReport=(WebView)findViewById(R.id.webViewReport);
        months=(TextView)findViewById(R.id.txtMonth);

        mProgress = new ProgressDialog(StudentReport.this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        //mProgress.show();

    intent=getIntent();
    roll=intent.getLongExtra("iRollNo1",0);

        //roll=uc.getRollNo();

        header="<html><body><center><table border='1'" +
                    "padding='10'  style='border-color:blue;font-family: arial, sans-serif;border-collapse: collapse;width: 100%; text-align:center'>" +
                    "<tr ><th colspan='2'>Overall Absents</th></tr><tr><th>Absents</th><th>Absent DateTime</tr></th>";
        footer="</table></center></body></html>";
        textView.setText("Student Report");
                db.collection("attendance")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {
                                    for(QueryDocumentSnapshot document : task.getResult()){
                                        if(document.getId().contains("Jan")){ MonthList.put("1","Jan"); }
                                        if(document.getId().contains("Feb")){ MonthList.put("2","Feb"); }
                                        if(document.getId().contains("Mar")){ MonthList.put("3","Mar"); }
                                        if(document.getId().contains("Apr")){ MonthList.put("4","Apr"); }
                                        if(document.getId().contains("May")){ MonthList.put("5","May"); }
                                        if(document.getId().contains("Jun")){ MonthList.put("6","Jun"); }
                                        if(document.getId().contains("Jul")){ MonthList.put("7","Jul"); }
                                        if(document.getId().contains("Aug")){ MonthList.put("8","Aug"); }
                                        if(document.getId().contains("Sep")){ MonthList.put("9","Sep"); }
                                        if(document.getId().contains("Oct")){ MonthList.put("10","Oct"); }
                                        if(document.getId().contains("Nov")){ MonthList.put("11","Nov"); }
                                        if(document.getId().contains("Dec")){ MonthList.put("12","Dec"); }
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
                                            body+="<tr><td>"+count+"</td><td>"+document.getId()+"</td></tr>";
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
                                            body+="<tr><td>"+count+"</td><td>"+document.getId()+"</td></tr>";
                                            ABSDate.put("\n" + count, " " + document.getId());
                                            EAbsent++;
                                            totalE++;
                                        }
                                        totalTime++;
                                    }
                                     int totalpresent=MPresent+EPresent;
                                    float percentage = 0;
                                    float mpercent = 0;
                                    float epercent = 0;
                                    if((totalE==0)||(totalTime==0)||(totalM==0)){
                                        Toast.makeText(StudentReport.this, "No Data Found", Toast.LENGTH_SHORT).show();
                                    
                                    }
try {
     percentage = ((totalpresent * 100) / totalTime);
     mpercent = ((MPresent * 100) / totalM);
     epercent = ((EPresent * 100) / totalE);
}catch (Exception e){

}


                                    reportData="<html>" +
                                            "<body>" +
                                            "<table border='1' style='border-color:blue;font-family: arial, sans-serif;border-collapse: collapse;width: 100%; text-align:center'>" +
                                            "<tr><th>&nbsp&nbsp&nbsp</th><th>Morning</th><th>Evening</th><th>Overall</th></tr>" +
                                            "<tr><th>Total Working Time</th><th>"+totalM+"</th><th>"+totalE+"<th rowspan='4'>"+percentage+"%</th></tr>" +
                                            "<tr><th>Present</th><th>"+MPresent+"</th><th>"+EPresent+"</tr>" +
                                            "<tr><th>Absent</th><th>"+MAbsent+"</th><th>"+EAbsent+"</tr>" +
                                            "<tr><th>Percentage</th><th>"+mpercent+"%</th><th>"+epercent+"%</tr>" +
                                            "</table>" +
                                            "</body>" +
                                            "</html>";
                                    months.setText("    Including : "+MonthList.values());
                                    webViewReport.loadData(reportData,"text/html; charset=utf-8","UTF-8");
                                    if (ABSDate.size() == 0) {

                                    } else {

                                        webView.loadData(header+body+footer, "text/html; charset=utf-8", "UTF-8");
                                        mProgress.dismiss();
                                    }
                                }
                                else{
                                    Toast.makeText(StudentReport.this, "No Data Found", Toast.LENGTH_SHORT).show();
                                }
                            }
        });
    }


}