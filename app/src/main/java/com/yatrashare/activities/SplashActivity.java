package com.yatrashare.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yatrashare.R;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DISPLAY_LENGTH = 2000;
    private Runnable runnable;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try {

            runnable =  new Runnable() {
                @Override
                public void run() {
                /* Create an Intent that will start the Home Activity. */
                    Intent mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
                    overridePendingTransition(R.anim.jump_to_down, R.anim.jump_from_down);
                    startActivity(mainIntent);
                    finish();
                }
            };
            handler = new Handler();
            handler.postDelayed(runnable, SPLASH_DISPLAY_LENGTH);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            handler.removeCallbacks(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
