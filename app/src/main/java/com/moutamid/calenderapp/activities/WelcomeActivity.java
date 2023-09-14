package com.moutamid.calenderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;

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

        binding.explore.setOnClickListener(v -> {
          //  startActivity(new Intent(this, MainActivity.class));
            if (valid()) {
                startActivity(new Intent(this, SignUpActivity.class).putExtra("email", binding.email.getEditText().getText().toString()));
            }
        });

        binding.login.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

    }

    private boolean valid() {
        if (binding.email.getEditText().getText().toString().isEmpty()){
            binding.email.setErrorEnabled(true);
            binding.email.setError("Email is Empty");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getEditText().getText().toString()).matches()){
            binding.email.setErrorEnabled(true);
            binding.email.setError("Email is not valid");
            return false;
        }
        return true;
    }
}