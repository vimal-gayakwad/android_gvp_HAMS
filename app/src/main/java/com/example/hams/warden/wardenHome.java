package com.example.hams.warden;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hams.R;
import com.example.hams.general.AppState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                intent = new Intent(wardenHome.this, wardenReport.class);
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

    public static class StudentList {
        private int mImageRes;
        private String string;

        public StudentList(int mImageRes, String string) {
            this.mImageRes = mImageRes;
            this.string = string;
        }

        public int getmImageRes() {
            return mImageRes;
        }

        public void setmImageRes(int mImageRes) {
            this.mImageRes = mImageRes;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public void setText(String text) {
            string = text;
        }
    }

    public static class StudentRegister extends AppCompatActivity {

        Calendar cal = Calendar.getInstance();
        private EditText sName, Address, Email, Contact, uname, password, birthdate, RollNo;
        private Button submit;
        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private Spinner dept, semeter;
        private String department;
        private ProgressDialog mProgress;
        private DatePickerDialog.OnDateSetListener mDatesetListener;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);
            // control Variables
            sName = findViewById(R.id.edsName);
            Address = findViewById(R.id.edAddress);
            submit = findViewById(R.id.btnSubmit);
            dept = findViewById(R.id.spDept);
            birthdate = findViewById(R.id.edBDate);
            Contact = findViewById(R.id.edContactNo);
            Email = findViewById(R.id.edEmail);
            uname = findViewById(R.id.edSUsername);
            password = findViewById(R.id.edSPassword);
            RollNo = findViewById(R.id.edRollNo);
            semeter = findViewById(R.id.spSem);

            birthdate.setText("dd-mm-yyyy");
            mProgress = new ProgressDialog(StudentRegister.this);
            mProgress.setTitle("Processing...");
            mProgress.setMessage("Please wait...");
            mProgress.setCancelable(false);
            mProgress.setIndeterminate(true);
            mProgress.show();
            birthdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(StudentRegister.this, android.R.style.Theme_Holo_Dialog_MinWidth, mDatesetListener, year, month, day);
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    datePickerDialog.show();

                }
            });
            mDatesetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                    cal.set(year, month, dayOfMonth);
                    String dateString = sdf.format(cal.getTime());
                    birthdate.setText(dateString);
                }
            };

            //        db.collection("StudentLastRollNo").document("LastRollNo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            //            @Override
            //            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            //                if (task.isSuccessful()) {
            //                    DocumentSnapshot documentSnapshot = task.getResult();
            //                    LastRollNo = documentSnapshot.getLong("RollNo");
            //                    RollNo.setText("" + LastRollNo);
            //                    mProgress.dismiss();
            //                } else {
            //                    mProgress.dismiss();
            //                    Toast.makeText(StudentRegister.this, "Error", Toast.LENGTH_SHORT).show();
            //                }
            //            }
            //        });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo CREATE STRING VARIABLE TO STORE EDITTEXT VALUE
                    if (TextUtils.isEmpty(RollNo.getText())) {
                        sName.setError(getString(R.string.register_setError_rollno));
                    }
                    if (TextUtils.isEmpty(sName.getText())) {
                        sName.setError(getString(R.string.register_serError_sname));
                    }
                    if (TextUtils.isEmpty(Address.getText())) {
                        Address.setError(getString(R.string.register_setError_address));
                    }
                    if (TextUtils.equals(birthdate.getText().toString(), "dd-mm-yyyy")) {
                        birthdate.setError(getString(R.string.register_setError_birthdate));
                    }
                    if (TextUtils.isEmpty(Contact.getText())) {
                        Contact.setError(getString(R.string.register_setError_contact));
                    }
                    if (TextUtils.isEmpty(Email.getText())) {
                        Email.setError(getString(R.string.register_setError_email));
                    }
                    if (TextUtils.isEmpty(uname.getText())) {
                        uname.setError(getString(R.string.register_setError_uname));
                    }
                    if (TextUtils.isEmpty(password.getText())) {
                        password.setError(getString(R.string.register_setError_password));
                    }
                    if (dept.getSelectedItem().equals("Choose")) {
                        Toast.makeText(StudentRegister.this, getString(R.string.register_toast_select_dept), Toast.LENGTH_SHORT).show();
                    }
                    if (semeter.getSelectedItem().equals("Choose")) {
                        Toast.makeText(StudentRegister.this, "", Toast.LENGTH_SHORT).show();
                    } else {
                        mProgress.show();
                        String sname = sName.getText().toString(),
                                rollNo = RollNo.getText().toString(),
                                address = Address.getText().toString(),
                                bdate = birthdate.getText().toString(),
                                cont = Contact.getText().toString(),
                                email = Email.getText().toString(),
                                Uname = uname.getText().toString(),
                                passwd = password.getText().toString(),
                                semestersp = semeter.getSelectedItem().toString();

                        department = dept.getSelectedItem().toString();
                        // Add studentDetails to studentDetails Collection
                        Map<String, Object> user = new HashMap<>();
                        user.put("StudentName", sname);
                        user.put("Address", address);
                        user.put("Birthdate", bdate);
                        user.put("Department", department);
                        user.put("Contact", cont);
                        user.put("Email", email);
                        user.put("RollNo", rollNo);
                        user.put("Semester", semestersp);
                        //Add Stdunet UserName And Password to student Table
                        final Map<String, Object> LoginData = new HashMap<>();
                        LoginData.put("RollNo", rollNo);
                        LoginData.put("username", Uname);
                        LoginData.put("password", passwd);
                        final Map<String, Object> AttData = new HashMap<>();
                        AttData.put("RollNo", rollNo);
                        AttData.put("username", Uname);
                        // Add a new document with a generated ID
                        db.collection("StudentDetails").document(Uname)
                                .set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //final Map<String, Long> LRollNo = new HashMap<>();
                                        //LRollNo.put("RollNo", LastRollNo + 1);
                                        //db.collection("StudentLastRollNo").document("LastRollNo").set(LRollNo);
                                        db.collection("student").document(uname.getText().toString()).set(LoginData);
                                        db.collection("attendanceList").document(RollNo.getText().toString()).set(AttData);

                                        mProgress.dismiss();
                                        Toast.makeText(StudentRegister.this, getString(R.string.register_toast_added_sucessfully), Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mProgress.dismiss();
                                        Toast.makeText(StudentRegister.this, getString(R.string.register_toast_added_failed) + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                        sName.setText("");
                        Address.setText("");
                        birthdate.setText("dd-mm-yyyy");
                        Contact.setText("");
                        Email.setText("");
                        uname.setText("");
                        password.setText("");
                        RollNo.setText((""));
                    }
                }
            });
        }
    }

    public static class StudentReport extends Activity {
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
                    "<tr ><th colspan='2'>" + getString(R.string.html_overall_absent) + "</th></tr><tr><th>" + getString(R.string.html_absent) + "</th><th>" + getString(R.string.html_absent_datetime) + "</th></tr>";
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
                                        "<tr><th>&nbsp&nbsp&nbsp</th><th>" + getString(R.string.html_morning) + "</th><th>" + getString(R.string.html_evening) + "</th><th>" + getString(R.string.html_overall) + "</th></tr>" +
                                        "<tr><th>" + getString(R.string.html_total_working_time) + "</th><th>" + totalM + "</th><th>" + totalE + "<th rowspan='4'>" + percentage + "%</th></tr>" +
                                        "<tr><th>" + getString(R.string.html_present) + "</th><th>" + MPresent + "</th><th>" + EPresent + "</tr>" +
                                        "<tr><th>" + getString(R.string.html_absent) + "</th><th>" + MAbsent + "</th><th>" + EAbsent + "</tr>" +
                                        "<tr><th>" + getString(R.string.html_percentage) + "</th><th>" + mpercent + "%</th><th>" + epercent + "%</tr>" +
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

    public static class updateStudent extends AppCompatActivity {

        private EditText sName, Address, Email, Contact, uname, password, birthdate, RollNo;
        private Button submit;
        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private Spinner dept;
        private String department;
        private Long LastRollNo, iRoll;
        private DatePickerDialog.OnDateSetListener mDatesetListener;
        private Calendar cal = Calendar.getInstance();
        private Intent intent;
        private ProgressDialog mProgress;
        private Map<String, String> map = new HashMap<>();

        //progress Dialog for login process
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_update_student);
            // control Variables
            sName = findViewById(R.id.edsNameUP);
            Address = findViewById(R.id.edAddressUP);
            submit = findViewById(R.id.btnSubmitUP);
            dept = findViewById(R.id.spDeptUP);
            birthdate = findViewById(R.id.edBDateUP);
            Contact = findViewById(R.id.edContactNoUP);
            Email = findViewById(R.id.edEmailUP);
            uname = findViewById(R.id.edSUsernameUP);
            password = findViewById(R.id.edSPasswordUP);
            RollNo = findViewById(R.id.edRollNoUP1);

            mProgress = new ProgressDialog(updateStudent.this);
            mProgress.setTitle("Processing...");
            mProgress.setMessage("Please wait...");
            mProgress.setCancelable(false);
            mProgress.setIndeterminate(true);
            intent = getIntent();
            iRoll = Long.valueOf(intent.getStringExtra("rollNo"));
            RollNo.setEnabled(false);
            birthdate.setText("dd-mm-yyyy");
            mProgress.show();
            db.collection("StudentDetails")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (Long.valueOf(document.getString("RollNo")).equals(iRoll)) { //&& document.getId().contains("morning") && document.getString("" + rollno.get(i)).contains("a")) {
                                        map.put("sname", document.getString("StudentName"));
                                        map.put("Address", document.getString("Address"));
                                        map.put("Contact", document.getString("Contact"));
                                        map.put("Email", document.getString("Email"));
                                        map.put("Birthdate", document.getString("Birthdate"));
                                        map.put("Department", document.getString("Department"));
                                        map.put("RollNo", "" + Long.parseLong(document.getString("RollNo")));
                                        map.put("uid", document.getId());
                                        mProgress.dismiss();
                                        break;
                                    }
                                }
                            } else {
                                mProgress.dismiss();
                            }
                            sName.setText(map.get("sname"));
                            RollNo.setText(map.get("RollNo"));
                            Address.setText(map.get("Address"));
                            dept.setSelection(0);
                            Contact.setText(map.get("Contact"));
                            Email.setText(map.get("Email"));
                            birthdate.setText(map.get("Birthdate"));
                            uname.setText(map.get("uid"));
                        }
                    });
            birthdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(updateStudent.this, android.R.style.Theme_Holo_Dialog_MinWidth, mDatesetListener, year, month, day);
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    datePickerDialog.show();
                }
            });
            mDatesetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                    cal.set(year, month, dayOfMonth);
                    String dateString = sdf.format(cal.getTime());
                    birthdate.setText(dateString);
                }
            };
            db.collection("StudentLastRollNo").document("LastRollNo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        LastRollNo = documentSnapshot.getLong("RollNo");
                        RollNo.setText("" + LastRollNo);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // todo UPLOAD DATA TO FIREBASE
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo CREATE STRING VARIABLE TO STORE EDITTEXT VALUE
                    if (TextUtils.isEmpty(RollNo.getText())) {
                        sName.setError("RollNo Required");
                    }
                    if (TextUtils.isEmpty(sName.getText())) {
                        sName.setError("StudentName Required");
                    }
                    if (TextUtils.isEmpty(Address.getText())) {
                        Address.setError("Address Required");
                    }
                    if (TextUtils.equals(birthdate.getText().toString(), "dd-mm-yyyy")) {
                        birthdate.setError("Select Birthdate");
                    }
                    if (TextUtils.isEmpty(Contact.getText())) {
                        Contact.setError("Contact Number Requiered");
                    }
                    if (TextUtils.isEmpty(Email.getText())) {
                        Email.setError("Email required");
                    }
                    if (TextUtils.isEmpty(uname.getText())) {
                        uname.setError("StudentName Required");
                    }
                    if (TextUtils.isEmpty(password.getText())) {
                        password.setError("Password Required");
                    }
                    if (dept.getSelectedItem().equals("-select Department-")) {
                        Toast.makeText(getApplicationContext(), "Select Department", Toast.LENGTH_SHORT).show();
                    } else {
                        mProgress.show();
                        String sname = sName.getText().toString(),
                                rollNo = RollNo.getText().toString(),
                                address = Address.getText().toString(),
                                bdate = birthdate.getText().toString(),
                                cont = Contact.getText().toString(),
                                email = Email.getText().toString(),
                                Uname = uname.getText().toString(),
                                passwd = password.getText().toString();
                        department = dept.getSelectedItem().toString();
                        // Add studentDetails to studentDetails Collecction
                        Map<String, Object> user = new HashMap<>();
                        user.put("StudentName", sname);
                        user.put("Address", address);
                        user.put("Birthdate", bdate);
                        user.put("Department", department);
                        user.put("Contact", cont);
                        user.put("Email", email);
                        user.put("RollNo", rollNo);
                        //Add Stdunet UserName And Password to student Table
                        final Map<String, Object> LoginData = new HashMap<>();
                        LoginData.put("RollNo", rollNo);
                        LoginData.put("username", Uname);
                        LoginData.put("password", passwd);
                        final Map<String, Object> AttData = new HashMap<>();
                        AttData.put("RollNo", rollNo);
                        AttData.put("username", Uname);

                        db.collection("StudentDetails").document(Uname)
                                .update(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mProgress.dismiss();
                                        Toast.makeText(getApplicationContext(), "Error While Updating " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                        mProgress.dismiss();
                        db.collection("student").document(uname.getText().toString()).update(LoginData);
                        //Update Roll Number After SuccessFully Added
                        sName.setText("");
                        Address.setText("");
                        birthdate.setText("dd-mm-yyyy");
                        Contact.setText("");
                        Email.setText("");
                        uname.setText("");
                        password.setText("");
                        RollNo.setText("");
                    }
                }
            });
        }
    }

    public static class NewWarden extends AppCompatActivity {

        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private EditText adminUname, adminPass;
        private Map<String, Object> user;
        private Button btnSubmit;
        private ProgressDialog mProgress;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_new_warden);
            adminUname = findViewById(R.id.edWUsername);
            adminPass = findViewById(R.id.edWPassword);
            btnSubmit = findViewById(R.id.btNewWarden);

            mProgress = new ProgressDialog(NewWarden.this);
            mProgress.setTitle("Processing...");
            mProgress.setMessage("Please wait...");
            mProgress.setCancelable(false);
            mProgress.setIndeterminate(true);


            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    user = new HashMap<>();
                    user.put("username", adminUname.getText().toString());
                    user.put("password", adminPass.getText().toString());
                    if (TextUtils.isEmpty(adminUname.getText())) {
                        adminUname.setError("User Name Required");
                    }
                    if (TextUtils.isEmpty(adminPass.getText())) {
                        adminPass.setError("Password Must Required");
                    } else if ((!TextUtils.isEmpty(adminUname.getText())) && (!TextUtils.isEmpty(adminPass.getText()))) {
                        mProgress.show();
                        db.collection("warden").document(adminUname.getText().toString())
                                .set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(NewWarden.this, "Warden Added SuccessFully", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(NewWarden.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
            });
        }
    }

    public static class MaintainStudent extends AppCompatActivity {
        private Button reg, update, del, newSem;
        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private Intent intent;
        private EditText rollnum;
        private ProgressDialog mProgress;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maintain);
            reg = findViewById(R.id.btnStuRegister);
            update = findViewById(R.id.btnStuUpdate);
            del = findViewById(R.id.btnstuDel);
            rollnum = findViewById(R.id.edRollNoToMaintain);
            newSem = findViewById(R.id.btnstuPromot);
            mProgress = new ProgressDialog(MaintainStudent.this);
            mProgress.setTitle("Processing...");
            mProgress.setMessage("Please wait...");
            mProgress.setCancelable(false);
            mProgress.setIndeterminate(true);
            reg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), StudentRegister.class));
                }
            });
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(rollnum.getText())) {
                        rollnum.setError("Enter Roll Number");
                    } else if (!TextUtils.isEmpty(rollnum.getText())) {
                        intent = new Intent(MaintainStudent.this, updateStudent.class);
                        intent.putExtra("rollNo", rollnum.getText().toString());
                        startActivity(intent);
                    }
                }
            });
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(rollnum.getText())) {
                        rollnum.setError("Roll No Must Required");
                    } else if ((!TextUtils.isEmpty(rollnum.getText()))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MaintainStudent.this);
                        builder.setTitle(getString(R.string.student_maintain_delete));
                        builder.setPositiveButton(getString(R.string.exit_positive), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                db.collection("StudentDetails")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        if (document.getString("RollNo").contains("" + rollnum.getText().toString())) {
                                                            mProgress.show();
                                                            Toast.makeText(MaintainStudent.this, "" + document.getId(), Toast.LENGTH_SHORT).show();
                                                            db.collection("StudentDetails").document(document.getId()).delete();
                                                            db.collection("student").document(document.getId()).delete();
                                                            Toast.makeText(MaintainStudent.this, "Student Details Deleted", Toast.LENGTH_SHORT).show();
                                                            mProgress.dismiss();
                                                        } else {
                                                            Toast.makeText(MaintainStudent.this, "No Student Found", Toast.LENGTH_SHORT).show();
                                                            mProgress.dismiss();
                                                        }
                                                    }
                                                } else {
                                                    Toast.makeText(MaintainStudent.this, "No Record Found", Toast.LENGTH_SHORT).show();
                                                    mProgress.dismiss();
                                                }
                                            }
                                        });
                            }

                        });
                        builder.setNegativeButton(getString(R.string.exit_negative), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        builder.show();
                    }
                }

            });
            newSem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.collection("StudentDetails")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (document.getString("RollNo").contains("" + rollnum.getText().toString())) {
                                                mProgress.show();

                                                mProgress.dismiss();
                                            } else {
                                                Toast.makeText(MaintainStudent.this, "No Student Found", Toast.LENGTH_SHORT).show();
                                                mProgress.dismiss();
                                            }
                                        }
                                    } else {
                                        Toast.makeText(MaintainStudent.this, "No Record Found", Toast.LENGTH_SHORT).show();
                                        mProgress.dismiss();
                                    }
                                }
                            });
                }
            });
        }
    }
}