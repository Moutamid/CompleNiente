package com.moutamid.calenderapp.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.SplashScreenActivity;
import com.moutamid.calenderapp.activities.ProfileEditActivity;
import com.moutamid.calenderapp.activities.SelectUserActivity;
import com.moutamid.calenderapp.adapters.EventAdapter;
import com.moutamid.calenderapp.adapters.EventProfileAdapter;
import com.moutamid.calenderapp.databinding.FragmentProfileBinding;
import com.moutamid.calenderapp.models.TaskModel;
import com.moutamid.calenderapp.models.UserModel;
import com.moutamid.calenderapp.utilis.Constants;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    Context context;
    ArrayList<TaskModel> taskList;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        context = binding.getRoot().getContext();

//        setStatusBarColor();

        Constants.initDialog(context);

        taskList = new ArrayList<>();

        binding.name.setOnClickListener(v -> startActivity(new Intent(context, ProfileEditActivity.class)));
        binding.profileImage.setOnClickListener(v -> startActivity(new Intent(context, ProfileEditActivity.class)));
        binding.edit.setOnClickListener(v -> startActivity(new Intent(context, ProfileEditActivity.class)));
        binding.newEvent.setOnClickListener(v -> startActivity(new Intent(context, SelectUserActivity.class)));

//        binding.logout.setOnClickListener(v -> {
//            new AlertDialog.Builder(context)
//                    .setTitle("Logout")
//                    .setMessage("Are you sure you want to logout?")
//                    .setNegativeButton("No", ((dialog, which) -> dialog.dismiss()))
//                    .setPositiveButton("Yes", ((dialog, which) -> {
//                        dialog.dismiss();
//                        Stash.clear(Constants.STASH_USER);
//                        Constants.auth().signOut();
//                        startActivity(new Intent(context, SplashScreenActivity.class));
//                        getActivity().finish();
//                    }))
//                    .show();
//        });


        binding.eventsRC.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        binding.eventsRC.setHasFixedSize(false);

//        binding.terms.setOnClickListener(v -> openBrowser(Constants.TERMS));
//        binding.privacy.setOnClickListener(v -> openBrowser(Constants.POLICY));

        getSendRequests();

        return binding.getRoot();
    }

    private void getSendRequests() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.ACTIVE_TASKS).child(Constants.CurrentMonth()).child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            taskList.clear();
                            Stash.clear(Constants.NOTI_SCHEDULE);
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                TaskModel taskModel = dataSnapshot.getValue(TaskModel.class);
                                if (!taskModel.isEnded()){
                                    taskList.add(taskModel);
                                }
                                if (taskList.size() > 0){
                                    binding.eventsRC.setVisibility(View.VISIBLE);
                                    binding.noItemLayout.setVisibility(View.GONE);
                                } else {
                                    binding.eventsRC.setVisibility(View.GONE);
                                    binding.noItemLayout.setVisibility(View.VISIBLE);
                                }
                                binding.eventCount.setText("" + taskList.size());
                                Stash.put(Constants.NOTI_SCHEDULE, taskList);
                                EventProfileAdapter adapter = new EventProfileAdapter(context, taskList);
                                binding.eventsRC.setAdapter(adapter);

                            }
                        }
                        Constants.dismissDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Constants.dismissDialog();
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openBrowser(String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        UserModel user = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        binding.name.setText(user.getName());
        Glide.with(context).load(user.getImage()).placeholder(R.drawable.profile_icon).into(binding.profileIcon);
    }
}