package com.hackdroid.droidpay.App;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.channguyen.adv.AnimatedDotsView;
import com.hackdroid.droidpay.R;

public class CustomTransferAlert extends AppCompatActivity {
    Context context ;
    Activity activity;
    AlertDialog alertDialog;
    public CustomTransferAlert(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }
    public void showTransferAlert(String sender , String receiver , String amount){
        InputMethodManager imm = (InputMethodManager)activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            // writeToLog("Software Keyboard was shown");
           // imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.alert_transfer , null);
        AnimatedDotsView animatedDotsView = view.findViewById(R.id.dots);
        animatedDotsView.startAnimation();
        TextView senderView  , rcvView , text ;
        senderView  =view.findViewById(R.id.sender);
        rcvView = view.findViewById(R.id.receiver);
        text = view.findViewById(R.id.text);
        senderView.setText(sender);
        rcvView.setText(receiver);
        text.setText(context.getString(R.string.rs)+" "+ amount);
        alert.setView(view);
        alert.setCancelable(false) ;
        alertDialog  = alert.create() ;
        alertDialog.show();
    }
    public void dismiss(){
        alertDialog.dismiss();
    }
}
