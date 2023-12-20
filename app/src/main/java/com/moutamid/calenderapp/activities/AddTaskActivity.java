package com.moutamid.calenderapp.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.adapters.AddImageAdapter;
import com.moutamid.calenderapp.databinding.ActivityAddTaskBinding;
import com.moutamid.calenderapp.interfaces.AddImageClick;
import com.moutamid.calenderapp.models.ShareContentModel;
import com.moutamid.calenderapp.utilis.Constants;

import java.util.ArrayList;

public class AddTaskActivity extends AppCompatActivity {
    ActivityAddTaskBinding binding;
    private static final int PICK_FROM_GALLERY = 1;
    AddImageAdapter adapter;
    private final int limit = 5;
    ArrayList<Uri> imagesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Constants.initDialog(this);

        binding.toolbar.title.setText(getString(R.string.add_tasks));
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        imagesList = new ArrayList<>();

        binding.AddPhotoLayout.setOnClickListener(v -> getImage());

        binding.btnAddCarPhoto.setOnClickListener(v -> {
            if (imagesList.size() >= limit){
                Toast.makeText(this, getString(R.string.required_number_of_images_are_selected), Toast.LENGTH_SHORT).show();
            } else{
                getImage();
            }
        });

        binding.RecyclerViewImageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.RecyclerViewImageList.setHasFixedSize(true);

    }

    private void getImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, ""), PICK_FROM_GALLERY);
    }

    AddImageClick click = new AddImageClick() {
        @Override
        public void onClick(ShareContentModel uri, int position) {
            final Dialog dialog = new Dialog(AddTaskActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bs_image_menu);

            Button removeImg = dialog.findViewById(R.id.btnRemoveImage);
            Button cancel = dialog.findViewById(R.id.btnCancel);

            cancel.setOnClickListener(can -> {
                dialog.cancel();
            });

            removeImg.setOnClickListener(remove -> {
                try{
                    if(imagesList.size() == 1){
                        imagesList.remove(position);
                        adapter.notifyItemRemoved(position);
                        dialog.cancel();
                        if (imagesList.isEmpty()) {
                            binding.AddPhotoLayoutRecycler.setVisibility(View.GONE);
                            binding.AddPhotoLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    imagesList.remove(position);
                    adapter.notifyItemRemoved(position);
                    dialog.cancel();
                } catch (Exception e){

                }

            });

            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }
    };

}