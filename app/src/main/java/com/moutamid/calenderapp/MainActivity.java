package com.moutamid.calenderapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fxn.stash.Stash;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.moutamid.calenderapp.databinding.ActivityMainBinding;
import com.moutamid.calenderapp.fragment.ChatFragment;
import com.moutamid.calenderapp.fragment.HomeFragment;
import com.moutamid.calenderapp.fragment.ListFragment;
import com.moutamid.calenderapp.fragment.ProfileFragment;
import com.moutamid.calenderapp.models.TaskModel;
import com.moutamid.calenderapp.notifications.Notification;
import com.moutamid.calenderapp.notifications.NotificationSchdule;
import com.moutamid.calenderapp.utilis.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

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

        ArrayList<TaskModel> taskList = new ArrayList<>();
        Constants.databaseReference().child(Constants.ACTIVE_TASKS).child(Constants.CurrentMonth()).child(Constants.auth().getCurrentUser().getUid())
                .get().addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            TaskModel taskModel = snapshot.getValue(TaskModel.class);
                            taskList.add(taskModel);
                        }
                        Notification.scheduleEventNotifications(this, taskList);
                    }
                });

        binding.bottomNav.setItemActiveIndicatorColor(ColorStateList.valueOf(getResources().getColor(R.color.greenLight)));
        binding.bottomNav.setOnNavigationItemSelectedListener(this);
        binding.bottomNav.setSelectedItemId(R.id.nav_home);

    }

    private void initializeNotification() {
//        FirebaseMessaging.getInstance().subscribeToTopic(Constants.auth().getCurrentUser().getUid())
//                .addOnSuccessListener(unused -> {
////                    new FcmNotificationsSender("/topics/" + "ALL", "Incoming Request", "Someone want to work with you", MainActivity.this, MainActivity.this).SendNotifications();
////                       Toast.makeText(this, "Subscribed", Toast.LENGTH_SHORT).show();
//                }).addOnFailureListener(e -> {
//                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                });
//        Constants.databaseReference().child("serverKey").get().addOnSuccessListener(dataSnapshot -> {
//            String key = dataSnapshot.getValue().toString();
//            //  Toast.makeText(this, key, Toast.LENGTH_SHORT).show();
//            Stash.put(Constants.KEY, key);
//        });

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {
            Log.d("NotificationHelper", "getToken: " + s);
            Constants.databaseReference().child(Constants.USER).child(Constants.auth().getCurrentUser().getUid()).child("fcmToken").setValue(s);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProfileFragment()).commit();
            return true;
        } else if (item.getItemId() == R.id.nav_list) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ListFragment()).commit();
            return true;
        }
        return false;
    }
}