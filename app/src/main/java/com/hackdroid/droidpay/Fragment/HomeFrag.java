package com.hackdroid.droidpay.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hackdroid.droidpay.AddMoney;
import com.hackdroid.droidpay.App.Constant;
import com.hackdroid.droidpay.App.CustomAlert;
import com.hackdroid.droidpay.CardActivity;
import com.hackdroid.droidpay.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFrag extends Fragment {
View view ;
TextView addMoney  , walletAmount;
    public HomeFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      view =  inflater.inflate(R.layout.fragment_home, container, false);
      CustomAlert customAlert = new CustomAlert(getContext());
      addMoney = view.findViewById(R.id.addMoney);
      walletAmount = view.findViewById(R.id.amountBal);
      walletAmount.setText(getString(R.string.rs)+" "+Constant.WALLET_AMOUNT);
      addMoney.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent addMoneyActivity = new Intent(getContext() , AddMoney.class);
              addMoneyActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              startActivity(addMoneyActivity);

          }
      });
    //  customAlert.showAlertDanger("Aert");
      return  view;
    }

}
