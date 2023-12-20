package com.moutamid.calenderapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.moutamid.calenderapp.MainActivity;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.SplashScreenActivity;
import com.moutamid.calenderapp.databinding.ActivityEmailVerifyBinding;
import com.moutamid.calenderapp.models.UserModel;
import com.moutamid.calenderapp.utilis.Constants;

public class EmailVerifyActivity extends AppCompatActivity {
    ActivityEmailVerifyBinding binding;
    String email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.initDialog(this);
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);

        boolean fromSplash = getIntent().getBooleanExtra("fromSplash", false);

        String text = fromSplash ? getString(R.string.didn_t_receive_verification_email) : getString(R.string.verify_your_email);
        binding.verify.setText(text);

        binding.verify.setOnClickListener(v -> {
            Constants.auth().getCurrentUser().sendEmailVerification().addOnSuccessListener(unused -> binding.message.setVisibility(View.VISIBLE));
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        UserModel userModel = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        email = userModel.getEmail();
        password = userModel.getPassword();
        Constants.showDialog();
        Constants.auth().signInWithEmailAndPassword(
                email, password
        ).addOnSuccessListener(authResult -> {
            Constants.dismissDialog();
            if (Constants.auth().getCurrentUser().isEmailVerified()) {
                Toast.makeText(this, getString(R.string.email_verified), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EmailVerifyActivity.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(e -> {
            Constants.dismissDialog();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}