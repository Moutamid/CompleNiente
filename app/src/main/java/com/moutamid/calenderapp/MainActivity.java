package com.moutamid.calenderapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fxn.stash.Stash;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.moutamid.calenderapp.databinding.ActivityMainBinding;
import com.moutamid.calenderapp.fragment.ChatFragment;
import com.moutamid.calenderapp.fragment.HomeFragment;
import com.moutamid.calenderapp.fragment.ListFragment;
import com.moutamid.calenderapp.fragment.SettingsFragment;
import com.moutamid.calenderapp.utilis.Constants;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS);
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        initializeNotification();

        binding.bottomNav.setItemActiveIndicatorColor(ColorStateList.valueOf(getResources().getColor(R.color.greenLight)));
        binding.bottomNav.setOnNavigationItemSelectedListener(this);
        binding.bottomNav.setSelectedItemId(R.id.nav_home);

    }

    private void initializeNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.auth().getCurrentUser().getUid())
                .addOnSuccessListener(unused -> {
//                       Toast.makeText(this, "Subscribed", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
        Constants.databaseReference().child("serverKey").get().addOnSuccessListener(dataSnapshot -> {
            String key = dataSnapshot.getValue().toString();
          //  Toast.makeText(this, key, Toast.LENGTH_SHORT).show();
            Stash.put(Constants.KEY, key);
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();
            return true;
        } else if (item.getItemId() == R.id.nav_chat) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ChatFragment()).commit();
            return true;
        } else if (item.getItemId() == R.id.nav_setting) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SettingsFragment()).commit();
            return true;
        } else if (item.getItemId() == R.id.nav_list) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ListFragment()).commit();
            return true;
        }
        return false;
    }
}