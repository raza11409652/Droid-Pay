package com.hackdroid.droidpay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class AddMoney extends AppCompatActivity {
Toolbar toolbar;
Button add ;
EditText amountToBeAdded ;
String amount ;
SessionManager sessionManager ;
ProgressBar loader ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
        toolbar = findViewById(R.id.toolbar);
       final Drawable upArrow = getResources().getDrawable(R.drawable.left_arrow);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        if (getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        sessionManager = new SessionManager(getApplicationContext());
        setTitle(getString(R.string.add));
        add  =findViewById(R.id.add);
        loader = findViewById(R.id.loader);
        amountToBeAdded = findViewById(R.id.amount_to_be_added);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = amountToBeAdded.getText().toString() ;
                if (TextUtils.isEmpty(amount)){
                    amountToBeAdded.setError("Required");
                    return;
                }else{
                    Double am = Double.valueOf(amount);
                    if (am<1.00){
                        amountToBeAdded.setError("Error Amount");
                        return;

                    }else if(am>10000.00){
                        amountToBeAdded.setError("Error Amount");
                        return;
                    }
                    else{
                        if (isConnectionAvailable(getApplicationContext())){
                            loader.setVisibility(View.VISIBLE);
                            addMoneyInit(amount , sessionManager.getLoggedInMobile());
                            Constant.amountToBeAdded = amount ;
                        }else{
                            Toast.makeText(getApplicationContext() , "Internet not available" , Toast.LENGTH_SHORT).show();
                        }

                      //  Intent paymentSelector = new Intent(getApplicationContext() , PaymentSelector.class);
                        //paymentSelector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //startActivity(paymentSelector);
                    }

                }

            }
        });
    }

    private void addMoneyInit(final String amount, final String loggedInMobile) {
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
                        Constant.amountToBeAdded = amount ;
                        loader.setVisibility(View.GONE);
                        Intent paymentSelector = new Intent(getApplicationContext() , PaymentSelector.class);
                        paymentSelector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(paymentSelector);
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
                map.put("amount" , amount);
                map.put("user" , loggedInMobile);
                return map ;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
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
}
