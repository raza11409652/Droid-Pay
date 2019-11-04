package com.hackdroid.droidpay;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hackdroid.droidpay.App.Constant;
import com.hackdroid.droidpay.App.CustomAlert;
import com.hackdroid.droidpay.App.CustomLoader;
import com.hackdroid.droidpay.App.CustomTransferAlert;
import com.hackdroid.droidpay.App.Server;
import com.hackdroid.droidpay.App.SessionManager;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity  implements PaymentResultListener {
TextView name , mobile ;
EditText amountBox , refBox ;
String amount , ref ="N/A" , receiver = Constant.PAYMENT_TO_MOBILE ,sender;
Button payBtn ;
CustomAlert alert ;
CustomLoader loader;
CustomTransferAlert customTransferAlert ;
SessionManager  sessionManager ;
    final Activity activity = this;
    Checkout checkout = new Checkout();
   // final Activity activity = this
    ProgressBar loaderP ;
    BottomSheetDialog bottomSheetBehavior ;

    TextView requiredMoneyText  ,amountTobeAddedText ;
    Button requiredMoneyBtn ;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        name = findViewById(R.id.name);
        mobile = findViewById(R.id.mobile);
        name.setText(Constant.PAYMENT_TO_NAME);
        mobile.setText("+91 "+Constant.PAYMENT_TO_MOBILE);
        sessionManager = new SessionManager(this);
        sender = sessionManager.getLoggedInMobile();
        alert = new CustomAlert(this);
        loader = new CustomLoader(this ,activity);
        customTransferAlert = new CustomTransferAlert(this , activity);
        amountBox = findViewById(R.id.amount);
        refBox = findViewById(R.id.ref);
        payBtn = findViewById(R.id.pay);

        bottomSheetBehavior  = new BottomSheetDialog(this , R.style.SheetDialog);
        bottomSheetBehavior.setContentView(R.layout.required_payment_add);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = amountBox.getText().toString();

                ref = refBox.getText().toString();
                if (TextUtils.isEmpty(ref)){
                    ref = "N/A";
                }
                if (TextUtils.isEmpty(amount)){
                        alert.showAlertDanger("Amount Required");

                }else {
                    Double am    = Double.valueOf(amount);
                     if(am<1.00){
                        alert.showAlertDanger("Amount less than Rs 1 ");
                    }else if(am>10000.00){
                        alert.showAlertDanger("Amount is greater than Rs. 10000");
                    }else{
                        // loader.showLoader("Please wait..");
                        customTransferAlert.showTransferAlert(sender , receiver , amount);
                        transferAmount(sender , receiver , amount   ,ref);
                    }
                }
            }
        });

    }

    private void transferAmount(final String sender, final String receiver, final String amount, final String ref) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.PAYMENT_TRANSFER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Tranfer" , response);
                customTransferAlert.dismiss();
                try {
                    JSONObject jsonObject  = new JSONObject(response);
                    Boolean error=jsonObject.getBoolean("error");
                    if(error==false){
                        /*
                        * error":false,"msg":"Transfer success","transactionAmount":"25","transactionUid":"265eceb6d4d961057f1b483a558e2885",
                        * "reciever":"9835555982","sender":"6204304229","date":"2019-08-23 12:20:27","updatedBal":"11655"*/
                        String date = jsonObject.getString("date");
                        String transactionRef = jsonObject.getString("ref");
                        String transactionUid = jsonObject.getString("transactionUid");
                        String sender=  jsonObject.getString("sender");
                        String updatedBal =jsonObject.getString("updatedBal") ;
                        String amount = jsonObject.getString("transactionAmount");
                        String receiver = jsonObject.getString("reciever");
                        Constant.TRASN_DATE=date ;
                        Constant.TRASN_RCV = receiver ;
                        Constant.TRASN_REF = transactionRef ;
                        Constant.TRASN_AMOUNT = amount ;
                        Constant.TRASN_UID = transactionUid;
                        Constant.TRASN_SENDER = sender;
                        Constant.WALLET_AMOUNT = updatedBal;
                        Intent succ = new Intent(getApplicationContext() , TrnasferSuccess.class);
                        succ.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(succ);

                        finish();
                    }else if(error == true){
                        Boolean walletError = jsonObject.getBoolean("walletError");
                        if (walletError == true){
                            final String requiredMoney = jsonObject.getString("requiredMoney");
                            String msg  = jsonObject.getString("msg");
                           // Toast.makeText( getApplicationContext() , requiredMoney , Toast.LENGTH_SHORT).show();
                            bottomSheetBehavior.show();


                            requiredMoneyText = bottomSheetBehavior.findViewById(R.id.requiredMoneyText);
                            amountTobeAddedText = bottomSheetBehavior.findViewById(R.id.amount_to_be_added);
                            requiredMoneyText.setText(msg);
                            amountTobeAddedText.setText("You need to add "+getString(R.string.rs)+" "+requiredMoney+" in your wallet to make this transaction");
                            requiredMoneyBtn = bottomSheetBehavior.findViewById(R.id.requiredMoneyBtn);

                            loaderP= bottomSheetBehavior.findViewById(R.id.loader);
                            loaderP.setVisibility(View.GONE);
                            requiredMoneyBtn.setVisibility(View.VISIBLE);
                            requiredMoneyBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                requiredMoneyBtn.setVisibility(View.GONE);
                                loaderP.setVisibility(View.VISIBLE);
                                 //   startPaymentRazorPay(requiredMoney);
                                    startWalletInit(sessionManager.getLoggedInMobile() , requiredMoney);
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext() , "Can't connect to server"  , Toast.LENGTH_SHORT).show(); ;

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String , String> map = new HashMap<>();
                map.put("sender" , sender);
                map.put("receiver" , receiver);
                map.put("amount" , amount);
                map.put("ref" ,ref);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void startWalletInit(final String loggedInMobile, final String requiredMoney) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.WALLET_INIT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response" , response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean error = jsonObject.getBoolean("error");
                    if (error==false){

                        String uid = jsonObject.getString("uid");
                        Constant.TRANSACTION_UID = uid;
                        Constant.amountToBeAdded = requiredMoney ;
                        startPaymentRazorPay(Constant.amountToBeAdded);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String  , String>map = new HashMap<>();
                map.put("amount" , requiredMoney);
                map.put("user" , loggedInMobile);
                return map ;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void startPaymentRazorPay(String requiredMoney) {
        // final Activity activity = this;

        //final Checkout co = new Checkout();
        try {
            String userMobile = sessionManager.getLoggedInMobile();
            Integer priceToBePaid = 0;
            priceToBePaid = Integer.valueOf(requiredMoney);
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
    private void ConfirmWalletAdd(final String userMobile, final String transactionUid, final String transactionSrc) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.WALLET_CONFIRM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("res" , response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean error = jsonObject.getBoolean("error");
                    if (error == false){
                        String updatedBalance = jsonObject.getString("updatedBalance");
                        String uid = jsonObject.getString("uid");
                        String amountAdded = jsonObject.getString("amountAdded");
                        String date = jsonObject.getString("date");
                        Constant.TRANSACTION_UID=uid ;
                        Constant.WALLET_AMOUNT = updatedBalance;
                        Constant.amountToBeAdded = amountAdded;
                        Constant.ORDER_DATE = date;
                        loaderP.setVisibility(View.GONE);
                        bottomSheetBehavior.dismiss();
                        transferAmount(sessionManager.getLoggedInMobile() , receiver , amount  , ref);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String , String> map = new HashMap<>();
                map.put("user" , userMobile);
                map.put("uid" , transactionUid);
                map.put("src" , transactionSrc);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(getApplicationContext() , ""+s , Toast.LENGTH_SHORT).show();
        Constant.TRANSACTION_SRC = "RazorPay Payment"+s;
        ConfirmWalletAdd(sessionManager.getLoggedInMobile() ,  Constant.TRANSACTION_UID  , Constant.TRANSACTION_SRC);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getApplicationContext() , "Add Money Failed : "+s , Toast.LENGTH_SHORT).show();
        requiredMoneyBtn.setVisibility(View.VISIBLE);
        loaderP.setVisibility(View.GONE);
    }
}
