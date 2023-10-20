package com.moutamid.calenderapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.moutamid.calenderapp.activities.EmailVerifyActivity;
import com.moutamid.calenderapp.activities.WelcomeActivity;
import com.moutamid.calenderapp.utilis.Constants;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            if (Constants.auth().getCurrentUser() != null) {
                if (Constants.auth().getCurrentUser().isEmailVerified()) {
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, EmailVerifyActivity.class).putExtra("fromSplash", true));
                    finish();
                }
            } else {
                startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
                finish();
            }
        }, 2000);

    }
}