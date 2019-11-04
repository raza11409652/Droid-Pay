package com.hackdroid.droidpay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;

public class CardRegistration extends AppCompatActivity {
    Toolbar toolbar ;
    String mobile = Constant.currentMobile ;
    Flashbar flashbar  = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_registration);
        toolbar = findViewById(R.id.toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.left_arrow);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        if (getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // toolbar.setTitle("Manage Booking");
        }
        setTitle(getString(R.string.card_rfid));
      new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
              cardRegistration(mobile);
          }
      } , 5000);
    }

    private void cardRegistration(final String mobile) {
        flashbar = null ;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.CARD_REGISTRATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("errorCard" , response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean error = jsonObject.getBoolean("error");
                    if(error==true){

                        String msg = jsonObject.getString("msg");
                        flashbar =  primaryActionListener(""+msg);
                        flashbar.show();
                    }else{
//                        ActivateUser(mobile);
                        Intent intent = new Intent(getApplicationContext() , CardActivity.class) ;
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
                        startActivity(intent);
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
                map.put("mobile" , mobile);
                return  map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void ActivateUser(final String mobile) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Server.USER_ACTIVATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean error = jsonObject.getBoolean("error");
                    if(error == false){
                        Toast.makeText(getApplicationContext() , "Please Login to your account"  , Toast.LENGTH_SHORT).show();
                        Intent dash = new Intent(getApplicationContext()  , LoginActivity.class);
                        dash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
                        startActivity(dash);
                        finish();
                    }else{
                        String msg = jsonObject.getString("msg") ;
                        primaryActionListener(msg);
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
                map.put("mobile" , mobile);
                return  map;
            }
        } ;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private Flashbar primaryActionListener(String msg) {
        return new Flashbar.Builder(this)
                .gravity(Flashbar.Gravity.BOTTOM)
                .title("Alert")
                .message(msg)
                .backgroundColorRes(R.color.danger_grad_start)
                .primaryActionText("RETRY")
                .primaryActionTextColor(R.color.black)
                .primaryActionTapListener(new Flashbar.OnActionTapListener() {
                    @Override
                    public void onActionTapped(Flashbar bar) {
                        bar.dismiss();
                        cardRegistration(mobile);
                    }
                })
                .build();
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
