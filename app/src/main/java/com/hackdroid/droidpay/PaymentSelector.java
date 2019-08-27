package com.hackdroid.droidpay;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.flashbar.Flashbar;
import com.hackdroid.droidpay.App.Constant;
import com.hackdroid.droidpay.App.SessionManager;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class PaymentSelector extends AppCompatActivity implements PaymentResultListener {
Toolbar toolbar ;
    private static final int UPI_PAYMENT = 9999;
    TextView amountTobeAddedTextView ;
CardView gPayCard  , phonePeCard , paytmCard  , rzrPayCard ;
Flashbar flashbar ;
Checkout checkout = new Checkout();
    final Activity activity = this;
    SessionManager sessionManager ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_selector);
        toolbar = findViewById(R.id.toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.left_arrow);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        if (getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // toolbar.setTitle("Manage Booking");
        }
        sessionManager = new SessionManager(getApplicationContext());

        Checkout.preload(getApplicationContext());
        setTitle(getString(R.string.select_payment_mode));
        gPayCard = findViewById(R.id.gPayCard);
        phonePeCard = findViewById(R.id.phonePeCard) ;
        paytmCard = findViewById(R.id.paytmCard);
        rzrPayCard = findViewById(R.id.rzrPayCard);
        amountTobeAddedTextView = findViewById(R.id.amount_to_added_txt);
        amountTobeAddedTextView.setText("An amount of  "+getString(R.string.rs)+""+ Constant.amountToBeAdded +" will be added to your droidpay wallet on successfull payment");
        gPayCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGpay() ;

            }
        });
        phonePeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phonePeInit();
            }
        });
        rzrPayCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // createOrderId();
            if (isConnectionAvailable(getApplicationContext())){
                startPayment(Constant.amountToBeAdded);
            }else{
                Toast.makeText(getApplicationContext() , "Internet not available"  , Toast.LENGTH_SHORT).show();
            }
            }
        });
    }
    private void startPayment(String amount) {
       // final Activity activity = this;

        //final Checkout co = new Checkout();
        try {
            String userMobile = sessionManager.getLoggedInMobile();
            Integer priceToBePaid = 0;
            priceToBePaid = Integer.valueOf(amount);
            priceToBePaid = priceToBePaid * 100;
            JSONObject options = new JSONObject();
            options.put("name", "Droid Pay");
            options.put("description", "Adding Rs "+(priceToBePaid/100)+" in your droidpay wallet" );
            //options.put("theme","#70884B");
            options.put("image","https://res.cloudinary.com/flatsondemand/image/upload/v1566396737/droidpaylogo_zgsg3x.png");
            options.put("currency", "INR");
            options.put("amount",""+ priceToBePaid);
            JSONObject preFill = new JSONObject();
            JSONObject theme = new JSONObject();
            theme.put("color"  ,"#456E3F" );
            options.put("theme" , theme);
            //  preFill.put("email", "test@razorpay.com");
            preFill.put("contact", userMobile);

            options.put("prefill", preFill);

            checkout.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }


    private void phonePeInit() {
        flashbar = null ;
        String PHONE_PE_PCKG = "com.phonepe.app";
        //"com.google.android.apps.nbu.paisa.user";
        //"com.phonepe.app";
        //
        // int GOOGLE_PAY_REQUEST_CODE = 123;

        try{
            Uri uri =new Uri.Builder()
                    .scheme("upi")
                    .authority("pay")
                    .appendQueryParameter("pa", getString(R.string.upiId))
                    .appendQueryParameter("pn", "Droid Pay")
                    .appendQueryParameter("tn", "Payment for ")
                    .appendQueryParameter("am", "1.00")
                    .appendQueryParameter("cu", "INR")
                    .build();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(PHONE_PE_PCKG);
            startActivityForResult(intent, UPI_PAYMENT);
        }catch (Exception e){
            e.printStackTrace();
            flashbar = alertDanger("Phonepe not" +
                    " found  in device" );
            flashbar.show();
            flashbar = null ;
            // Toast.makeText(getApplicationContext() , "GPay not found" , Toast.LENGTH_SHORT).show();
        }
    }

    private void startGpay() {
        flashbar = null ;
        String G_PAY_PCKG = "com.google.android.apps.nbu.paisa.user";
        //"com.google.android.apps.nbu.paisa.user";
        //"com.phonepe.app";
        //
        // int GOOGLE_PAY_REQUEST_CODE = 123;

        try{
            Uri uri =new Uri.Builder()
                    .scheme("upi")
                    .authority("pay")
                    .appendQueryParameter("pa", getString(R.string.upiId))
                    .appendQueryParameter("pn", "Droid Pay")
                    .appendQueryParameter("tn", "Payment for ")
                    .appendQueryParameter("am", "1.00")
                    .appendQueryParameter("cu", "INR")
                    .build();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(G_PAY_PCKG);
            startActivityForResult(intent, UPI_PAYMENT);
        }catch (Exception e){
            e.printStackTrace();
            flashbar = alertDanger("GPay not found  in device" );
            flashbar.show();
            flashbar = null ;
           // Toast.makeText(getApplicationContext() , "GPay not found" , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case  UPI_PAYMENT:
                    if(data!=null){
                        Toast.makeText(getApplicationContext() , ""+data , Toast.LENGTH_SHORT).show();
                        String trxt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    }else{
                        Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                    break;
                default:

                    break;



            }
        }
    }
    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(getApplicationContext())) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(getApplicationContext(), "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.d("UPI", "responseStr: "+approvalRefNo);
              //  Intent payconfirmation =new Intent(getApplicationContext() , PaymentConfirmation.class);
                String s = "responseStr: "+approvalRefNo;
               // payconfirmation.putExtra("payref"  , s);
                //payconfirmation.putExtra("amount" , Constant.AMOUNT_TO_BE_PAID);
               // startActivity(payconfirmation);

            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(getApplicationContext(), "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
    public  Flashbar alertDanger(String msg){
        return new Flashbar.Builder(this)
                .gravity(Flashbar.Gravity.TOP)
                .message(msg)
                .title("Warning")
                .backgroundDrawable(R.drawable.alert_danger)
                .duration(3000)
                .build();
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(getApplicationContext() , ""+s , Toast.LENGTH_SHORT).show();
        Constant.TRANSACTION_SRC = "RazorPay Payment"+s;
        Intent paymentHandler = new Intent(getApplicationContext() , PaymentHandler.class);
        paymentHandler.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(paymentHandler);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getApplicationContext() , ""+s , Toast.LENGTH_SHORT).show();
    }
}
