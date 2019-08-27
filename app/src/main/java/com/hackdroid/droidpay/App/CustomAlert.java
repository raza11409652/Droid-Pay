package com.hackdroid.droidpay.App;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hackdroid.droidpay.R;

public class CustomAlert extends AppCompatActivity {
    Context context ;
    AlertDialog alertDialog;
    public CustomAlert(Context context) {
        this.context = context;
    }
    public  void showAlertDanger(String msg){
        Animation animation = AnimationUtils.loadAnimation(context , R.anim.fadeout);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.alert_danger , null);
        TextView text  = view.findViewById(R.id.text);
        ImageView imageView= view.findViewById(R.id.image);
        imageView.setAnimation(animation);
        text.setText(msg);
        alert.setView(view);
        alert.setCancelable(false) ;
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            }
        });
       alertDialog  = alert.create() ;
        alertDialog.show();
    }
    public  void dismiss(){
        alertDialog.dismiss();
    }
}
