package com.eko.sensortracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private TextView tvHello;
    private Button serviceButton;
    private  Intent intent;
    private final int MY_PERMISSIONS_REQUESTS = 01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUESTS);
        }

        tvHello = (TextView) findViewById(R.id.hello);
        serviceButton = (Button) findViewById(R.id.service_button);
        serviceButton.setText("Activate Sensor Tracking");
        intent = new Intent(MainActivity.this, SensorService.class);
        serviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(SensorService.isRunnning()){
                    stopService(intent);
                    serviceButton.setText("Activate Sensor Tracking");
                }else {
                    startService(intent);
                    serviceButton.setText("Deactivate Sensor Tracking");
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUESTS: {
                // If request is cancelled, the result arrays are empty.
                for (int i =0; i< grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        Toast.makeText(getBaseContext(), "Granted", Toast.LENGTH_SHORT).show();
                    } else {

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                        Toast.makeText(getBaseContext(), "Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
