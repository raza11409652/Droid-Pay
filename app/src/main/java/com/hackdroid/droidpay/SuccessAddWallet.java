package com.hackdroid.droidpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.hackdroid.droidpay.App.Constant;

public class SuccessAddWallet extends AppCompatActivity {
    TextView amountAdded , addedSrc , orderId , orderDate ,updatedBalance , successMsg;
    Animation smallToBig ;
    Button home , add , passbook ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_add_wallet);
        smallToBig = AnimationUtils.loadAnimation(getApplicationContext() , R.anim.fadeout);
        successMsg = findViewById(R.id.successMsg);
        successMsg.setAnimation(smallToBig);
        amountAdded = findViewById(R.id.amountAdded);
        addedSrc = findViewById(R.id.addedSrc) ;
        orderId = findViewById(R.id.orderID);
        orderDate = findViewById(R.id.date);
        updatedBalance = findViewById(R.id.updatedBalance);
        amountAdded.setText(getString(R.string.rs)+" "+Constant.amountToBeAdded);
        addedSrc.setText(Constant.TRANSACTION_SRC);
        orderId.setText(Constant.TRANSACTION_UID);
        orderDate.setText(Constant.ORDER_DATE);

        home = findViewById(R.id.home);
        add = findViewById(R.id.addMoney);
        passbook = findViewById(R.id.passbook);
        passbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(getApplicationContext() , Passbook.class) ;
                startActivity(home);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              //  finish();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(getApplicationContext() , Dash.class) ;
                startActivity(home);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addM = new Intent(getApplicationContext() , AddMoney.class);
                addM.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(addM);
            }
        });
        updatedBalance.setText("Updated DroidPay Wallet Balance "+
                getString(R.string.rs)+" " + Constant.WALLET_AMOUNT);

    }

    @Override
    public void onBackPressed() {

        return;
    }
}
