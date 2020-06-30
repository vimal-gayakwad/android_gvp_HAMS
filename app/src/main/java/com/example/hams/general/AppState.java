package com.example.hams.general;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hams.R;
import com.example.hams.student.studentHome;
import com.example.hams.warden.wardenHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class AppState {
    private static AppState singleInstance;
    private boolean isLoggingOut;

    private AppState() {
    }

    public static AppState getSingleInstance() {
        if (singleInstance == null) {
            singleInstance = new AppState();
        }
        return singleInstance;
    }

    public boolean isLoggingOut() {
        return isLoggingOut;
    }

    public void setLoggingOut(boolean isLoggingOut) {
        this.isLoggingOut = isLoggingOut;
    }

    public static class UploadImage extends AppCompatActivity {
        public Uri imageuri;
        Button btnUpload, btnChoose;
        ImageView imageView;
        StorageReference mstorage;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_upload_image);
            btnChoose = findViewById(R.id.btnChoose);
            btnUpload = findViewById(R.id.btnUpload);
            imageView = findViewById(R.id.imageViewUpload);
            mstorage = FirebaseStorage.getInstance().getReference("Images");
            btnChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileChooser();
                }
            });
            btnUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileUploader();
                }
            });
        }

        private String getExtenstion(Uri uri) {
            ContentResolver cr = getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
        }

        private void FileUploader() {
            //        StorageReference reference=mstorage.child(System.currentTimeMillis()+"."+getExtenstion(imageuri));
            //        reference.putFile(imageuri)
            //                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            //                    @Override
            //                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            //                        // Get a URL to the uploaded content
            //                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
            //                        Toast.makeText(UploadImage.this, "Image Uploaded suucess", Toast.LENGTH_SHORT).show();
            //                    }
            //                })
            //                .addOnFailureListener(new OnFailureListener() {
            //                    @Override
            //                    public void onFailure(@NonNull Exception exception) {
            //                        // Handle unsuccessful uploads
            //                        // ...
            //                        Toast.makeText(UploadImage.this, "Fails", Toast.LENGTH_SHORT).show();
            //                    }
            //                });


            Uri file = Uri.fromFile(new File("R.drawable.gvlogo.png"));
            StorageReference riversRef = mstorage.child("images/rivers.jpg");

            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Toast.makeText(UploadImage.this, "Sucess", Toast.LENGTH_SHORT).show();
                            // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(UploadImage.this, "fail" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            // ...
                        }
                    });


        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                imageuri = data.getData();
                imageView.setImageURI(imageuri);

            }
        }

        private void FileChooser() {

            Intent intent = new Intent();
            intent.setType("image/");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 1);
        }

    }

    public static class TrippleDes {
    }

    public static class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
        private ArrayList<wardenHome.StudentList> mStudentList;
        private onItemClickListener mListener;

        public StudentAdapter(ArrayList<wardenHome.StudentList> studentLists) {
            this.mStudentList = studentLists;
        }

        public void setOnItemClickListener(onItemClickListener listener) {
            mListener = listener;
        }

        @NonNull
        @Override
        public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);
            StudentViewHolder studentViewHolder = new StudentViewHolder(view, mListener);
            return studentViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
            wardenHome.StudentList currentStudent = mStudentList.get(position);
            holder.imageView.setImageResource(currentStudent.getmImageRes());
            holder.textView.setText(currentStudent.getString());
        }

        @Override
        public int getItemCount() {
            return mStudentList.size();
        }

        public interface onItemClickListener {
            void onItemClick(int position);
        }

        public static class StudentViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageView;
            public TextView textView;

            public StudentViewHolder(@NonNull View itemView, final onItemClickListener listener) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imgIcon);
                textView = itemView.findViewById(R.id.txtView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(position);
                            }
                        }
                    }
                });
            }
        }
    }

    public static class netCheck {
        public static boolean checkConnection(Context context) {
            final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connMgr != null) {
                NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

                if (activeNetworkInfo != null) { // connected to the internet
                    // connected to the mobile provider's data plan
                    if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        // connected to wifi
                        return true;
                    } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
                }
            }
            return false;
        }
    }

    public static class Login extends AppCompatActivity {

        boolean flag = false;
        private EditText edUsername, edPassword;
        private Button btnLogin;
        private Spinner spinnerUser;
        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private Intent intent;
        private int userType = 0;
        private String utype = "";
        private ProgressDialog mProgress;
        private SharedPreferences pref;//to retrive data from device
        private SharedPreferences.Editor editor; //to store data from device

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            editor = pref.edit();

            flag = pref.getBoolean("login", false);

            if (flag == true) {

                if (pref.getString("utype", "").equals("student")) {
                    intent = new Intent(getApplicationContext(), studentHome.class);
                } else if (pref.getString("utype", "").equals("warden")) {
                    intent = new Intent(getApplicationContext(), wardenHome.class);
                }

                intent.putExtra("iUserName", pref.getString("username", ""));//edUsername.getText().toString());
                intent.putExtra("iPassword", pref.getString("password", ""));//,edPassword.getText().toString());
                intent.putExtra("iUtype", pref.getString("utype", ""));
                intent.putExtra("iRollNo", pref.getLong("RollNo", 0));
                startActivity(intent);
            } else {
                edUsername = findViewById(R.id.edusername);
                edPassword = findViewById(R.id.edpassword);
                btnLogin = findViewById(R.id.btnLogin);
                spinnerUser = findViewById(R.id.spUsers);
                //initilize progress dialog for login
                mProgress = new ProgressDialog(Login.this);
                mProgress.setTitle(getString(R.string.mprogress_title));
                mProgress.setMessage(getString(R.string.mprogress_msg));
                mProgress.setCancelable(false);
                mProgress.setIndeterminate(true);
                spinnerUser.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        userType = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(Login.this, getString(R.string.login_toast_utype), Toast.LENGTH_SHORT).show();
                        userType = 0;
                        return;
                    }
                });
                //////////////////btnLogin/////////////
                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // to check internet Connectivity
                        if (netCheck.checkConnection(getApplicationContext())) {
                            mProgress.show();
                            final String username = edUsername.getText().toString();
                            final String password = edPassword.getText().toString();
                            //check the number of spinnerUser value and set user type
                            if (userType == 0) {
                                Toast.makeText(Login.this, getString(R.string.login_toast_utype), Toast.LENGTH_SHORT).show();
                                spinnerUser.setFocusable(true);
                                mProgress.dismiss();
                            }
                            if (userType == 1) {
                                utype = "warden";
                                intent = new Intent(Login.this, wardenHome.class);
                            }
                            if (userType == 2) {
                                utype = "student";
                                intent = new Intent(Login.this, studentHome.class);
                            }
                            if (TextUtils.isEmpty(edUsername.getText())) {
                                edUsername.setError(getString(R.string.login_setError_uname));
                                mProgress.dismiss();
                            } else if (TextUtils.isEmpty(edPassword.getText())) {
                                edPassword.setError(getString(R.string.login_setError_password));
                                mProgress.dismiss();
                            }
                            ////get Value from database and Check The Value///////////////////////
                            else if (userType != 0) {
                                db.collection(utype).document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            String Fusername = documentSnapshot.getString("username");//Firebase Username
                                            String Fpassword = documentSnapshot.getString("password");//Firebase Password
                                            if (username.equals(Fusername) && password.equals(Fpassword)) {
                                                mProgress.dismiss();
                                                String un = username,
                                                        ps = password,
                                                        ut = utype;
                                                if (userType == 2) {
                                                    long rl = Long.parseLong((String) documentSnapshot.get("RollNo"));
                                                    intent.putExtra("iRollNo", rl);
                                                    editor.putLong("RollNo", rl);
                                                }
                                                editor.putString("utype", ut);
                                                editor.putBoolean("login", true);
                                                editor.putString("password", ps);
                                                editor.putString("username", un);
                                                editor.apply();
                                                intent.putExtra("docId", documentSnapshot.getId());
                                                intent.putExtra("iUserName", un);//edUsername.getText().toString());
                                                intent.putExtra("iPassword", ps);//,edPassword.getText().toString());
                                                intent.putExtra("iUtype", ut);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(Login.this, getString(R.string.login_toast_incorrect_credentials), Toast.LENGTH_SHORT).show();
                                                mProgress.dismiss();
                                            }
                                        } else {
                                            mProgress.dismiss();
                                            Toast.makeText(Login.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(Login.this, getString(R.string.login_toast_utype), Toast.LENGTH_SHORT).show();
                                spinnerUser.setFocusable(true);
                            }
                        } else {
                            Toast.makeText(Login.this, getString(R.string.login_toast_offline), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    public static class DisplayActivity extends AppCompatActivity {
        private RecyclerView recyclerView;
        private StudentAdapter mAdapter;
        private RecyclerView.LayoutManager mlayoutManager;
        private ArrayList<wardenHome.StudentList> stuList;
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
}