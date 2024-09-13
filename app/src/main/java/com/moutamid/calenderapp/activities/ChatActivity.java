package com.moutamid.calenderapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.adapters.AddImageAdapter;
import com.moutamid.calenderapp.adapters.ChatAdapter;
import com.moutamid.calenderapp.databinding.ActivityChatBinding;
import com.moutamid.calenderapp.interfaces.AddImageClick;
import com.moutamid.calenderapp.models.ChatListModel;
import com.moutamid.calenderapp.models.ChatsModel;
import com.moutamid.calenderapp.models.ShareContentModel;
import com.moutamid.calenderapp.notifications.FCMNotificationHelper;
import com.moutamid.calenderapp.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    ChatListModel chatListModel;
    private static final int PICK_FROM_GALLERY_IMAGE = 1;
    private static final int PICK_FROM_GALLERY_VIDEO = 2;
    AddImageAdapter adapter;
    ChatAdapter chatAdapter;
    private final int limit = 5;
    ArrayList<ShareContentModel> imagesList;
    ArrayList<ChatsModel> chatsList;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_VIDEO_CAPTURE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.green));

        binding.back.setOnClickListener(v -> onBackPressed());

        chatListModel = (ChatListModel) getIntent().getSerializableExtra("ChatPerson");

        binding.title.setText(chatListModel.getName());
        Glide.with(this).load(chatListModel.getImage()).placeholder(R.drawable.profile_icon).into(binding.profileImage);

        imagesList = new ArrayList<>();
        chatsList = new ArrayList<>();

        binding.imagePreviewRC.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.imagePreviewRC.setHasFixedSize(true);

        binding.chatRC.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRC.setHasFixedSize(false);

        Date currentDate = new Date();
        Date taskDate = chatListModel.getDate();
        String dayMonth = "ddMM";
        String cd = new SimpleDateFormat(dayMonth, Locale.getDefault()).format(currentDate);
        String t = new SimpleDateFormat(dayMonth, Locale.getDefault()).format(taskDate);
        if (cd.equals(t)) {
            binding.noChatLayout.setVisibility(View.GONE);
            binding.uploadImage.setEnabled(true);
            binding.uploadVideo.setEnabled(true);
        } else {
            binding.noChatLayout.setVisibility(View.VISIBLE);
            binding.uploadImage.setEnabled(false);
            binding.uploadVideo.setEnabled(false);
        }

        Constants.databaseReference().child(Constants.CHATS).child(chatListModel.getTaskID())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()) {
                            ChatsModel chatsModel = snapshot.getValue(ChatsModel.class);
                            chatsList.add(chatsModel);
                            chatsList.sort(Comparator.comparing(ChatsModel::getTimestamps));
                            chatAdapter = new ChatAdapter(ChatActivity.this, chatsList);
                            binding.chatRC.setAdapter(chatAdapter);
                            binding.chatRC.scrollToPosition(chatsList.size() - 1);
                            chatAdapter.notifyItemInserted(chatsList.size() - 1);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()) {

                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.uploadImage.setOnClickListener(v -> {
            if (imagesList.size() >= limit) {
                Toast.makeText(this, getString(R.string.required_number_of_medias_are_selected), Toast.LENGTH_SHORT).show();
            } else {
                showPicker(true);
            }
        });

        binding.uploadVideo.setOnClickListener(v -> {
            if (imagesList.size() >= limit) {
                Toast.makeText(this, getString(R.string.required_number_of_medias_are_selected), Toast.LENGTH_SHORT).show();
            } else {
                showPicker(false);
            }
        });

        binding.send.setOnClickListener(v -> {
            uploadChat();
        });

    }

    public void showPicker(boolean isImage) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.picker_selector);

        MaterialCardView camera = dialog.findViewById(R.id.camera);
        MaterialCardView image = dialog.findViewById(R.id.image);

        camera.setOnClickListener(v -> {
            dialog.dismiss();
            if (isImage) {
                ImagePicker.with(ChatActivity.this)
                        .cameraOnly()
                        .compress(1024)
                        .start(REQUEST_IMAGE_CAPTURE);
            } else {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(takeVideoIntent, ""), REQUEST_VIDEO_CAPTURE);
                }
            }
        });

        image.setOnClickListener(v -> {
            dialog.dismiss();
            if (isImage) {
                getImage();
            } else {
                getVideos();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void uploadChat() {
        binding.sendLayout.setVisibility(View.GONE);
        binding.progressLayout.setVisibility(View.VISIBLE);
        for (ShareContentModel model : imagesList) {
            String path = UUID.randomUUID().toString();
            Constants.storageReference(Constants.auth().getCurrentUser().getUid()).child("chats").child(path)
                    .putFile(model.getUri())
                    .addOnSuccessListener(taskSnapshot -> {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                            ChatsModel sender = new ChatsModel(uri.toString(), Constants.auth().getCurrentUser().getUid(), new Date().getTime(), model.getType());
                            Constants.databaseReference().child(Constants.CHATS).child(chatListModel.getTaskID())
                                    .push().setValue(sender).addOnSuccessListener(unused -> {
                                        binding.progressLayout.setVisibility(View.GONE);
                                        imagesList.clear();
                                        new FCMNotificationHelper(ChatActivity.this).sendNotification(chatListModel.getUserID(), "Nuovo messaggio", "Hai ricevuto un nuovo messaggio");
                                    })
                                    .addOnFailureListener(e -> {
                                        e.printStackTrace();
                                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }).addOnFailureListener(e -> {
                            e.printStackTrace();
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                    }).addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(snapshot -> {
                        int progress = (int) ((100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount());
                        binding.progressText.setText(progress + "/100");
                        binding.progressBar.setProgress(progress, true);
                    });
        }
    }

    private void getVideos() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, ""), PICK_FROM_GALLERY_VIDEO);
    }

    private void getImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, ""), PICK_FROM_GALLERY_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_FROM_GALLERY_IMAGE) {
                try {
                    if (resultCode == RESULT_OK && data != null && data.getClipData() != null) {
                        binding.sendLayout.setVisibility(View.VISIBLE);
                        int currentImage = 0;

                        while (currentImage < data.getClipData().getItemCount()) {
                            if (currentImage < limit) {
                                Uri imageUri = data.getClipData().getItemAt(currentImage).getUri();
                                imagesList.add(new ShareContentModel(imageUri, "img"));
                            }
                            currentImage++;
                        }

                        adapter = new AddImageAdapter(ChatActivity.this, imagesList, click);
                        binding.imagePreviewRC.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, getString(R.string.please_select_multiple_images), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == PICK_FROM_GALLERY_VIDEO) {
                try {
                    if (resultCode == RESULT_OK && data != null && data.getClipData() != null) {
                        binding.sendLayout.setVisibility(View.VISIBLE);
                        int currentImage = 0;

                        while (currentImage < data.getClipData().getItemCount()) {
                            if (currentImage < limit) {
                                Uri videoUri = data.getClipData().getItemAt(currentImage).getUri();
                                imagesList.add(new ShareContentModel(videoUri, "vid"));
                            }
                            currentImage++;
                        }

                        adapter = new AddImageAdapter(ChatActivity.this, imagesList, click);
                        binding.imagePreviewRC.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, getString(R.string.please_select_multiple_videos), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Log.d("EVENTDETAIL123", "onActivityResult: REQUEST_IMAGE_CAPTURE");
                Log.d("EVENTDETAIL123", "onActivityResult: resultCode   " + resultCode);
                Log.d("EVENTDETAIL123", "onActivityResult: data   " + (data != null));
                Log.d("EVENTDETAIL123", "onActivityResult: data   " + (data.getData() != null));
                if (resultCode == RESULT_OK) {
                    binding.sendLayout.setVisibility(View.VISIBLE);
                    Uri imageUri = data.getData();
                    imagesList.add(new ShareContentModel(imageUri, "img"));
                    adapter = new AddImageAdapter(ChatActivity.this, imagesList, click);
                    binding.imagePreviewRC.setAdapter(adapter);
                }
            } else if (requestCode == REQUEST_VIDEO_CAPTURE) {
                if (resultCode == RESULT_OK && data != null) {
                    binding.sendLayout.setVisibility(View.VISIBLE);
                    Uri imageUri = data.getData();
                    imagesList.add(new ShareContentModel(imageUri, "vid"));
                    adapter = new AddImageAdapter(ChatActivity.this, imagesList, click);
                    binding.imagePreviewRC.setAdapter(adapter);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    AddImageClick click = new AddImageClick() {
        @Override
        public void onClick(ShareContentModel uri, int position) {
            final Dialog dialog = new Dialog(ChatActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bs_image_menu);

            Button removeImg = dialog.findViewById(R.id.btnRemoveImage);
            Button cancel = dialog.findViewById(R.id.btnCancel);

            cancel.setOnClickListener(can -> {
                dialog.cancel();
            });

            removeImg.setOnClickListener(remove -> {
                try {
                    if (imagesList.size() == 1) {
                        imagesList.remove(position);
                        adapter.notifyItemRemoved(position);
                        dialog.cancel();
                        if (imagesList.isEmpty()) {
                            binding.sendLayout.setVisibility(View.GONE);
                        }
                    }
                    imagesList.remove(position);
                    adapter.notifyItemRemoved(position);
                    dialog.cancel();
                } catch (Exception e) {

                }

            });

            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }
    };

}