package com.hackdroid.droidpay.App;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.channguyen.adv.AnimatedDotsView;
import com.hackdroid.droidpay.R;

public class CustomLoader extends AppCompatActivity {
    Context context ;
    Activity activity ;
    AlertDialog alertDialog;
    boolean isKeyboardShowing = false;
    public CustomLoader(Context context , Activity activity) {
        this.context = context;
        this.activity = activity;

    }
    public  void showLoader(String str){
        InputMethodManager imm = (InputMethodManager)activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
           // writeToLog("Software Keyboard was shown");
         //   imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }else{
          //  imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.loader , null);
        TextView textView = view.findViewById(R.id.text);
        textView.setText(""+str);
        alert.setView(view);
        alert.setCancelable(false) ;
        alertDialog  = alert.create() ;
        //alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_input);
        alertDialog.show();
    }
    public  void closeLoader(){
        alertDialog.dismiss();
    }
}
