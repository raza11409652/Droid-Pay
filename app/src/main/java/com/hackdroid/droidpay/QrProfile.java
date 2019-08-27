package com.hackdroid.droidpay;

import androidx.appcompat.app.AppCompatActivity;


import android.graphics.Bitmap;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.hackdroid.droidpay.App.SessionManager;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrProfile extends AppCompatActivity {

String text="";
ImageView imageView ;
TextView userProfileName , textHint ;
SessionManager sessionManager ;
MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_profile);
        sessionManager = new SessionManager(getApplicationContext());
        text=sessionManager.getLoggedInMobile();
        userProfileName = findViewById(R.id.userProfileName);
        textHint = findViewById(R.id.hint);
        userProfileName.setText(sessionManager.getLoggedInUser());
        textHint.setText(getString(R.string.show_qr)+" "+ sessionManager.getLoggedInUser());
            imageView = findViewById(R.id.qrCodeHolder);
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(text , BarcodeFormat.QR_CODE , 512 , 512 );
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder() ;
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
