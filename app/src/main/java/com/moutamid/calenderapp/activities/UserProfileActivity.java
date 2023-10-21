package com.moutamid.calenderapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.databinding.ActivityUserProfileBinding;
import com.moutamid.calenderapp.models.UserModel;

public class UserProfileActivity extends AppCompatActivity {
    ActivityUserProfileBinding binding;
    UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.title.setText("Friend Profile");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        userModel = (UserModel) Stash.getObject("PassUser", UserModel.class);

        binding.invite.setOnClickListener(v -> startActivity(new Intent(this, NewEventActivity.class)));

        binding.name.setText(userModel.getName());
        Glide.with(this).load(userModel.getImage()).placeholder(R.drawable.profile_icon).into(binding.profileIcon);

    }
}