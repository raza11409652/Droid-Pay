package com.hackdroid.droidpay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hackdroid.droidpay.App.Constant;
import com.hackdroid.droidpay.App.Server;
import com.hackdroid.droidpay.App.SessionManager;
import com.hackdroid.droidpay.App.TransactionAdapter;
import com.hackdroid.droidpay.Model.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Passbook extends AppCompatActivity {
    Toolbar toolbar;
    TextView amount ;
    BottomSheetDialog bottomSheetDialogCal  ;
    Calendar calendar = Calendar.getInstance();
    Date Today = new Date();
    String dateSelected = null ;
    CalendarView simpleCalendarView =null;
    SessionManager sessionManager ;
    RecyclerView transactionList ;
    List<Transaction> transactions = new ArrayList<>();
    LinearLayout noTransactionFoundLayout ;
    TextView date  ;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passbook);
        toolbar = findViewById(R.id.toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.left_arrow);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        if (getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // toolbar.setTitle("Manage Booking");
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        sessionManager = new SessionManager(this);
        setTitle("Passbook");
        amount = findViewById(R.id.amount);
        amount.setText(Constant.WALLET_AMOUNT);
        bottomSheetDialogCal  = new BottomSheetDialog(this , R.style.SheetDialog);
        bottomSheetDialogCal.setContentView(R.layout.custom_calender);
        if (dateSelected==null){
            dateSelected = "1";
        }
        date = findViewById(R.id.date);
        noTransactionFoundLayout = findViewById(R.id.noTransactionFoundLayout);
        transactionList = findViewById(R.id.transactionList);
        transactionList.setHasFixedSize(true);
        fetchPassBookTransaction(dateSelected);
    }

    private void fetchPassBookTransaction(final String dateSelected) {
        progressDialog.setMessage("Loading...");
        progressDialog.show();
            transactions     = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Server.TRANSACTION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("res"  ,response);
                try {
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean error=jsonObject.getBoolean("error");
                    String amounts = jsonObject.getString("wallet");
                    Constant.WALLET_AMOUNT = amounts;
                    amount.setText(Constant.WALLET_AMOUNT);
                    if (error==false){
                        JSONArray jsonArray = jsonObject.getJSONArray("records");
                       if (jsonArray.length()>0){
                           transactionList.setVisibility(View.VISIBLE);
                           noTransactionFoundLayout.setVisibility(View.GONE);
                           for (int i=0;i<jsonArray.length() ;i++){
                                Transaction transaction = new Transaction();
                                JSONObject transactionObject = jsonArray.getJSONObject(i);
                                Log.d("d",""+transactionObject);
                                transaction.setAmount(transactionObject.getString("amount"));
                                transaction.setDate(transactionObject.getString("date"));
                                transaction.setUid(transactionObject.getString("uid"));
                                transaction.setSrc(transactionObject.getString("src"));
                                transaction.setType(transactionObject.getString("type"));
                                transactions.add(transaction);
                           }
                           setAdapter(transactions);
                       }else{
                           transactionList.setVisibility(View.GONE);
                           noTransactionFoundLayout.setVisibility(View.VISIBLE);
                       }
                    }else{
                        transactionList.setVisibility(View.GONE);
                        noTransactionFoundLayout.setVisibility(View.VISIBLE);
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
                HashMap<String, String>map = new HashMap<>();
                map.put("date" , dateSelected);
                map.put("user" , sessionManager.getLoggedInMobile());
                return  map ;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(stringRequest);
    }

    private void setAdapter(List<Transaction> transactions) {
        TransactionAdapter adapter = new TransactionAdapter(getApplicationContext() , transactions);
        transactionList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        transactionList.setAdapter(adapter);
        transactionList.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.passbook_filter_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.reload) {
           // Toast.makeText(getApplicationContext()  , "reload" , Toast.LENGTH_SHORT).show();
            dateSelected = "1" ;
            fetchPassBookTransaction(dateSelected);
            calendar = Calendar.getInstance();
            if (simpleCalendarView!=null){
                simpleCalendarView.setDate(calendar.getTimeInMillis());
            }
        }else if(item.getItemId()==R.id.date){
           // Toast.makeText(getApplicationContext()  , "date" , Toast.LENGTH_SHORT).show();
            bottomSheetDialogCal.show();
           simpleCalendarView = (CalendarView) bottomSheetDialogCal. findViewById(R.id.cal);
            simpleCalendarView.setMaxDate(Today.getTime());
            simpleCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                    calendar.set(Calendar.YEAR , year);
                    calendar.set(Calendar.MONTH , month);
                    calendar.set(Calendar.DAY_OF_MONTH , dayOfMonth);

                    month = month+1;
                    String Month = null , Day = null;
                    if (month<10){
                        Month="0"+month;
                    }else{
                        Month =String.valueOf( month);
                    }
                    if (dayOfMonth<10){
                        Day = "0"+dayOfMonth;
                    }else{
                        Day = String.valueOf(dayOfMonth);
                    }
                    dateSelected = year+"-"+Month+"-"+Day;
                    date.setText(dateSelected);
                    //Toast.makeText(getApplicationContext() , ""+dateSelected  , Toast.LENGTH_SHORT).show();
                    simpleCalendarView.setDate(calendar.getTimeInMillis());
                    fetchPassBookTransaction(dateSelected);
                    bottomSheetDialogCal.dismiss();
                }
            });

        }
        return super.onOptionsItemSelected(item);
    }
}
