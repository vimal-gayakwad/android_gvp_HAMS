package com.example.hams;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.hams.fragments.FragmentAccount;
import com.example.hams.fragments.FragmentAddWarden;
import com.example.hams.fragments.FragmentAttendance;
import com.example.hams.fragments.FragmentDeleteStu;
import com.example.hams.fragments.FragmentList;
import com.example.hams.fragments.FragmentLogout;
import com.example.hams.fragments.FragmentReport;
import com.example.hams.fragments.FragmentUpdate;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FragmentAttendance()).commit();
            navigationView.setCheckedItem(R.id.nav_attendance);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_attendance:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentAttendance()).commit();
                break;
            case R.id.nav_student_report:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentReport()).commit();
                break;
            case R.id.nav_student_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentList()).commit();
                break;
            case R.id.nav_update_details:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentUpdate()).commit();
                break;
            case R.id.nav_delete_details:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentDeleteStu()).commit();
                break;
            case R.id.nav_add_warden:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentAddWarden()).commit();
                break;
            case R.id.nav_logout:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentLogout()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentAccount()).commit();
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
