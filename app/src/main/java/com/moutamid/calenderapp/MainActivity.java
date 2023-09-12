package com.moutamid.calenderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moutamid.calenderapp.databinding.ActivityMainBinding;
import com.moutamid.calenderapp.fragment.HomeFragment;
import com.moutamid.calenderapp.fragment.ListFragment;
import com.moutamid.calenderapp.fragment.SettingsFragment;
import com.moutamid.calenderapp.utilis.Constants;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);

        binding.bottomNav.setItemActiveIndicatorColor(ColorStateList.valueOf(getResources().getColor(R.color.orange_ripple)));
        binding.bottomNav.setOnNavigationItemSelectedListener(this);
        binding.bottomNav.setSelectedItemId(R.id.nav_home);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home ){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout , new HomeFragment()).commit();
            return true;
        } else  if (item.getItemId() == R.id.nav_setting ){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout , new SettingsFragment()).commit();
            return true;
        } else  if (item.getItemId() == R.id.nav_list ){
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout , new ListFragment()).commit();
            return true;
        }
        return false;
    }
}