package com.developer.arsltech.flashlight;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;

public class Police_strobe extends AppCompatActivity {

    private MediaPlayer player;
    private ImageView imageV;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_strobe);



        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window win=getWindow();
            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }

        player=MediaPlayer.create(this,R.raw.police_siren);
        player.start();
        player.setLooping(true);



        imageV = (ImageView) findViewById(R.id.imageView);
        ObjectAnimator anim=ObjectAnimator.ofInt(imageV,"BackgroundColor", Color.RED,Color.BLUE);

        anim.setDuration(120);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        anim.start();
    }


    @Override
    public void onBackPressed() {

        player.stop();
        super.onBackPressed();
    }
}
