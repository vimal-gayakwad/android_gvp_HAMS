package com.example.hams.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.hams.R;
import com.example.hams.general.AppState;

public class FragmentLogout extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout, container, false);
        //return super.onCreateView(inflater, container, savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.logout_title));
        builder.setPositiveButton(getString(R.string.logout_positive), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //SharedPreferences myPrefs = getSharedPreferences("MyPref",
                //      MODE_PRIVATE);
                //SharedPreferences.Editor editor = myPrefs.edit();
                //editor.putBoolean("login", false);
                //editor.apply();
                //finish();
                AppState.getSingleInstance().setLoggingOut(true);
                Intent intent = new Intent(getContext(),
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
        return view;
    }
}
