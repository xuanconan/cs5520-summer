package edu.neu.madcourse.kexuan;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActivityAbout extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private Button buttonClose;
    private TextView uniqueId;
    private String IMEI;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Ke Xuan");
        setContentView(R.layout.activity_about);

        buttonClose = (Button) findViewById(R.id.buttonClose);
        // close and return to main page
        buttonClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

        uniqueId = (TextView) findViewById(R.id.uniqueId);
        // ask for permission and find IMEI id
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = tm.getDeviceId();
            if (IMEI != null) {
                uniqueId.setText("IMEI Number: " + IMEI);
            } else {
                uniqueId.setText("IMEI not available");
            }
        }

    }

}
