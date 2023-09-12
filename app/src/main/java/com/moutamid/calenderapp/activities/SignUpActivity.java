package com.moutamid.calenderapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;

import com.fxn.stash.Stash;
import com.moutamid.calenderapp.MainActivity;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.databinding.ActivitySignUpBinding;
import com.moutamid.calenderapp.models.UserModel;
import com.moutamid.calenderapp.utilis.Constants;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Constants.initDialog(this);

        binding.toolbar.title.setText("Create Account");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        email = getIntent().getStringExtra("email");

        binding.email.getEditText().setText(email);

        binding.create.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                createAccount();
            }
        });

    }

    private void createAccount() {
        Constants.auth().createUserWithEmailAndPassword(
                        binding.email.getEditText().getText().toString(),
                        binding.password.getEditText().getText().toString()
                )
                .addOnSuccessListener(authResult -> {
                    UserModel userModel = new UserModel(
                            authResult.getUser().getUid(),
                            binding.username.getEditText().getText().toString(),
                            binding.email.getEditText().getText().toString(),
                            binding.password.getEditText().getText().toString(), "", "", ""
                    );
                    Stash.put(Constants.USERNAME, binding.username.getEditText().getText().toString().trim());
                    Stash.put(Constants.EMAIL, binding.email.getEditText().getText().toString().trim());
                    Constants.databaseReference().child(Constants.USER).child(Constants.auth().getCurrentUser().getUid())
                            .setValue(userModel)
                            .addOnSuccessListener(unused -> {
                                Constants.dismissDialog();
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            }).addOnFailureListener(e -> {
                                Constants.dismissDialog();
                                Constants.createSnackbar(this, binding.getRoot(), e.getLocalizedMessage());
                            });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Constants.createSnackbar(this, binding.getRoot(), e.getLocalizedMessage());
                });
    }

    private boolean valid() {
        if (binding.username.getEditText().getText().toString().isEmpty()) {
            binding.username.setErrorEnabled(true);
            binding.username.setError("Name is Empty");
            return false;
        }
        if (binding.email.getEditText().getText().toString().isEmpty()) {
            binding.email.setErrorEnabled(true);
            binding.email.setError("Email is Empty");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getEditText().getText().toString()).matches()) {
            binding.email.setErrorEnabled(true);
            binding.email.setError("Email is not valid");
            return false;
        }
        if (binding.password.getEditText().getText().toString().isEmpty()) {
            binding.password.setErrorEnabled(true);
            binding.password.setError("Password is Empty");
            return false;
        }
        return true;
    }
}