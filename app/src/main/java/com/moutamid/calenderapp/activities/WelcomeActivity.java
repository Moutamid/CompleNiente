package com.moutamid.calenderapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.moutamid.calenderapp.databinding.ActivityWelcomeBinding;
import com.moutamid.calenderapp.utilis.Constants;

public class WelcomeActivity extends AppCompatActivity {
    ActivityWelcomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);

        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);

        binding.explore.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });

        binding.login.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

    }

}