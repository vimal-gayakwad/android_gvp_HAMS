package com.example.hams.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hams.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FragmentAddWarden extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText adminUname, adminPass;
    private Map<String, Object> user;
    private Button btnSubmit;
    private ProgressDialog mProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_warden, container, false);
        //return super.onCreateView(inflater, container, savedInstanceState);

        adminUname = view.findViewById(R.id.edWUsername);
        adminPass = view.findViewById(R.id.edWPassword);
        btnSubmit = view.findViewById(R.id.btNewWarden);

        mProgress = new ProgressDialog(getContext());
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
                                    Toast.makeText(getContext(), "Warden Added SuccessFully", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
        return view;
    }
}
