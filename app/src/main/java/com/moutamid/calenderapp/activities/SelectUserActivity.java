package com.moutamid.calenderapp.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.calenderapp.adapters.CalendarAdapter;
import com.moutamid.calenderapp.adapters.UsersAdapter;
import com.moutamid.calenderapp.databinding.ActivitySelectUserBinding;
import com.moutamid.calenderapp.models.CalendarDate;
import com.moutamid.calenderapp.models.TaskModel;
import com.moutamid.calenderapp.models.UserModel;
import com.moutamid.calenderapp.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SelectUserActivity extends AppCompatActivity {
    ActivitySelectUserBinding binding;
    ArrayList<UserModel> list;
    CalendarDate date;
    UsersAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.title.setText("Select Users");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        binding.RC.setLayoutManager(new LinearLayoutManager(this));
        binding.RC.setHasFixedSize(false);

        Constants.initDialog(this);
        Constants.showDialog();

        list = new ArrayList<>();

        date = (CalendarDate) Stash.getObject(Constants.DATE, CalendarDate.class);

        binding.search.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getUsers();
    }

    private void getUsers() {
        Constants.databaseReference().child(Constants.USER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        UserModel model = dataSnapshot.getValue(UserModel.class);
                        if (!model.getID().equals(Constants.auth().getCurrentUser().getUid())){
                            list.add(model);
                        }
                    }

                    adapter = new UsersAdapter(SelectUserActivity.this, SelectUserActivity.this, list);
                    binding.RC.setAdapter(adapter);
                }
                Constants.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Constants.dismissDialog();
                Constants.createSnackbar(SelectUserActivity.this, binding.getRoot(), error.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}