package com.moutamid.calenderapp.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.databinding.FragmentChatBinding;
import com.moutamid.calenderapp.utilis.Constants;

public class ChatFragment extends Fragment {
    FragmentChatBinding binding;
    Context context;
    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(getLayoutInflater(), container, false);

        context = binding.getRoot().getContext();
        Constants.initDialog(context);



        return binding.getRoot();
    }
}