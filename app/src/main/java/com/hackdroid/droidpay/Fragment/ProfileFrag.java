package com.hackdroid.droidpay.Fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hackdroid.droidpay.App.Constant;
import com.hackdroid.droidpay.App.SessionManager;
import com.hackdroid.droidpay.CardActivity;
import com.hackdroid.droidpay.LoginActivity;
import com.hackdroid.droidpay.Passbook;
import com.hackdroid.droidpay.QrProfile;
import com.hackdroid.droidpay.R;
import com.hackdroid.droidpay.UpdateProfile;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFrag extends Fragment {

View view  ;
TextView userName , userMobile;
Button droidCard , qrCode  , logout , passbook,updateProfile;
SessionManager sessionManager ;
Activity activity ;

    public ProfileFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_profile, container, false);
        droidCard = view.findViewById(R.id.droid_card);
        qrCode = view.findViewById(R.id.your_qr_code);
        passbook = view.findViewById(R.id.passbook);
        userName = view.findViewById(R.id.userProfileName);
        userMobile = view.findViewById(R.id.userMobileNumber);
        logout = view.findViewById(R.id.logout);
        updateProfile = view.findViewById(R.id.update);
        sessionManager = new SessionManager(getContext());
        userMobile.setText("+91-"+sessionManager.getLoggedInMobile());
        userName.setText(sessionManager.getLoggedInUser());
activity = getActivity() ;
        droidCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent yourCard = new Intent(getContext()  , CardActivity.class);
                yourCard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(yourCard);
            }
        });
        qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent qrProfile = new Intent(getContext() , QrProfile.class);
                qrProfile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(qrProfile);
            }
        });
        passbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent passbook = new Intent(getContext() , Passbook.class);
                passbook.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(passbook);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setCancelable(false);
                LayoutInflater li = LayoutInflater.from(getContext());
                View promptsView = li.inflate(R.layout.logout_alert, null);
                alertDialog.setView(promptsView);
                alertDialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sessionManager.setLogin(false);
                        //sessionManager.setLogout(true);
                        sessionManager.setLoginuser(null);
                        sessionManager.setLoginMobile(null);
                        Intent logout = new Intent(getContext()  , LoginActivity.class);
                        logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(logout);

                        activity.finishActivity(22);

                    }
                }) .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

            AlertDialog dialog = alertDialog.create();
            dialog.show();
            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(getContext() , UpdateProfile.class);
                profile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(profile);
            }
        });
    return view ;
    }


}
