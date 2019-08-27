package com.hackdroid.droidpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackdroid.droidpay.App.Constant;

public class TrnasferSuccess extends AppCompatActivity {
Button home , addMoney  , passbook ;
TextView receiver , amount , sender  , transactionId , date , updatedBal  , ref;
ImageView succ ;
Animation animation ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trnasfer_success);
        addMoney = findViewById(R.id.addMoney);
        animation = AnimationUtils.loadAnimation(getApplicationContext() , R.anim.fadeout);

        passbook = findViewById(R.id.passbook);
        succ = findViewById(R.id.successImg);
        succ.setAnimation(animation);
        home  =findViewById(R.id.home);
        sender=findViewById(R.id.sender);
        amount = findViewById(R.id.amountTransfer);
        receiver = findViewById(R.id.receiver);
        transactionId = findViewById(R.id.orderID);
        date = findViewById(R.id.date);
        updatedBal = findViewById(R.id.updatedBalance);
        ref = findViewById(R.id.ref);

        ref.setText(Constant.TRASN_REF);
        receiver.setText("TO: "+Constant.TRASN_RCV);
        sender.setText("FROM: "+Constant.TRASN_SENDER);
        amount.setText(Constant.TRASN_AMOUNT);
        updatedBal.setText("Updated DroidPay Wallet Balance "+
                getString(R.string.rs)+" " + Constant.WALLET_AMOUNT);
        transactionId .setText(Constant.TRASN_UID);
        date.setText(Constant.TRASN_DATE);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open dash
                Intent home = new Intent(getApplicationContext() , Dash.class);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(home);
                finish();
            }
        });
        passbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(getApplicationContext() , Passbook.class);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(home);
               // finish();
            }
        });
        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(getApplicationContext() , AddMoney.class);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(home);
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
