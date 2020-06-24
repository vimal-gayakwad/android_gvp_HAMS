package com.example.hams;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.lang.String.valueOf;

@RequiresApi(api = Build.VERSION_CODES.N)
public class attendance extends AppCompatActivity {

    private CheckBox checkBox;
    private int numOfStu = 0;
    private TextView txtPre, txtAbs, txtTot;
    private EditText edDate;
    Button submit;
    String p;
    String attTime;// attendance time
    int crTime;
    GridLayout grd;
    ////////////////////////
    ArrayList<String> keyList;
    HashMap<String, String> meMap = new HashMap<String, String>();
    Map<String, Object> user = new HashMap<>();
    ///////////////////////////////////////////////
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog mProgress;
    private RadioGroup radioGroup;
    private RadioButton rdMorning, rdEvening;
    final Calendar myCalendar = Calendar.getInstance();
    /////////////////////////////////////////////////
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    ///////////////////////////////////////
    private void updateLabel() {
        String myFormat = "dd-MMM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edDate.setText(sdf.format(myCalendar.getTime()));
    }

    ///////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        txtPre = (TextView) findViewById(R.id.txtPresent);
        txtAbs = (TextView) findViewById(R.id.txtAbsent);
        txtTot = (TextView) findViewById(R.id.txtTotal);
        submit = (Button) findViewById(R.id.bt2);
        edDate = (EditText) findViewById(R.id.edDate);
        radioGroup = (RadioGroup) findViewById(R.id.prTime);
        rdEvening = (RadioButton) findViewById(R.id.rdEvening);
        rdMorning = (RadioButton) findViewById(R.id.rdMorning);

        grd = (GridLayout) findViewById(R.id.grd);
        grd.setColumnCount(getColumn());

        //////////////////////////////////////////////////////////
        Date c = Calendar.getInstance().getTime();
        crTime = Calendar.HOUR_OF_DAY;

        if (crTime <= 12) {
            rdMorning.setChecked(true);
        } else {
            rdEvening.setChecked(true);
        }

        //////////////////////////////////
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        edDate.setText(formattedDate);
        ////////////////////////////////////////
        mProgress = new ProgressDialog(attendance.this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        submit.setText("Submit");
        /////////////////////////////////////////////////
        edDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(attendance.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        mProgress.show();
        ///////////////////////////////////
        db.collection("attendanceList").orderBy("RollNo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = (String) document.get("Name");
                                long id = Long.valueOf(document.getString("RollNo"));
                                user.put("" + id, "a");
                                checkBox = new CheckBox(attendance.this);
                                checkBox.setId(Integer.parseInt("" + id));
                                checkBox.setText(("" + id));
                                checkBox.setTextSize(20);
                                checkBox.setOnClickListener(getOnClickDoSomething(checkBox));
                                checkBox.setPadding(20, 20, 20, 20);
                                checkBox.setShadowLayer(1, 1, 1, 1);
                                grd.addView(checkBox);
                                numOfStu++;
                                mProgress.dismiss();
                            }
                        } else {
                            mProgress.dismiss();
                            Toast.makeText(attendance.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }

                        txtTot.setText(valueOf(numOfStu));
                    }
                });
/////////////////////////////////////////////////////////////

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.show();
                if (rdMorning.isChecked()) {
                    attTime = "morning";
                } else {
                    attTime = "evening";
                }
                keyList = new ArrayList<>(meMap.keySet());
                ArrayList<String> valueList = new ArrayList<>(meMap.values());
                for (int i = 0; i < keyList.size(); i++) {
                    user.put(keyList.get(i), "p");
                }
                saveInfo();
            }
        });
    }

    private int getColumn() {
        int num = 0;
        int height, width;

        width = Resources.getSystem().getDisplayMetrics().widthPixels;

        if (width >= 1280) {
            num = (int) Math.round(width / 240);
        } else if (width >= 340 && width <= 780) {
            num = (int) Math.round(width / 120);
        } else if (width > 780 && width < 1280) {
            num = (int) Math.round(width / 180);
        }

        return num;

    }

    private void saveInfo() {
        db.collection("attendance").document(edDate.getText().toString() + " " + attTime)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mProgress.dismiss();
                        Toast.makeText(attendance.this, "Attendance Added Successfully", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgress.dismiss();
                        Toast.makeText(attendance.this, "Error While Adding " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    ////////////////////////////////////////////////////////////////
    int i = 0;

    View.OnClickListener getOnClickDoSomething(final CheckBox cbox) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                if (cbox.isChecked()) {
                    meMap.put("" + cbox.getId(), cbox.getText().toString());
                    operation();
                } else {
                    meMap.remove("" + cbox.getId());
                    operation();
                }
            }
        };
    }

    /////////perform operation on checkboxStateChangedListner
    private void operation() {
        int abs, prs;
        int ab;
        abs = numOfStu;
        prs = meMap.size();
        ab = (abs - prs);
        txtAbs.setText(valueOf(ab));
        p = (valueOf(meMap.size()));
        txtPre.setText(p);
    }
}