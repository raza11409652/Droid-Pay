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
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.hackdroid.droidpay.App.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class RegisterAccount extends AppCompatActivity {
Toolbar toolbar ;
Button loginBtn , registerBtn ;
String email , name ,mobile , password ;
EditText emailEdT ,nameEdT , mobileEdT , passwordEdT ;
Flashbar flashbar =null ;
ProgressDialog progressDialog ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        toolbar = findViewById(R.id.toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.left_arrow);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        if (getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // toolbar.setTitle("Manage Booking");
        }
        setTitle(getString(R.string.create_Account));
    progressDialog = new ProgressDialog(this);
    progressDialog.setCanceledOnTouchOutside(false);

        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        nameEdT = findViewById(R.id.name)  ;
        emailEdT = findViewById(R.id.email);
        mobileEdT = findViewById(R.id.mobile);
        passwordEdT  =findViewById(R.id.password);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashbar = null;

            name = nameEdT.getText().toString()  ;
            email = emailEdT.getText().toString() ;
            mobile = mobileEdT.getText().toString()  ;
            password = passwordEdT.getText().toString();
            if(TextUtils.isEmpty(name)){
                nameEdT.setError("Required");
                return;
            }else if(TextUtils.isEmpty(email)){
                emailEdT.setError("Required");
                return;
            }else if(TextUtils.isEmpty(mobile)){
                mobileEdT.setError("Required");
                return;
            }else if(TextUtils.isEmpty(password)){
                passwordEdT.setError("Required");
                return;
            }else{
                progressDialog.setMessage("Please wait while we setup your profile");
                progressDialog.show();
                register(name , email , mobile , password);
            }

            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginActivity = new Intent(getApplicationContext() , LoginActivity.class);
                loginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginActivity);
                finish();
            }
        });
    }

    private void register(final String name, final String email, final String mobile, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Server.REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register" , response);
                try {
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean error = jsonObject.getBoolean("error");
                    if(error==false){
                        Constant.currentMobile = mobile;
                        Intent otpVerify = new Intent(getApplicationContext() , OtpVerify.class);
                        otpVerify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(otpVerify);
                    }else{
                        String msg = jsonObject.getString("msg");
                        if (flashbar == null){
                            flashbar = showAlertPrimary(msg , "Registration Failed");
                            flashbar.show();
                        }

                       // showAlertPrimary(msg);
                       // Toast.makeText(getApplicationContext()  , ""+msg , Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext() , "Can't connect to server"  , Toast.LENGTH_SHORT) ;

//                Toast.makeText(getApplicationContext()  , ""+error , Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String , String > map = new HashMap<>();
                map.put("name" , name);
                map.put("email" , email);
                map.put("mobile"  , mobile);
                map.put("password"  , password);
             return  map ;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private Flashbar showAlertPrimary(String msg , String Title) {
        return new Flashbar.Builder(this)
                .gravity(Flashbar.Gravity.TOP)
                .message(msg)
                .title(Title)
                .backgroundDrawable(R.drawable.alert_danger)
                .duration(3000)
                .build();
    }


}
