package com.hackdroid.droidpay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.andrognito.flashbar.Flashbar;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hackdroid.droidpay.App.Constant;
import com.hackdroid.droidpay.App.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OtpVerify extends AppCompatActivity {
Toolbar toolbar;
TextView otpHint ;
EditText otpBox ;
Button otpVerify  ;
String otp ;
ProgressDialog progressDialog ;
Flashbar flashbar =null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);
        toolbar = findViewById(R.id.toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.left_arrow);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        if (getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // toolbar.setTitle("Manage Booking");
        }
        setTitle(getString(R.string.otp_verify));
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        otpHint = findViewById(R.id.otpEnterTxt);
        otpBox = findViewById(R.id.otpEdtBox);
        otpVerify = findViewById(R.id.otpVerifyBtn);
        otpHint.setText("Hello  \nPlease enter 5 digit OTP sent to your mobile number +91-"+
                Constant.currentMobile +" below.");
        otpVerify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                flashbar = null ;
                otp = otpBox.getText().toString();
                if(TextUtils.isEmpty(otp)){
                  //  otpBox.setError();
                    flashbar = showAlert( "OTP required", "Error") ;
                    flashbar.show();
                    return;
                }else{
                    progressDialog.setMessage("Please wait ...");
                    progressDialog.show();
                    verifyOTP(otp , Constant.currentMobile);
                }
            }
        });
    }

    private void verifyOTP(final String otp, final String currentMobile) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.OTP_VERIFY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("OTP" , response);
                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean error = jsonObject.getBoolean("error");
                    if(error == false){
                        Intent cardRegistration  = new Intent(getApplicationContext() , CardRegistration.class);
                       Constant.currentMobile  = currentMobile ;
                        cardRegistration.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(cardRegistration);
                    }else{
                        String errorMsg = jsonObject.getString("msg");
                        flashbar = showAlert( "OTP Error "+errorMsg, "Warning") ;
                        flashbar.show();
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
                map.put("token"  , otp);
                map.put("mobile" , currentMobile);
                return  map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private  Flashbar showAlert(String msg , String title){
        return new Flashbar.Builder(this)
                .gravity(Flashbar.Gravity.BOTTOM)
                .message(msg)
                .title(title)
                .backgroundDrawable(R.drawable.alert_danger)
                .duration(3000)
                .build();
    }
}
