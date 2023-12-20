package com.moutamid.calenderapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.adapters.MediaPagerAdapter;
import com.moutamid.calenderapp.databinding.ActivityFullPreviewBinding;
import com.moutamid.calenderapp.models.ChatsModel;

import java.util.ArrayList;

public class FullPreviewActivity extends AppCompatActivity {
    ActivityFullPreviewBinding binding;
    int position;
    ArrayList<ChatsModel> list;
    ProgressDialog progressDialog;
    public MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(v -> onBackPressed());

        position = getIntent().getIntExtra("position", 0);
        list = Stash.getArrayList("GALERYLIST", ChatsModel.class);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.media_is_loading));
        progressDialog.setCancelable(false);
        mediaController = new MediaController(this);

        boolean isImage = list.get(position).getType().equals("img");
        if (isImage) {
            showImagePreview(position);
        } else {
            showVideoPreview(position);
        }

        binding.next.setOnClickListener(v -> {
            if (position < list.size()-1) {
                position += 1;
                boolean isIMG = list.get(position).getType().equals("img");
                if (binding.videoView.isPlaying()){
                    binding.videoView.stopPlayback();
                }
                if (isIMG) {
                    showImagePreview(position);
                } else {
                    showVideoPreview(position);
                }
            }
        });

        binding.prev.setOnClickListener(v -> {
            if (position > 0) {
                position -= 1;
                if (binding.videoView.isPlaying()){
                    binding.videoView.stopPlayback();
                }
                boolean isIMG = list.get(position).getType().equals("img");
                if (isIMG) {
                    showImagePreview(position);
                } else {
                    showVideoPreview(position);
                }
            }
        });

    }

    private void showVideoPreview(int position) {
        binding.videoView.setVisibility(View.VISIBLE);
        binding.imageView.setVisibility(View.GONE);
        String path = list.get(position).getMessage();

        progressDialog.show();
        mediaController.setAnchorView(binding.videoView);
        mediaController.setMediaPlayer(binding.videoView);
        binding.videoView.setVideoPath(path);
        binding.videoView.setMediaController(mediaController);
        binding.videoView.start();
        binding.videoView.setOnPreparedListener(mp -> {
            progressDialog.dismiss();
            mediaController.show();
        });
        binding.videoView.setOnCompletionListener(mp -> {
            binding.videoView.start();
        });


    }

    private void showImagePreview(int position) {
        binding.imageView.setVisibility(View.VISIBLE);
        binding.videoView.setVisibility(View.GONE);
        String path = list.get(position).getMessage();
        Glide.with(this).load(path).placeholder(R.color.black).into(binding.imageView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Stash.clear("GALERYLIST");
    }
}