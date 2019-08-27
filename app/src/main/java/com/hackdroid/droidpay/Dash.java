package com.hackdroid.droidpay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hackdroid.droidpay.App.Constant;
import com.hackdroid.droidpay.App.SessionManager;
import com.hackdroid.droidpay.Fragment.HomeFrag;
import com.hackdroid.droidpay.Fragment.ProfileFrag;
import com.hackdroid.droidpay.Fragment.ScanFrag;

public class Dash extends AppCompatActivity {
    SessionManager sessionManager ;
    FrameLayout frameLayout ;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        frameLayout = findViewById(R.id.frame_container);
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        sessionManager = new SessionManager(this);
        //Toast.makeText(getApplicationContext() , "name:"+ Constant.currentUserName+"\n mobile:"+Constant.currentMobile +"\nemail:"+Constant.currentUserEmail, Toast.LENGTH_SHORT).show();
        if (sessionManager.isLoggedIn()==false){
            Intent login = new Intent(getApplicationContext() , LoginActivity.class);
            startActivity(login);
            finish();
        }
        loadFragment(new HomeFrag());
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.home:
                loadFragment(new HomeFrag());
                    return true;
                case R.id.scan:
                  loadFragment(new ScanFrag());
                    return true;
                case R.id.user:
                   loadFragment(new ProfileFrag());
                    return true;
            }
            return false;
        }
    };
    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (sessionManager.isLoggedIn()==false){
            Intent login = new Intent(getApplicationContext() , LoginActivity.class);
            startActivity(login);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.isLoggedIn()==false){
            Intent login = new Intent(getApplicationContext() , LoginActivity.class);
            startActivity(login);
            finish();
        }
    }
}
