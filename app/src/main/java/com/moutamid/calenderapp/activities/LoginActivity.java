package com.moutamid.calenderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

import com.moutamid.calenderapp.MainActivity;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.SplashScreenActivity;
import com.moutamid.calenderapp.databinding.ActivityLoginBinding;
import com.moutamid.calenderapp.utilis.Constants;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Constants.initDialog(this);

        binding.toolbar.title.setText(getString(R.string.login));
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        binding.forgot.setOnClickListener(v -> startActivity(new Intent(this, ForgotActivity.class)));
        binding.login.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                LoginAccount();
            }
        });
    }

    private void LoginAccount() {
        Constants.auth().signInWithEmailAndPassword(
                        binding.email.getEditText().getText().toString(),
                        binding.password.getEditText().getText().toString()
                )
                .addOnSuccessListener(authResult -> {
                    Constants.dismissDialog();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
//                    if (Constants.auth().getCurrentUser().isEmailVerified()) {
//                        startActivity(new Intent(this, MainActivity.class));
//                        finish();
//                    } else {
//                        startActivity(new Intent(this, EmailVerifyActivity.class).putExtra("fromSplash", false));
//                        finish();
//                    }
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Log.d("LoginAccount", e.getLocalizedMessage());
                    Constants.createSnackbar(this, binding.getRoot(), e.getLocalizedMessage());
                });
    }

    private boolean valid() {
        if (binding.email.getEditText().getText().toString().isEmpty()) {
            binding.email.setErrorEnabled(true);
            binding.email.setError(getString(R.string.email_is_empty));
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getEditText().getText().toString()).matches()) {
            binding.email.setErrorEnabled(true);
            binding.email.setError(getString(R.string.email_is_not_valid));
            return false;
        }
        if (binding.password.getEditText().getText().toString().isEmpty()) {
            binding.password.setErrorEnabled(true);
            binding.password.setError(getString(R.string.password_is_empty));
            return false;
        }
        return true;
    }
}