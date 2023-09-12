package com.moutamid.calenderapp.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.SplashScreenActivity;
import com.moutamid.calenderapp.activities.ProfileEditActivity;
import com.moutamid.calenderapp.databinding.FragmentHomeBinding;
import com.moutamid.calenderapp.databinding.FragmentSettingsBinding;
import com.moutamid.calenderapp.models.UserModel;
import com.moutamid.calenderapp.utilis.Constants;

public class SettingsFragment extends Fragment {
    FragmentSettingsBinding binding;
    Context context;
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(getLayoutInflater(), container, false);
        context = binding.getRoot().getContext();

        Constants.initDialog(context);

        binding.name.setOnClickListener(v -> startActivity(new Intent(context, ProfileEditActivity.class)));
        binding.profileImage.setOnClickListener(v -> startActivity(new Intent(context, ProfileEditActivity.class)));
        binding.editProfile.setOnClickListener(v -> startActivity(new Intent(context, ProfileEditActivity.class)));

        binding.logout.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setNegativeButton("No", ((dialog, which) -> dialog.dismiss()))
                    .setPositiveButton("Yes", ((dialog, which) -> {
                        dialog.dismiss();
                        Stash.clear(Constants.STASH_USER);
                        Constants.auth().signOut();
                        startActivity(new Intent(context, SplashScreenActivity.class));
                        getActivity().finish();
                    }))
                    .show();
        });

        binding.terms.setOnClickListener(v -> openBrowser(Constants.TERMS));
        binding.privacy.setOnClickListener(v -> openBrowser(Constants.POLICY));

        return binding.getRoot();
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
        binding.email.setText(user.getEmail());
        Glide.with(context).load(user.getImage()).placeholder(R.drawable.profile_icon).into(binding.profileImage);
    }
}