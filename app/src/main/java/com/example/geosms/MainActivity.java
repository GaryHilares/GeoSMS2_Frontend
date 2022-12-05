package com.example.geosms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView view;
    Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* (Imitate default behavior) Open main activity */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Get UI elements */
        view = (TextView) findViewById(R.id.logTextView);
        logger = new Logger(view);

        /* Register receiver if permission is granted */
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS },111);
        }
        else{
            this.registerReceiver();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        /* (Imitate default behavior) */
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /* Register if permission was granted */
        boolean permissionGranted = requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        Toast.makeText(getApplicationContext(), permissionGranted ? "Request accepted" : "Request declined", Toast.LENGTH_SHORT).show();

        /* If permission was granted, register broadcaster */
        if (permissionGranted) {
            this.registerReceiver();
        }
    }

    public void registerReceiver()
    {
        /* Create broadcast receiver */
        BroadcastReceiver br = new MyBroadCastReceiver(logger);

        /* Create filter */
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");

        /* Register broadcast receiver to filter */
        this.registerReceiver(br, filter);
    }
}