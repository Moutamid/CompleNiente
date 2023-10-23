package com.moutamid.calenderapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.adapters.AddImageAdapter;
import com.moutamid.calenderapp.adapters.ChatAdapter;
import com.moutamid.calenderapp.adapters.GalleryAdapter;
import com.moutamid.calenderapp.databinding.ActivityEventDetailBinding;
import com.moutamid.calenderapp.interfaces.AddImageClick;
import com.moutamid.calenderapp.models.ChatListModel;
import com.moutamid.calenderapp.models.ChatsModel;
import com.moutamid.calenderapp.models.ShareContentModel;
import com.moutamid.calenderapp.models.TaskModel;
import com.moutamid.calenderapp.models.UserModel;
import com.moutamid.calenderapp.notifications.FcmNotificationsSender;
import com.moutamid.calenderapp.utilis.Constants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

public class EventDetailActivity extends AppCompatActivity {
    ActivityEventDetailBinding binding;
    String eventID;
    TaskModel taskModel;
    private static final int PICK_FROM_GALLERY_IMAGE = 1;
    private static final int PICK_FROM_GALLERY_VIDEO = 2;
    AddImageAdapter adapter;
    GalleryAdapter chatAdapter;
    private final int limit = 5;
    ArrayList<ShareContentModel> imagesList;
    ArrayList<ChatsModel> chatsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eventID = getIntent().getStringExtra("eventID");

        Constants.initDialog(this);

        taskModel = (TaskModel) Stash.getObject("EVENT", TaskModel.class);

        chatsList = new ArrayList<>();
        imagesList = new ArrayList<>();

        binding.eventName.setText(taskModel.getName());
        binding.address.setText(taskModel.getLocation());
        binding.date.setText(Constants.getFormattedDate(taskModel.getDate().getDate().getTime()));
        Glide.with(this).load(taskModel.getTaskImage()).placeholder(R.color.white).into(binding.eImage);

        getUsers();

        binding.send.setOnClickListener(v -> {
            uploadChat();
        });

        binding.add.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.buttons);

            MaterialButton uploadImage = dialog.findViewById(R.id.uploadImage);
            MaterialButton uploadVideo = dialog.findViewById(R.id.uploadVideo);

            uploadImage.setOnClickListener(vv -> {
                dialog.dismiss();
                if (imagesList.size() >= limit) {
                    Toast.makeText(this, "Required Number of Images are selected", Toast.LENGTH_SHORT).show();
                } else {
                    getImage();
                }
            });

            uploadVideo.setOnClickListener(vv -> {
                dialog.dismiss();
                if (imagesList.size() >= limit) {
                    Toast.makeText(this, "Required Number of Images are selected", Toast.LENGTH_SHORT).show();
                } else {
                    getVideos();
                }
            });

            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        });

        binding.toolbar.title.setText("Event");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        Constants.databaseReference().child(Constants.CHATS).child(eventID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()){
                            ChatsModel chatsModel = snapshot.getValue(ChatsModel.class);
                            chatsList.add(chatsModel);
                            chatsList.sort(Comparator.comparing(ChatsModel::getTimestamps));
                            chatAdapter = new GalleryAdapter(EventDetailActivity.this, chatsList);
                            binding.chatRC.setAdapter(chatAdapter);
                            chatAdapter.notifyItemInserted(chatsList.size()-1);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()){

                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()){

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void getUsers() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.USER).child(taskModel.getUserID())
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
                    Glide.with(this).load(userModel.getImage()).placeholder(R.drawable.profile_icon).into(binding.user2);
                    Glide.with(this).load(stashUser.getImage()).placeholder(R.drawable.profile_icon).into(binding.user1);
                    Constants.dismissDialog();
//                    getThisMonthTasks();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Constants.createSnackbar(this, binding.getRoot(), e.getLocalizedMessage(), "Dismiss");
                });
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
                            Constants.databaseReference().child(Constants.CHATS).child(taskModel.getID())
                                    .push().setValue(sender).addOnSuccessListener(unused -> {
                                        binding.progressLayout.setVisibility(View.GONE);
                                        imagesList.clear();
                                        new FcmNotificationsSender("/topics/" + taskModel.getUserID(), "New Message", "You got a new Message", EventDetailActivity.this, EventDetailActivity.this).SendNotifications();
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

                        adapter = new AddImageAdapter(EventDetailActivity.this, imagesList, click);
                        binding.imagePreviewRC.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Please Select Multiple Images", Toast.LENGTH_SHORT).show();
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

                        adapter = new AddImageAdapter(EventDetailActivity.this, imagesList, click);
                        binding.imagePreviewRC.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Please Select Multiple Images", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    AddImageClick click = new AddImageClick() {
        @Override
        public void onClick(ShareContentModel uri, int position) {
            final Dialog dialog = new Dialog(EventDetailActivity.this);
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