package com.moutamid.calenderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.calenderapp.MainActivity;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.databinding.ActivitySignUpBinding;
import com.moutamid.calenderapp.models.UserModel;
import com.moutamid.calenderapp.utilis.Constants;

import java.util.ArrayList;
import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    String email = "";
    ArrayList<UserModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Constants.initDialog(this);

        binding.toolbar.title.setText(getString(R.string.create_account));
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        email = getIntent().getStringExtra("email");

        binding.email.getEditText().setText(email);

        list = new ArrayList<>();

        Constants.databaseReference().child(Constants.USER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        UserModel model = dataSnapshot.getValue(UserModel.class);
                        list.add(model);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String allowedChars = "A-Za-z0-9._";
                String regexPattern = "^[a-z0-9._]+$";
                if (!source.toString().matches(regexPattern)) {
                    return "";
                }
          /*      for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (allowedChars.indexOf(c) == -1) {
                        return "";
                    }
                }*/
                return null;
            }
        };

        binding.username.getEditText().setFilters(new InputFilter[]{filter});

        binding.create.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                createAccount();
            }
        });

    }

    private void createAccount() {
        Constants.auth().createUserWithEmailAndPassword(
                        binding.email.getEditText().getText().toString(),
                        binding.password.getEditText().getText().toString()
                )
                .addOnSuccessListener(authResult -> {
                    String day = binding.day.getEditText().getText().toString();
                    String month = binding.month.getEditText().getText().toString();
                    String year = binding.year.getEditText().getText().toString();
                    String dob = day + "/" + month + "/" + year;
                    UserModel userModel = new UserModel(
                            authResult.getUser().getUid(),
                            binding.username.getEditText().getText().toString(),
                            binding.name.getEditText().getText().toString(),
                            binding.email.getEditText().getText().toString(),
                            binding.password.getEditText().getText().toString(), dob, "", ""
                    );
                    Stash.put(Constants.STASH_USER, userModel);
                    Constants.databaseReference().child(Constants.USER).child(Constants.auth().getCurrentUser().getUid())
                            .setValue(userModel)
                            .addOnSuccessListener(unused -> {
                                Constants.dismissDialog();
                                Intent intent = new Intent(this, EmailVerifyActivity.class);
                                intent.putExtra("fromSplash", false);
                                startActivity(intent);
                                finish();
                            }).addOnFailureListener(e -> {
                                Constants.dismissDialog();
                                Constants.createSnackbar(this, binding.create, e.getLocalizedMessage());
                            });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Constants.createSnackbar(this, binding.create, e.getLocalizedMessage());
                });
    }

    private boolean valid() {
        if (binding.username.getEditText().getText().toString().isEmpty()) {
            binding.username.setErrorEnabled(true);
            binding.username.setError(getString(R.string.user_name_is_empty));
            return false;
        }

        if (!binding.username.getEditText().getText().toString().isEmpty()){
            String un = binding.username.getEditText().getText().toString();
            for (UserModel user : list){
                if (un.equals(user.getUsername())) {
                    binding.username.setErrorEnabled(true);
                    binding.username.setError(getString(R.string.username_is_not_available));
                    return false;
                }
            }
        }

        if (binding.name.getEditText().getText().toString().isEmpty()) {
            binding.name.setErrorEnabled(true);
            binding.name.setError(getString(R.string.name_is_empty));
            return false;
        }
        String day = binding.day.getEditText().getText().toString();
        String month = binding.month.getEditText().getText().toString();
        String year = binding.year.getEditText().getText().toString();
        if (!isValidDate(day, month, year)){
            Constants.createSnackbar(this, binding.create, getString(R.string.date_is_not_valid));
            return false;
        }
        if (binding.email.getEditText().getText().toString().isEmpty()) {
            binding.email.setErrorEnabled(true);
            binding.email.setError(getString(R.string.email_is_empty));
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getEditText().getText().toString()).matches()) {
            binding.email.setErrorEnabled(true);
            binding.email.setError(getString(R.string.email_is_not_valid));
            return false;
        }
        if (binding.password.getEditText().getText().toString().isEmpty()) {
            binding.password.setErrorEnabled(true);
            binding.password.setError(getString(R.string.password_is_empty));
            return false;
        }
        return true;
    }

    private boolean isValidDate(String dayStr, String monthStr, String yearStr) {
        try {
            // Parse day, month, and year as integers
            int day = Integer.parseInt(dayStr);
            int month = Integer.parseInt(monthStr);
            int year = Integer.parseInt(yearStr);

            // Get the current year
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);

            // Check if year is valid (not greater than current year)
            if (year <= currentYear && String.valueOf(year).length() > 3) {
                // Check if month is valid (between 1 and 12)
                if (month >= 1 && month <= 12) {
                    // Check if day is valid based on the month
                    int maxDay = getMaximumDay(month, year);
                    if (day >= 1 && day <= maxDay) {
                        return true;
                    }
                }
            }
        } catch (NumberFormatException e) {
            // Invalid input (not numeric)
        }

        return false;
    }

    private int getMaximumDay(int month, int year) {
        // Calculate the maximum day for a given month and year
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1); // Calendar month is zero-based
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

}