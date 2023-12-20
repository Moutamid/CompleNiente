package com.moutamid.calenderapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.fxn.stash.Stash;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.SplashScreenActivity;
import com.moutamid.calenderapp.databinding.ActivitySettingBinding;
import com.moutamid.calenderapp.utilis.Constants;

public class SettingActivity extends AppCompatActivity {
    ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.toolbar.title.setText(getString(R.string.settings));
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        binding.logout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.logout))
                    .setMessage(getString(R.string.are_you_sure_you_want_to_logout))
                    .setNegativeButton(getString(R.string.no), ((dialog, which) -> dialog.dismiss()))
                    .setPositiveButton(getString(R.string.yes), ((dialog, which) -> {
                        dialog.dismiss();
                        Stash.clear(Constants.STASH_USER);
                        Constants.auth().signOut();
                        startActivity(new Intent(this, SplashScreenActivity.class));
                        finish();
                    }))
                    .show();
        });

        binding.terms.setOnClickListener(v -> openBrowser(Constants.TERMS));
        binding.privacy.setOnClickListener(v -> openBrowser(Constants.POLICY));

        binding.editProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileEditActivity.class)));

    }

    private void openBrowser(String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(intent);
    }

}