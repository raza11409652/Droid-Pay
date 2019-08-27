package com.hackdroid.droidpay;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andrognito.flashbar.Flashbar;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hackdroid.droidpay.App.Constant;
import com.hackdroid.droidpay.App.CustomLoader;
import com.hackdroid.droidpay.App.Server;
import com.hackdroid.droidpay.App.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
Button registerBtn , loginBtn ;
Context context ;
EditText mobileNo , password ;
String mobileStr , passwordStr ;
Flashbar flashbar =null ;
SessionManager sessionManager ;
ProgressDialog progressDialog ;
CustomLoader customLoader ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.context = getApplicationContext() ;

        sessionManager = new SessionManager(context) ;
        progressDialog = new ProgressDialog(this) ;
        progressDialog.setCanceledOnTouchOutside(false);
        customLoader = new CustomLoader(this , this);
        if (sessionManager.isLoggedIn()){
           // progressDialog.setMessage("");
            String User = sessionManager.getLoggedInMobile();
            customLoader.showLoader("Please wait while we verify your login session");
            getWallet(User);

        }
        registerBtn = findViewById(R.id.registerBtn);
        loginBtn = findViewById(R.id.loginBtn);
        mobileNo = findViewById(R.id.mobile);
        password = findViewById(R.id.password);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashbar = null ;
               mobileStr = mobileNo.getText().toString().trim()  ;
               passwordStr = password .getText().toString();
               if (TextUtils.isEmpty(mobileStr)){
                   if (flashbar == null){
                       flashbar = alertDanger("Mobile number is required");
                       flashbar.show();
                   }
                   return;
               }else if(TextUtils.isEmpty(passwordStr)){
                   if (flashbar == null){
                       flashbar = alertDanger("Password is required");
                       flashbar.show();
                   }
                   return;
               }else{
                   progressDialog.setMessage("Please wait ....");
                   progressDialog.show();
                   loginUser(mobileStr , passwordStr);
               }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivity  = new Intent(context  , RegisterAccount.class);
                registerActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerActivity);
                //finish();
            }
        });
    }

    private void getWallet(final String user) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.GET_WALLET, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                customLoader.closeLoader();
                Log.d("response" , response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean error = jsonObject.getBoolean("error");
                    if (error ==false){
                        String amount = jsonObject.getString("amount");
                        Constant.WALLET_AMOUNT = amount;
                    }else{
                        Constant.WALLET_AMOUNT = "0";
                    }
                    Intent dash = new Intent(getApplicationContext() , Dash.class);
                    dash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    //sessionManager.setLogin(true);
                    //sessionManager.setLoginMobile(mobileStr);
                    startActivity(dash);
                    finish();
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
                map.put("user" , user);
                return  map;
            }

        };
        RequestQueue requestQueue  = Volley.newRequestQueue(getApplicationContext() ) ;
        requestQueue.add(stringRequest);
    }

    private void loginUser(final String mobileStr, final String passwordStr) {
        StringRequest loginRequest = new StringRequest(Request.Method.POST, Server.LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("login" , response);
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean error = jsonObject.getBoolean("error");
                    if (error ==false){
                        Boolean cardStatus = jsonObject.getBoolean("cardStatus");
                        Constant.currentCardStatus = cardStatus;
                        Constant.currentMobile = mobileStr ;
                        Constant.currentUserName = jsonObject.getString("userName");
                        Constant.currentUserEmail = jsonObject.getString("userEmail");
                        Constant.currentCardDate = jsonObject.getString("cardCretedAt");
                        Constant.currentCardNumber = jsonObject.getString("cardNo");
                        Constant.currentCardUid = jsonObject.getString("cardUid");
                        Constant.WALLET_AMOUNT  =jsonObject.getString("walletAmount");
                        Intent dash = new Intent(getApplicationContext() , Dash.class);
                        dash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                        sessionManager.setLogin(true);
                        sessionManager.setLoginMobile(mobileStr);
                        sessionManager.setLoginuser(Constant.currentUserName);
                        startActivity(dash);
                        finish();
                    }else{
                        String msg = jsonObject.getString("msg");
                        flashbar =alertDanger("Login  error "+msg);
                        flashbar.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                error.printStackTrace();
                Toast.makeText(getApplicationContext()  , ""+error.getLocalizedMessage() , Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String , String> map = new HashMap<>();
                map.put("mobile" , mobileStr);
                map.put("password" , passwordStr);
                return  map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(loginRequest);
    }

    private Flashbar alertDanger(String msg){
        return   new Flashbar.Builder(this)
                .gravity(Flashbar.Gravity.TOP)
                .message(msg)
                .title("Warning")
                .backgroundDrawable(R.drawable.alert_danger)
                .duration(3000)
                .build();
    }
}
