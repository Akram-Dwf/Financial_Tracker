package com.example.dapurmoms;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Show Android 12+ Splash Screen first
        SplashScreen.installSplashScreen(this);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Simple fade-in animation for our custom UI
        android.widget.ImageView logo = findViewById(R.id.splash_logo);
        android.widget.TextView title = findViewById(R.id.splash_title);
        android.widget.TextView subtitle = findViewById(R.id.splash_subtitle);
        
        logo.setAlpha(0f);
        logo.setScaleX(0.8f);
        logo.setScaleY(0.8f);
        logo.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(800).setInterpolator(new android.view.animation.OvershootInterpolator()).start();

        title.setAlpha(0f);
        title.setTranslationY(40f);
        title.animate().alpha(1f).translationY(0f).setDuration(800).setStartDelay(200).setInterpolator(new android.view.animation.DecelerateInterpolator()).start();

        subtitle.setAlpha(0f);
        subtitle.setTranslationY(40f);
        subtitle.animate().alpha(0.8f).translationY(0f).setDuration(800).setStartDelay(300).setInterpolator(new android.view.animation.DecelerateInterpolator()).start();

        // Delay and navigate to MainActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 1500);
    }
}
