package com.moutamid.calenderapp.activities;

import android.os.Bundle;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

import com.moutamid.calenderapp.databinding.ActivityForgotBinding;
import com.moutamid.calenderapp.utilis.Constants;

public class ForgotActivity extends AppCompatActivity {
    ActivityForgotBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Constants.initDialog(this);

        binding.toolbar.title.setText("Password Reset");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        binding.reset.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                Constants.auth().sendPasswordResetEmail(
                        binding.email.getEditText().getText().toString()
                ).addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Constants.createSnackbar(this, binding.getRoot(), "A Password reset mail is sent to your Email Address");
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Constants.createSnackbar(this, binding.getRoot(), e.getLocalizedMessage());
                });
            }
        });


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