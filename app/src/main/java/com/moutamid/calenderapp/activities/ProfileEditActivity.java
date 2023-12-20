package com.moutamid.calenderapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.databinding.ActivityProfileEditBinding;
import com.moutamid.calenderapp.models.UserModel;
import com.moutamid.calenderapp.utilis.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileEditActivity extends AppCompatActivity {
    ActivityProfileEditBinding binding;
    Uri imageUri;
    Map<String, Object> user = new HashMap<>();
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Constants.initDialog(this);

        binding.toolbar.title.setText(getString(R.string.edit_profile));
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        getUserDetails();

        binding.image.setOnClickListener(v -> {

            ImagePicker.Companion.with(this)
                    .cropSquare()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            startForProfileImageResult.launch(intent);
                            return null;
                        }
                    });
        });

        binding.update.setOnClickListener(v -> {
            Constants.showDialog();
            if (imageUri != null) {
                updateImage();
            } else {
                updateData(userModel.getImage());
            }
        });

    }

    private void updateImage() {
        String id = UUID.randomUUID().toString();
        Constants.storageReference(Constants.auth().getCurrentUser().getUid()).child("profilePics").child(id).putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                updateData(uri.toString());
                            }).addOnFailureListener(e -> {
                                Constants.dismissDialog();
                                Constants.createSnackbar(this, binding.getRoot(), e.getLocalizedMessage());
                            });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Constants.createSnackbar(this, binding.getRoot(), e.getLocalizedMessage());
                });
    }

    private void updateData(String link) {
        userModel.setImage(link);
        userModel.setName(binding.name.getEditText().getText().toString().trim());
        String day = binding.day.getEditText().getText().toString();
        String month = binding.month.getEditText().getText().toString();
        String year = binding.year.getEditText().getText().toString();
        String dob = day + "/" + month + "/" + year;
        userModel.setDob(dob);
        userModel.setBio(binding.bio.getEditText().getText().toString());
        Stash.put(Constants.STASH_USER, userModel);
        Constants.databaseReference().child(Constants.USER).child(Constants.auth().getCurrentUser().getUid())
                .setValue(userModel).addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Constants.createSnackbar(this, binding.getRoot(), e.getLocalizedMessage());
                });
    }

    private void getUserDetails() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.USER).child(Constants.auth().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        userModel = dataSnapshot.getValue(UserModel.class);
                        Stash.put(Constants.STASH_USER, userModel);
                        binding.username.getEditText().setText(userModel.getUsername());
                        binding.name.getEditText().setText(userModel.getName());
                        binding.email.getEditText().setText(userModel.getEmail());
                        binding.bio.getEditText().setText(userModel.getBio());
                        String[] dob = userModel.getDob().split("/");
                        binding.day.getEditText().setText(dob[0]);
                        binding.month.getEditText().setText(dob[1]);
                        binding.year.getEditText().setText(dob[2]);

                        Glide.with(ProfileEditActivity.this).load(userModel.getImage()).placeholder(R.drawable.profile_icon).into(binding.profileImage);
                    } else {
                        Constants.createSnackbar(this, binding.getRoot(), getString(R.string.user_data_not_found));
                    }
                    Constants.dismissDialog();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Constants.createSnackbar(this, binding.getRoot(), e.getLocalizedMessage(), "Dismiss");
                });
    }

    private final ActivityResultLauncher<Intent> startForProfileImageResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    int resultCode = result.getResultCode();
                    Intent data = result.getData();

                    if (resultCode == Activity.RESULT_OK) {
                        // Image Uri will not be null for RESULT_OK
                        imageUri = data.getData();
                        Glide.with(ProfileEditActivity.this)
                                .load(imageUri)
                                .placeholder(R.drawable.profile_icon)
                                .into(binding.profileImage);
                    } else if (resultCode == ImagePicker.RESULT_ERROR) {
                        Constants.createSnackbar(ProfileEditActivity.this, binding.getRoot(), ImagePicker.getError(data));
                    } else {
                        Constants.createSnackbar(ProfileEditActivity.this, binding.getRoot(), getString(R.string.no_image_is_selected));
                    }
                }
            }
    );

}