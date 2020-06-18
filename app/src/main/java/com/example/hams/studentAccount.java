package com.example.hams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class studentAccount extends AppCompatActivity {
    Button btnSubmit;
    EditText edOldPassword,edNewPassword,edConfirmPassword;
    String dbPassword;
    Intent intent;
    private String uname;

    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private Map <String,String> LoginData=new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_account);

        btnSubmit=(Button)findViewById(R.id.btnChangePasswd);
        edNewPassword=(EditText)findViewById(R.id.edNewPassword);
        edOldPassword=(EditText)findViewById(R.id.edOldPassword);
        edConfirmPassword=(EditText)findViewById(R.id.edConfPassword);

        intent=getIntent();

        dbPassword=intent.getStringExtra("iPassword1");
        uname=intent.getStringExtra("iUserName1");
        
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edOldPassword.getText())){
                    edOldPassword.setError("Enter Password");
                }
                if(!TextUtils.equals(dbPassword,edOldPassword.getText())){
                    edOldPassword.setError("Enter Correct Password");
                }
                if(TextUtils.isEmpty(edNewPassword.getText())){
                    edNewPassword.setError("Enter New Password");
                }
                if(TextUtils.isEmpty(edConfirmPassword.getText())){
                    edConfirmPassword.setError("Confirm Password");
                }
                if(!TextUtils.equals(edNewPassword.getText().toString(),edConfirmPassword.getText().toString())){
                    edConfirmPassword.setError("Password Do Not Match");
                }
                else if((!TextUtils.isEmpty(edConfirmPassword.getText() )&& (!TextUtils.isEmpty(edOldPassword.getText())&&(!TextUtils.isEmpty(edNewPassword.getText()))))) {
                    LoginData.put("password", edConfirmPassword.getText().toString());
                    db.collection("student").document(uname).update("password", edConfirmPassword.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(studentAccount.this, "Updated Successfully \n Please Login with New Password", Toast.LENGTH_LONG).show();
                                intent=new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(studentAccount.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            });
                }
            }
        });
    }
}
