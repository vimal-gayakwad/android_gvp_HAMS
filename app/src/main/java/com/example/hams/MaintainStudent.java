package com.example.hams;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

public class MaintainStudent extends AppCompatActivity {
   private Button reg,update,del;
   private  FirebaseFirestore db = FirebaseFirestore.getInstance();
   private Intent intent;
   private EditText rollnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain);
    reg=(Button)findViewById(R.id.btnStuRegister);
    update=(Button)findViewById(R.id.btnStuUpdate);
    del=(Button)findViewById(R.id.btnstuDel);
    rollnum=(EditText)findViewById(R.id.edRollNoToMaintain);

    reg.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(),updateStudent.class));
        }
    });
    update.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(rollnum.getText())) {
                rollnum.setError("Enter Roll Number");
            } else if (!TextUtils.isEmpty(rollnum.getText())) {
                intent=new Intent(MaintainStudent.this, updateStudent.class);
                intent.putExtra("rollNo",rollnum.getText().toString());
                startActivity(intent);
            }

        }
    });
    del.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    });
        }
}
