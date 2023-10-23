package com.moutamid.calenderapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.adapters.EventProfileAdapter;
import com.moutamid.calenderapp.databinding.ActivityUserProfileBinding;
import com.moutamid.calenderapp.models.TaskModel;
import com.moutamid.calenderapp.models.UserModel;
import com.moutamid.calenderapp.utilis.Constants;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {
    ActivityUserProfileBinding binding;
    UserModel userModel;
    ArrayList<TaskModel> taskList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Constants.initDialog(this);

        taskList = new ArrayList<>();

        binding.toolbar.title.setText("Friend Profile");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        userModel = (UserModel) Stash.getObject("PassUser", UserModel.class);

        binding.invite.setOnClickListener(v -> startActivity(new Intent(this, NewEventActivity.class)));

        binding.eventsRC.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.eventsRC.setHasFixedSize(false);

        getSendRequests();

        binding.name.setText(userModel.getName());
        Glide.with(this).load(userModel.getImage()).placeholder(R.drawable.profile_icon).into(binding.profileIcon);

    }

    private void getSendRequests() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.ACTIVE_TASKS).child(Constants.CurrentMonth()).child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            taskList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                TaskModel taskModel = dataSnapshot.getValue(TaskModel.class);
                                if (!taskModel.isEnded() && Constants.auth().getCurrentUser().getUid().equals(userModel.getID())) {
                                    taskList.add(taskModel);
                                }
                                if (taskList.size() > 0){
                                    binding.eventsRC.setVisibility(View.VISIBLE);
                                    binding.noItemLayout.setVisibility(View.GONE);
                                } else {
                                    binding.eventsRC.setVisibility(View.GONE);
                                    binding.noItemLayout.setVisibility(View.VISIBLE);
                                }

                                EventProfileAdapter adapter = new EventProfileAdapter(UserProfileActivity.this, taskList);
                                binding.eventsRC.setAdapter(adapter);

                            }
                        }
                        Constants.dismissDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Constants.dismissDialog();
                        Toast.makeText(UserProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}