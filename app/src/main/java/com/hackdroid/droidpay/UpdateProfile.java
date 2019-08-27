package com.hackdroid.droidpay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.hackdroid.droidpay.App.Constant;
import com.hackdroid.droidpay.App.CustomAlert;
import com.hackdroid.droidpay.App.CustomLoader;
import com.hackdroid.droidpay.App.Server;
import com.hackdroid.droidpay.App.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {
    TextInputEditText mobile  , email , name;
    SessionManager sessionManager  ;
    Button updateProfile;
    Toolbar toolbar ;
    CustomAlert customAlert ;
    CustomLoader customLoader ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        sessionManager = new SessionManager(getApplicationContext());
        toolbar = findViewById(R.id.toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.left_arrow);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        customAlert = new CustomAlert(this);
        if (getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // toolbar.setTitle("Manage Booking");
        }
        customLoader = new CustomLoader(this , this);
        setTitle(getString(R.string.updateProfile));
        mobile = findViewById(R.id.mobile);
        mobile.setText(sessionManager.getLoggedInMobile());
        name = findViewById(R.id.name);
        name.setText(sessionManager.getLoggedInUser());
        getUser(sessionManager.getLoggedInMobile());
        email = findViewById(R.id.email);
        updateProfile = findViewById(R.id.updateP);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText = email.getText().toString();
                String nameText = name.getText().toString();
                if(TextUtils.isEmpty(emailText) || TextUtils.isEmpty(nameText)){
                    customAlert.showAlertDanger("Required name and email");
                }else{
                    customLoader.showLoader("Please wait...");
                    updateUser(emailText , nameText , sessionManager.getLoggedInMobile());
                }
            }
        });
    }

    private void updateUser(final String emailText, final String nameText, final String loggedInMobile) {
    StringRequest update = new StringRequest(Request.Method.POST, Server.UPDATE_PROFILE, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
        customLoader.closeLoader();
            try {
                JSONObject jsonObject = new JSONObject(response);
                Boolean error = jsonObject.getBoolean("error");
                if(error==true){
                    String msg = jsonObject.getString("msg");
                    customAlert.showAlertDanger(""+msg);
                }else{
                    String names = jsonObject.getString("name");
                    String emails = jsonObject.getString("email");
                    sessionManager.setLoginuser(names);
                    name.setText(names);
                    email.setText(emails);
                    Toast.makeText(getApplicationContext() , "Updated successfully" , Toast.LENGTH_SHORT).show();
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
            map.put("name" , nameText);
            map.put("email" , emailText);
            map.put("user" , loggedInMobile);
            return  map ;
        }
    };
    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
    requestQueue.add(update);
    }

    private void getUser(final String loggedInMobile) {
            customLoader.showLoader("Please wait..");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.USER_USERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response" , response);
                customLoader.closeLoader();
                try {
                    //progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    String names = jsonObject.getString("user_name");
                    String emails = jsonObject.getString("user_email");
                    String cardUid = jsonObject.getString("card_uid");
                    Constant.currentCardUid = cardUid;
                    email.setText(emails);
                    name.setText(names);

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
                HashMap<String , String> map  = new HashMap<>();
                map.put("mobile" ,loggedInMobile);
                return  map;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}
