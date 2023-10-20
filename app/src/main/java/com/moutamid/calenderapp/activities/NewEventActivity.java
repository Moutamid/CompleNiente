package com.moutamid.calenderapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.databinding.ActivityNewEventBinding;
import com.moutamid.calenderapp.utilis.Constants;

public class NewEventActivity extends AppCompatActivity {
    ActivityNewEventBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Constants.initDialog(this);

        binding.toolbar.title.setText("Create Event");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

    }
}