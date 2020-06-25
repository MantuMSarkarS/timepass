package com.milkyway.timepass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.milkyway.timepass.R;
import com.milkyway.timepass.databinding.ActivitySplashBinding;


public class SplashActivity extends AppCompatActivity {

    private static int mDelayTime=3000;
     ActivitySplashBinding mBinding;
     Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler=new Handler();
        mHandler.postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
            finish();
        },mDelayTime);
    }
}
