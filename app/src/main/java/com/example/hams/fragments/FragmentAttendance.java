package com.example.hams.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.hams.R;
import com.example.hams.general.AppState;
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
public class FragmentAttendance extends Fragment {

    final Calendar myCalendar = Calendar.getInstance();
    int i = 0;
    private CheckBox checkBox;
    private int numberOfStudents = 0;
    private TextView txtPresent, txtAbsent, txtTotal;
    private EditText edDate;
    private Button submit;
    private String present, attTime;
    private GridLayout gridview;
    private ArrayList<String> keyList;
    private HashMap<String, String> meMap = new HashMap<String, String>();
    private Map<String, Object> user = new HashMap<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog mProgress;
    private RadioButton rdMorning, rdEvening;

    private void updateLabel() {
        String myFormat = "dd-MMM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edDate.setText(sdf.format(myCalendar.getTime()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_attendance, container, false);

        txtPresent = view.findViewById(R.id.txtPresent);
        txtAbsent = view.findViewById(R.id.txtAbsent);
        txtTotal = view.findViewById(R.id.txtTotal);
        submit = view.findViewById(R.id.bt2);
        edDate = view.findViewById(R.id.edDate);
        rdEvening = view.findViewById(R.id.rdEvening);
        rdMorning = view.findViewById(R.id.rdMorning);
        gridview = view.findViewById(R.id.grd);
        gridview.setColumnCount(getColumn());
        mProgress = new ProgressDialog(getContext());
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
///to make locale fixed for datepicker even language is changed
        Locale locale = Locale.US;
        Locale.setDefault(locale);
        Configuration configuration = getContext().getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        getContext().createConfigurationContext(configuration);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        Date c = Calendar.getInstance().getTime();
        rdMorning.setChecked(true);
        //////////////////////////////////
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        edDate.setText(formattedDate);
        submit.setText(R.string.attendance_btn_submit);

        edDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        mProgress.show();
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
                                checkBox = new CheckBox(getContext());
                                checkBox.setId(Integer.parseInt("" + id));
                                checkBox.setText(("" + id));
                                checkBox.setTextSize(20);
                                checkBox.setOnClickListener(getOnClickDoSomething(checkBox));
                                checkBox.setPadding(20, 20, 20, 20);
                                checkBox.setShadowLayer(1, 1, 1, 1);
                                gridview.addView(checkBox);
                                numberOfStudents++;
                                mProgress.dismiss();
                            }
                        } else {
                            mProgress.dismiss();
                            Toast.makeText(getContext(), "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        txtTotal.setText(valueOf(numberOfStudents));
                    }
                });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppState.netCheck.checkConnection(getContext())) {
                    mProgress.show();
                    if (rdMorning.isChecked()) {
                        attTime = "morning";
                    } else {
                        attTime = "evening";
                    }
                    keyList = new ArrayList<>(meMap.keySet());
                    ArrayList<String> valueList = new ArrayList<>(meMap.values());
                    for (int i = 0; i < keyList.size(); i++) {
                        user.put(keyList.get(i), "present");
                    }
                    saveInfo();
                } else {
                    mProgress.dismiss();
                    Toast.makeText(getContext(), getString(R.string.login_toast_offline), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private int getColumn() {
        int num = 0;
        int height, width;
        width = Resources.getSystem().getDisplayMetrics().widthPixels;
        if (width >= 1280) {
            num = Math.round(width / 240);
        } else if (width >= 340 && width <= 780) {
            num = Math.round(width / 120);
        } else if (width > 780 && width < 1280) {
            num = Math.round(width / 180);
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
                        Toast.makeText(getContext(), getString(R.string.attendance_toast_added_sucsess), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgress.dismiss();
                        Toast.makeText(getContext(), getString(R.string.attendance_toast_added_failed) + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    /////////perform operation on checkboxStateChangedListner
    private void operation() {
        int prs, ab, abs = numberOfStudents;
        prs = meMap.size();
        ab = (abs - prs);
        txtAbsent.setText(valueOf(ab));
        present = (valueOf(meMap.size()));
        txtPresent.setText(present);
    }

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
    ///////////////////////////////////////////////

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}


