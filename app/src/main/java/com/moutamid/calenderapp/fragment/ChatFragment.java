package com.moutamid.calenderapp.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.adapters.ChatListAdapter;
import com.moutamid.calenderapp.databinding.FragmentChatBinding;
import com.moutamid.calenderapp.models.ChatListModel;
import com.moutamid.calenderapp.utilis.Constants;

import java.util.ArrayList;

public class ChatFragment extends Fragment {
    FragmentChatBinding binding;
    Context context;
    ArrayList<ChatListModel> list;
    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(getLayoutInflater(), container, false);

        context = binding.getRoot().getContext();
        Constants.initDialog(context);

//        setStatusBarColor();

        list = new ArrayList<>();

        binding.RC.setLayoutManager(new LinearLayoutManager(context));
        binding.RC.setHasFixedSize(false);
        Constants.showDialog();
        Constants.databaseReference().child(Constants.CHAT_LIST).child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                ChatListModel model = dataSnapshot.getValue(ChatListModel.class);
                                list.add(model);

                                if (list.size() > 0){
                                    binding.RC.setVisibility(View.VISIBLE);
                                    binding.noItemLayout.setVisibility(View.GONE);
                                } else {
                                    binding.RC.setVisibility(View.GONE);
                                    binding.noItemLayout.setVisibility(View.VISIBLE);
                                }

                                ChatListAdapter adapter = new ChatListAdapter(context, list);
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

        return binding.getRoot();
    }
}