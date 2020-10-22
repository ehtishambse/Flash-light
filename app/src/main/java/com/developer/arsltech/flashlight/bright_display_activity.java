package com.developer.arsltech.flashlight;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.w3c.dom.Text;

import yuku.ambilwarna.AmbilWarnaDialog;

public class bright_display_activity extends AppCompatActivity {

    private RelativeLayout myLayout;
    private TextView myText;
    private int currentColor;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bright_display_activity);


        myLayout = (RelativeLayout) findViewById(R.id.bright_display_holder);
        myText = (TextView) findViewById(R.id.bright_display_change_color);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8863088720647115/4642102809");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);

        }

        myLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(false);
            }
        });

    }
    private void openDialog(boolean supportsAlpha) {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, currentColor, supportsAlpha, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                currentColor = color;
                myLayout.setBackgroundColor(color);
                myText.setTextColor(color);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    Window window = getWindow();
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(color);

                }
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    finish();
                }
            });
        }else{
            super.onBackPressed();
        }}


    }

