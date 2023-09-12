package com.moutamid.calenderapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

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

import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.adapters.AddImageAdapter;
import com.moutamid.calenderapp.databinding.ActivityAddTaskBinding;
import com.moutamid.calenderapp.interfaces.AddImageClick;
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

        binding.toolbar.title.setText("Add Tasks");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        imagesList = new ArrayList<>();

        binding.AddPhotoLayout.setOnClickListener(v -> getImage());

        binding.btnAddCarPhoto.setOnClickListener(v -> {
            if (imagesList.size() >= limit){
                Toast.makeText(this, "Required Number of Images are selected", Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_FROM_GALLERY) {
                try{
                    if (resultCode == RESULT_OK && data != null && data.getClipData() != null) {
                        binding.AddPhotoLayout.setVisibility(View.GONE);
                        binding.AddPhotoLayoutRecycler.setVisibility(View.VISIBLE);
                        int currentImage = 0;

                        while (currentImage < data.getClipData().getItemCount()) {
                            if (currentImage < limit){
                                imagesList.add(data.getClipData().getItemAt(currentImage).getUri());
                            }
                            currentImage++;
                        }

                        adapter = new AddImageAdapter(AddTaskActivity.this, imagesList, click);
                        binding.RecyclerViewImageList.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Please Select Multiple Images", Toast.LENGTH_SHORT).show();
                    }
                }  catch (Exception e){
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    binding.AddPhotoLayout.setVisibility(View.GONE);
                    binding.AddPhotoLayoutRecycler.setVisibility(View.VISIBLE);
                    imagesList.add(data.getData());

                    adapter = new AddImageAdapter(AddTaskActivity.this, imagesList, click);
                    binding.RecyclerViewImageList.setAdapter(adapter);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    AddImageClick click = new AddImageClick() {
        @Override
        public void onClick(Uri uri, int position) {
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