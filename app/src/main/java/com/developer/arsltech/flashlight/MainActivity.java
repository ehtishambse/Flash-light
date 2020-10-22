package com.developer.arsltech.flashlight;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btn_switch;
    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    private Camera.Parameters params;
    private AdView mAdView;
    private TextView myBatteryLevelText;
    private Button jmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this,"ca-app-pub-8863088720647115~3028594651");
        btn_switch=(Button)findViewById(R.id.buttonSwitch);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        myBatteryLevelText = (TextView)findViewById(R.id.batteryLevel);

        jmp = (Button) findViewById(R.id.jmpBTN);

        jmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final android.support.v7.app.AlertDialog.Builder alert  = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                View mview= getLayoutInflater().inflate(R.layout.dialog_counter,null);

                alert.setView(mview);
                final android.support.v7.app.AlertDialog alertDialog =alert.create();
                final TextView rateText=(TextView) mview.findViewById(R.id.txt_rate);
                final TextView moreAppsText = (TextView) mview.findViewById(R.id.txt_moreAPps);

                rateText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try{

                            Uri uri = Uri.parse("market://details?id="+getPackageName());
                            Intent gotoMarket = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(gotoMarket);
                        }
                        catch (ActivityNotFoundException e) {

                            Uri uri = Uri.parse("http://play.google.com/store/apps/details?id="+getPackageName());
                            Intent gotoMarket = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(gotoMarket);
                        }
                    }
                });

                moreAppsText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try{
                            Uri uri = Uri.parse("market://search?q=pub:ArslTech");
                            Intent gotoMarket = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(gotoMarket);
                        }
                        catch (ActivityNotFoundException e) {

                            Uri uri = Uri.parse("http://play.google.com/store/search?q=pub:ArslTech");
                            Intent gotoMarket = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(gotoMarket);
                        }

                    }
                });

                alertDialog.show();


            }
        });
        getBattery_percentage();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        functionsMain();
                        updateTime("1000");
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();


    }


    public void functionsMain(){

        //First check if device is supporting flashlight or not

        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            });
            alert.show();
            return;
        }


        btn_switch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    // turn off flash
                    turnOffFlash();
                } else {
                    // turn on flash
                    turnOnFlash();
                }


            }
        });
        // get the camera
        getCamera();

        // displaying button image
        toggleButtonImage();
    }

    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {

            }
        }
    }


    // Turning On flash

    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;

            // changing button/switch image
            toggleButtonImage();
        }

    }


    // Turning Off flash

    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;

            // changing button/switch image
            toggleButtonImage();
        }
    }



    private void toggleButtonImage() {
        if (isFlashOn) {
            btn_switch.setBackgroundResource(R.drawable.onbutton);
        } else {
            btn_switch.setBackgroundResource(R.drawable.offbutton);
        }
    }




    public void jumBTN(View view) {
        startActivity(new Intent(getApplicationContext(),bright_display_activity.class));
    }

    void getBattery_percentage()
    {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float)scale;
        float p = batteryPct * 100;

        myBatteryLevelText.setText("Battery: "+String.valueOf(Math.round(p))+"%");
    }

    Runnable updater;
    void updateTime(final String timeString) {
        final Handler timerHandler = new Handler();

        updater = new Runnable() {
            @Override
            public void run() {
                getBattery_percentage();
                timerHandler.postDelayed(updater,1000);
            }
        };
        timerHandler.post(updater);
    }

    public void jumpSiren(View view) {
        if (isFlashOn) {
            // turn off flash
            turnOffFlash();
        }
        startActivity(new Intent(getApplicationContext(),Police_strobe.class));
    }



}
