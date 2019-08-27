package com.hackdroid.droidpay.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hackdroid.droidpay.App.Constant;
import com.hackdroid.droidpay.App.CustomAlert;
import com.hackdroid.droidpay.App.CustomLoader;
import com.hackdroid.droidpay.App.Server;
import com.hackdroid.droidpay.App.SessionManager;
import com.hackdroid.droidpay.BarCodeCapture;
import com.hackdroid.droidpay.PaymentActivity;
import com.hackdroid.droidpay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.androidhive.barcode.BarcodeReader;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScanFrag extends Fragment {
View view ;
ImageButton scanBtn ;
Button payBtn ;
EditText recieverMobileNumber ;
String recieverMobileNo   , senderMobileNo;
CustomAlert customAlert;
CustomLoader customLoader ;
SessionManager sessionManager ;
    private static final int BAR_CODE_CAPTURE=222;
    public ScanFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      view =  inflater.inflate(R.layout.fragment_scan, container, false);
      customAlert = new CustomAlert(getContext());
      customLoader = new CustomLoader(getContext() , getActivity());
      sessionManager = new SessionManager( getContext());
      senderMobileNo = sessionManager.getLoggedInMobile();
      recieverMobileNumber = view.findViewById(R.id.recieverMobileNumber);
      payBtn = view.findViewById(R.id.payBtn);
      scanBtn = view.findViewById(R.id.scanBtn);
      scanBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent barcodeReader = new Intent(getContext() , BarCodeCapture.class);
              startActivityForResult(barcodeReader , BAR_CODE_CAPTURE , null);
          }
      });
      payBtn .setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              recieverMobileNo = recieverMobileNumber.getText().toString();
              if (TextUtils.isEmpty(recieverMobileNo)){
                  customAlert.showAlertDanger("Mobile number is required");
                  return;
              }else if(recieverMobileNo.equalsIgnoreCase(senderMobileNo)){
                  customAlert.showAlertDanger("You can't transfer money to your self");
                  return;
              }
              else{
                startPayment(recieverMobileNo ,senderMobileNo );
              }
          }
      });
      return  view ;
    }

    private void startPayment(final String recieverMobileNo, final String senderMobileNo) {
        customLoader.showLoader("Please wait while we validate +91-"+recieverMobileNo);
        //customLoader.closeLoader();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Server.PAYMENT_INIT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("res" ,response);
                customLoader.closeLoader();
                try {
                    JSONObject  jsonObject = new JSONObject(response);
                    Boolean error = jsonObject.getBoolean("error");
                    String msg = jsonObject.getString("msg");
                    if (error ==true){
                        customAlert.showAlertDanger(""+msg);
                    }else if (error==false){
                        String name = jsonObject.getString("name");
                        Constant.PAYMENT_TO_NAME = name;
                        Constant.PAYMENT_TO_MOBILE = recieverMobileNo ;
                        Intent pay = new Intent(getContext() , PaymentActivity.class);
                        pay.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(pay);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
           // customLoader.dismiss();
                customLoader.closeLoader();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String , String>map = new HashMap<>();
                map.put("sender" , senderMobileNo);
                map.put("receiver"  ,recieverMobileNo);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case   BAR_CODE_CAPTURE:
                    // Toast.makeText(getApplicationContext() , ""+data  , Toast.LENGTH_SHORT).show();
                    String user = data.getStringExtra("useruid");
                    if(user !=null){
                       startPayment(user , senderMobileNo);
                    }
                    // Toast.makeText(getApplication() , bookingNo   ,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
