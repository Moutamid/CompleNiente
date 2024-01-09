package com.moutamid.calenderapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.calenderapp.adapters.TaskAdapter;
import com.moutamid.calenderapp.bottomsheets.TaskRequestBottomSheet;
import com.moutamid.calenderapp.databinding.FragmentListBinding;
import com.moutamid.calenderapp.interfaces.TaskClickListener;
import com.moutamid.calenderapp.models.TaskModel;
import com.moutamid.calenderapp.notifications.FcmNotificationsSender;
import com.moutamid.calenderapp.utilis.Constants;

import java.util.ArrayList;

public class ListFragment extends Fragment {
    FragmentListBinding binding;
    Context context;
    ArrayList<TaskModel> taskList;
    ArrayList<TaskModel> sendList;
    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(getLayoutInflater(), container, false);

//        setStatusBarColor();

        context = binding.getRoot().getContext();

        sendList = new ArrayList<>();
        taskList = new ArrayList<>();

        binding.RC.setLayoutManager(new LinearLayoutManager(context));
        binding.RC.setHasFixedSize(false);
        binding.sendRC.setLayoutManager(new LinearLayoutManager(context));
        binding.sendRC.setHasFixedSize(false);

        return binding.getRoot();
    }

    private void getThisMonthTasks() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.REQUESTS).child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            Log.d("getThisMonthTasks", "onDataChange: ");
                            taskList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                TaskModel taskModel = dataSnapshot.getValue(TaskModel.class);
                                if (!taskModel.isEnded()){
                                    taskList.add(taskModel);
                                }
                                if (taskList.size() > 0){
                                    binding.RC.setVisibility(View.VISIBLE);
                                    binding.noItemLayout.setVisibility(View.GONE);
                                } else {
                                    binding.RC.setVisibility(View.GONE);
                                    binding.noItemLayout.setVisibility(View.VISIBLE);
                                }
                                TaskAdapter adapter = new TaskAdapter(context, taskList, listener);
                                binding.RC.setAdapter(adapter);
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

    @Override
    public void onResume() {
        super.onResume();
        Constants.initDialog(requireContext());
        getThisMonthTasks();
        getSendTasks();
    }

    private void getSendTasks() {
        Constants.databaseReference().child(Constants.SEND_REQUESTS).child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            Log.d("getThisMonthTasks", "onDataChange: ");
                            sendList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                TaskModel taskModel = dataSnapshot.getValue(TaskModel.class);
                                if (!taskModel.isEnded() || !taskModel.getIsAccepted().equals("YES")) {
                                    sendList.add(taskModel);
                                }
                                if (sendList.size() > 0) {
                                    binding.sendRC.setVisibility(View.VISIBLE);
                                    binding.noSendLayout.setVisibility(View.GONE);
                                } else {
                                    binding.sendRC.setVisibility(View.GONE);
                                    binding.noSendLayout.setVisibility(View.VISIBLE);
                                }
                                TaskAdapter adapter = new TaskAdapter(context, sendList, sendListener);
                                binding.sendRC.setAdapter(adapter);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    TaskClickListener sendListener = model -> {
        TaskRequestBottomSheet bottomSheetFragment = new TaskRequestBottomSheet(model, true);
        bottomSheetFragment.show(requireActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    };

    TaskClickListener listener = model -> {
        TaskRequestBottomSheet bottomSheetFragment = new TaskRequestBottomSheet(model, false);
        bottomSheetFragment.show(requireActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    };

}