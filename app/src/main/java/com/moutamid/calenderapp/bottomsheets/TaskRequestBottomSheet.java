package com.moutamid.calenderapp.bottomsheets;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.databinding.TaskRequestBottomsheetBinding;
import com.moutamid.calenderapp.models.TaskModel;

import java.util.Locale;

public class TaskRequestBottomSheet extends BottomSheetDialogFragment {
    TaskRequestBottomsheetBinding binding;
    Context context;
    TaskModel model;
    public TaskRequestBottomSheet(TaskModel model) {
        this.model = model;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = TaskRequestBottomsheetBinding.inflate(getLayoutInflater(), container, false);
        context = binding.getRoot().getContext();

        binding.username.setText(model.getUsername());
        binding.userID.setText("@" + model.getUserHandle());
        binding.taskName.setText(model.getName());
        binding.taskDesc.setText(model.getDescription());

        String date = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(model.getDate().getDate());
        binding.date.setText(date);

        Glide.with(context).load(model.getUserImage()).placeholder(R.drawable.profile_icon).into(binding.profileImage);

        if (model.isAccepted()){
            binding.accept.setVisibility(View.GONE);
            binding.endReject.setText("End Task");
        } else {
            binding.accept.setVisibility(View.VISIBLE);
            binding.endReject.setText("Reject");
        }

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
/*        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            BottomSheetBehavior.from((View) getView().getParent()).setState(BottomSheetBehavior.STATE_EXPANDED);
        }*/
    }

}
