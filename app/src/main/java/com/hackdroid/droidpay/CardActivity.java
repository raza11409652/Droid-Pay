package com.hackdroid.droidpay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hackdroid.droidpay.App.Constant;
import com.hackdroid.droidpay.App.Server;
import com.hackdroid.droidpay.App.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CardActivity extends AppCompatActivity {
Toolbar toolbar ;
ProgressDialog progressDialog , changePinProgress ;
TextView cardNumber , userProfileName , cardStartDate ;
Switch cardStatus;
SessionManager sessionManager ;
String userMobile ;
TextView alertCard ;
final Context context = this ;
Button changePin ;
Flashbar flashbar = null;
Button closeBottomSheet , changePinBtn  ,generatePin ;
AlertDialog.Builder showNewPinAlert ;
AlertDialog alertDialogShoNewPin;
    BottomSheetBehavior bottomSheetBehavior ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        toolbar = findViewById(R.id.toolbar);
        //showNewPinAlert =new AlertDialog.Builder(getApplicationContext());
        final Drawable upArrow = getResources().getDrawable(R.drawable.left_arrow);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        if (getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // toolbar.setTitle("Manage Booking");
        }
        sessionManager = new SessionManager(getApplicationContext());
        userMobile =sessionManager.getLoggedInMobile() ;
        cardStatus = findViewById(R.id.cardStatus);
        cardNumber = findViewById(R.id.cardNumber);
        cardStartDate = findViewById(R.id.cardStartDate);
        userProfileName = findViewById(R.id.userProfileName);
        alertCard = findViewById(R.id.alertCard);
        changePin = findViewById(R.id.changePin);
        changePinBtn = findViewById(R.id.changePinBtn);
        generatePin = findViewById(R.id.generatePin);
       // cardNumber.setText(Constant.currentCardNumber);
       // cardStartDate.setText(Constant.currentCardDate);
        userProfileName.setText(sessionManager.getLoggedInUser());
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        changePinProgress = new ProgressDialog(this);
        changePinProgress.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait while we fetch your payment details");
        progressDialog.show();
        fetchUserData(userMobile);
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        closeBottomSheet = findViewById(R.id.closeBottomSheet);
        // init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        //BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        // change the state of the bottom sheet
       // bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
     //   bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
       bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

// set the peek height
       // bottomSheetBehavior.setPeekHeight(120);

// set hideable or not
       // bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        closeBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        changePinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {flashbar = null;
                final  EditText oldPin = findViewById(R.id.oldPinInput);
                final  EditText newPin = findViewById(R.id.newPinInput);
                String oldPinStr = oldPin.getText().toString();
                String newPinStr  = newPin.getText().toString();
                if(TextUtils.isEmpty(oldPinStr)||TextUtils.isEmpty(newPinStr)){
                    flashbar = alertDanger("PIN Required for changing your old pin");
                    flashbar.show();
                    return;
                }else if(oldPinStr.equals(newPinStr)){
                    flashbar = alertDanger("PIN can't be same");
                    flashbar.show();
                    return;
                }else {
                    changePinProgress.setMessage("Please wait while we update your PIN");
                    changePinProgress.show();
                    updatePin(Constant.currentCardUid , oldPinStr , newPinStr);
                }
            }
        });
        cardStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if(b==true){
                   updateCardStatus("1");
                   alertCard.setVisibility(View.GONE);
               }else{
                   updateCardStatus("0");
                   alertCard.setVisibility(View.VISIBLE);
               }
            }
        });
    changePin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Boolean status = cardStatus.isChecked();
            if (Constant.currentCardUid!=null && status){
               // startPinChange(Constant.currentCardUid);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }else{
                flashbar = null ;
                flashbar = alertDanger("Please unblock your card first");
                flashbar.show();
            }
        }
    });
    generatePin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Boolean status = cardStatus.isChecked();

            if (status){
                progressDialog.setMessage("Please wait while system is generating new PIN");
                progressDialog.show();
                generateNewPin(Constant.currentCardUid);
            }else{
                flashbar = null ;
                flashbar = alertDanger("Please unblock your card first");
                flashbar.show();
            }
        }
    });
    }
    private void  generateNewPin(final String card){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.GENERATE_NEW_PIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("res" , response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean error = jsonObject.getBoolean("error");
                    if(error == false){
                        String newPinStr = jsonObject.getString("newPin");
                        showNewPinAlert  = new AlertDialog.Builder(context);
                        LayoutInflater li = LayoutInflater.from(context);
                        View promptsView = li.inflate(R.layout.new_pin_holder, null);
                        TextView newPinHolder  = promptsView.findViewById(R.id.newPin);
                        newPinHolder.setText(newPinStr);
                        showNewPinAlert.setView(promptsView);
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        showNewPinAlert.setCancelable(false)
                                //   .setMessage("HEllo")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        alertDialogShoNewPin = showNewPinAlert.create();
                        alertDialogShoNewPin.show();
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
                map.put("card"  ,card);
                return map  ;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private void updatePin(final String currentCardUid, final String oldPinStr, final String newPinStr) {
        flashbar = null;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.CARD_PIN_UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            Log.d("ers" , response);
                try {
                    if (changePinProgress.isShowing()){
                        changePinProgress.dismiss();
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean error = jsonObject.getBoolean("error");
                    String msg =jsonObject.getString("msg");
                    if(error==true){
                        flashbar = alertDanger(""+msg);
                        flashbar.show();
                    }else if (error==false){

                        flashbar = alertSuccess(""+msg);
                        flashbar.show();
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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
                HashMap<String , String> map =new HashMap<>();
                map.put("cardUid" , currentCardUid);
                map.put("oldPin" , oldPinStr);
                map.put("newPin" , newPinStr);
                return  map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void startPinChange(String currentCardUid) {
// get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.pinchangeprompt, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(promptsView);
        //final EditText oldPinUserInput = (EditText) promptsView.findViewById(R.id.oldPin);
       // final EditText newPinUserInput = (EditText) promptsView
               // .findViewById(R.id.newPin);
                alert.setCancelable(false)
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        flashbar = null;
                      // String oldPin  = oldPinUserInput.getText().toString();
                      // String newPin = newPinUserInput.getText().toString();
                     /*  if(TextUtils.isEmpty(oldPin) || TextUtils.isEmpty(newPin)){
                          flashbar =  alertDanger("PIN required");
                          flashbar .show();
                           return;
                       }
                        else if (oldPin.equalsIgnoreCase(newPin)){
                            //
                         flashbar =    alertDanger("PIN can't be same");

                         flashbar.show();
                        }else{
                            // Toast.makeText(getApplicationContext() , ""+i , Toast.LENGTH_SHORT).show();
                            changePinProgress.setMessage("Please wait while pin is being changed");
                            changePinProgress.show();
                        }
*/
                    }
                })
                .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alert
                .create();

        // show it
       // alertDialog.show();
    }

    private void updateCardStatus(final String b) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Server.CARD_STATUS_UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String , String> map = new HashMap<>();
                map.put("status" ,b );
                map.put("card" , Constant.currentCardUid);
                return  map ;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void fetchUserData(final String userMobile) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.USER_USERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response" , response);
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    String cardNumberStr =jsonObject.getString("card_number");
                    String cardStartDateStr= jsonObject.getString("card_date");
                   Boolean cardStatusBool = jsonObject.getBoolean("card_status");
                   if (cardStatusBool==true){
                       cardStatus.setChecked(true);
                   }else{
                       alertCard.setVisibility(View.VISIBLE);
                       cardStatus.setChecked(false);
                   }
                    String cardUid = jsonObject.getString("card_uid");
                   Constant.currentCardUid = cardUid;

                   cardNumber.setText(cardNumberStr);
                   cardStartDate.setText("From-"+cardStartDateStr);
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
                map.put("mobile" , userMobile);
                return  map;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private Flashbar alertDanger(String msg){
        return   new Flashbar.Builder(this)
                .gravity(Flashbar.Gravity.TOP)
                .message(msg)
                .title("Warning")
                .backgroundDrawable(R.drawable.alert_danger)
                .duration(3000)
                .build();
    }private Flashbar alertSuccess(String msg){
        return   new Flashbar.Builder(this)
                .gravity(Flashbar.Gravity.TOP)
                .message(msg)
                .title("Done!!")
                .backgroundColorRes(R.color.colorPrimaryDark)
                .duration(3000)
                .build();
    }
}
