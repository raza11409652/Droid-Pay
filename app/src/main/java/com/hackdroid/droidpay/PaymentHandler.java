package com.hackdroid.droidpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hackdroid.droidpay.App.Constant;
import com.hackdroid.droidpay.App.Server;
import com.hackdroid.droidpay.App.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentHandler extends AppCompatActivity {
SessionManager sessionManager ;
String userMobile , transactionUid , transactionSrc ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_handler);
        sessionManager = new SessionManager(this);
        userMobile = sessionManager.getLoggedInMobile();
        transactionSrc  = Constant.TRANSACTION_SRC  ;
        transactionUid = Constant.TRANSACTION_UID ;
        //Toast.makeText(getApplicationContext() , ""+userMobile + transactionUid + transactionSrc , Toast.LENGTH_SHORT).show();
        ConfirmWalletAdd(userMobile , transactionUid , transactionSrc);
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
                        Intent successPayment = new Intent(getApplicationContext()  , SuccessAddWallet.class);
                        successPayment.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(successPayment);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext() , "Can't connect to server"  , Toast.LENGTH_SHORT) ;

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
    public void onBackPressed() {
        return;
    }
}
