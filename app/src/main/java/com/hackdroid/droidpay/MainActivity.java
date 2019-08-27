package com.hackdroid.droidpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

public class MainActivity extends AppCompatActivity {
    Context context ;
    SessionManager sessionManager ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        sessionManager = new SessionManager(context);
        if (sessionManager.isLoggedIn()){
            getWallet(sessionManager.getLoggedInMobile());
        }else{
            Intent dash = new Intent(context , LoginActivity.class);
            startActivity(dash);
            finish();
        }

    }
    private void getWallet(final String user) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.GET_WALLET, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               // customLoader.closeLoader();
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
}
