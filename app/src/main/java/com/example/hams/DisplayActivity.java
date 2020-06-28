package com.example.hams;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class DisplayActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StudentAdapter mAdapter;
    private RecyclerView.LayoutManager mlayoutManager;
    private ArrayList<StudentList> stuList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Long> rollno = new ArrayList<Long>();
    private ArrayList<String> tempStudata = new ArrayList<>();
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        mProgress = new ProgressDialog(DisplayActivity.this);
        mProgress.setTitle(getString(R.string.mprogress_title));
        mProgress.setMessage(getString(R.string.mprogress_msg));
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        stuList = new ArrayList<>();
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

                                            long RollNo = 1;
                                            String Sname = "", Address = "", Contact = "", Email = "", Department = "";
                                            int i;
                                            for (i = 0; i < rollno.size(); i++) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (Long.valueOf(document.getString("RollNo")) == rollno.get(i)) { //&& document.getId().contains("morning") && document.getString("" + rollno.get(i)).contains("a")) {
                                                        Sname = document.getString("StudentName");
                                                        Address = document.getString("Address");
                                                        Contact = document.getString("Contact");
                                                        Email = document.getString("Email");
                                                        Department = document.getString("Department");
                                                        RollNo = Long.parseLong(document.getString("RollNo"));
                                                        tempStudata.add(Integer.parseInt("" + RollNo), Sname);
                                                    }
                                                }
                                            }
                                            Toast.makeText(DisplayActivity.this, "size " + tempStudata.size(), Toast.LENGTH_SHORT).show();
//                                            for (int ik=0;ik<tempStudata.size();ik++) {
//
//                                                Toast.makeText(DisplayActivity.this, ""+tempStudata.get(ik), Toast.LENGTH_SHORT).show();
//                                                stuList.add(new StudentList(R.drawable.gvlogo, ""+tempStudata.get(ik)));
//                                            }

                                        }

                                        mProgress.dismiss();
                                    }
                                });
                    }
                });


        for (int j = 0; j < 10; j++) {
            Toast.makeText(this, "" + tempStudata.toString(), Toast.LENGTH_SHORT).show();
            //stuList.add(new StudentList(R.drawable.gvlogo, ""+j));
        }
        RecyclerView studentList = findViewById(R.id.studentList);
        recyclerView = findViewById(R.id.studentList);
        recyclerView.setHasFixedSize(true);
        mlayoutManager = new LinearLayoutManager(this);
        mAdapter = new StudentAdapter(stuList);
        recyclerView.setLayoutManager(mlayoutManager);
        recyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new StudentAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                changeItem(position, "clicked");
            }
        });
    }

    public void changeItem(int position, String text1) {
        stuList.get(position).setText(text1);
        mAdapter.notifyItemChanged(position);
    }
}
