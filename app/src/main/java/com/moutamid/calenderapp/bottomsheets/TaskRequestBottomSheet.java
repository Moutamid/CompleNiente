package com.moutamid.calenderapp.bottomsheets;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.databinding.TaskRequestBottomsheetBinding;
import com.moutamid.calenderapp.models.ChatListModel;
import com.moutamid.calenderapp.models.TaskModel;
import com.moutamid.calenderapp.models.UserModel;
import com.moutamid.calenderapp.notifications.FcmNotificationsSender;
import com.moutamid.calenderapp.utilis.Constants;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class TaskRequestBottomSheet extends BottomSheetDialogFragment {
    TaskRequestBottomsheetBinding binding;
    Context context;
    TaskModel model;
    boolean b;

    public TaskRequestBottomSheet(TaskModel model, boolean b) {
        this.model = model;
        this.b = b;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = TaskRequestBottomsheetBinding.inflate(getLayoutInflater(), container, false);
        context = binding.getRoot().getContext();

        Constants.initDialog(context);

        binding.username.setText(model.getUsername());
        binding.userID.setText("@" + model.getUserHandle());
        binding.taskName.setText(model.getName());
        binding.taskDesc.setText(model.getDescription());

        String date = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(model.getDate().getDate());
        binding.date.setText(date);

        Glide.with(context).load(model.getUserImage()).placeholder(R.drawable.profile_icon).into(binding.profileImage);

        if (b) {
            binding.accept.setVisibility(View.GONE);
            binding.endReject.setText("Reject/Delete");
        }

        if (model.isAccepted().equals(Constants.YES)) {
            binding.accept.setVisibility(View.GONE);
            binding.endReject.setText("End Task");
        } else {
            if (!b) {
                binding.accept.setVisibility(View.VISIBLE);
            }
            binding.endReject.setText("Reject/Delete");
        }

        if (model.isAccepted().equals(Constants.YES)) {
            binding.status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
            binding.statusText.setText("Accepted");
        } else if (model.isAccepted().equals(Constants.REJ)) {
            binding.status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            binding.statusText.setText("Rejected");
        } else if (model.isAccepted().equals(Constants.PEN)) {
            binding.status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange2)));
            binding.statusText.setText("Pending");
        }


        binding.accept.setOnClickListener(v -> {
            Constants.showDialog();
            model.setAccepted(Constants.YES);
            model.getDate().setSelected(true);
            UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
            Constants.databaseReference().child(Constants.ACTIVE_TASKS).child(Constants.CurrentMonth()).child(Constants.auth().getCurrentUser().getUid()).child(model.getID()).setValue(model).addOnSuccessListener(unused -> {
                Constants.databaseReference().child(Constants.ACTIVE_TASKS).child(Constants.CurrentMonth()).child(model.getUserID()).child(model.getID()).setValue(model).addOnSuccessListener(unused1 -> {
                    Constants.databaseReference().child(Constants.SEND_REQUESTS).child(Constants.CurrentMonth()).child(Constants.auth().getCurrentUser().getUid()).child(model.getID()).setValue(model).addOnSuccessListener(unused2 -> {
                        Constants.databaseReference().child(Constants.SEND_REQUESTS).child(Constants.CurrentMonth()).child(model.getUserID()).child(model.getID()).setValue(model).addOnSuccessListener(unused8 -> {
                            Constants.databaseReference().child(Constants.REQUESTS).child(Constants.CurrentMonth()).child(Constants.auth().getCurrentUser().getUid()).child(model.getID()).removeValue().addOnSuccessListener(unused6 -> {
                                String ID = UUID.randomUUID().toString();
                                ChatListModel sender = new ChatListModel(ID, stashUser.getImage(), stashUser.getName(), "Start sharing content", model.getID(), stashUser.getID(), model.getDate().getDate());
                                ChatListModel receiver = new ChatListModel(ID, model.getUserImage(), model.getUsername(), "Start sharing content", model.getID(), model.getUserID(), model.getDate().getDate());
                                Constants.databaseReference().child(Constants.CHAT_LIST).child(Constants.auth().getCurrentUser().getUid()).child(ID).setValue(receiver).addOnSuccessListener(unused3 -> {
                                    Constants.databaseReference().child(Constants.CHAT_LIST).child(model.getUserID()).child(ID).setValue(sender).addOnSuccessListener(unused4 -> {
                                        new FcmNotificationsSender(model.getUserID(), "Request Accepted", "Your Request for \'" + model.getName() + "\' is accepted", context, (Activity) context).SendNotifications();
                                        Toast.makeText(context, "Task Accepted", Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(e -> {
                                        Constants.dismissDialog();
                                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    });
                                }).addOnFailureListener(e -> {
                                    Constants.dismissDialog();
                                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                });
                            }).addOnFailureListener(e -> {
                                Constants.dismissDialog();
                                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }).addOnFailureListener(e -> {
                            Constants.dismissDialog();
                            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }).addOnFailureListener(e -> {
                        Constants.dismissDialog();
                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Constants.dismissDialog();
                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            });

            dismiss();
        });

        binding.endReject.setOnClickListener(v -> {
            Constants.showDialog();
            if (model.isAccepted().equals(Constants.PEN) || model.isAccepted().equals(Constants.REJ)) {
                rejectRequest();
            } else {
                endTask();
            }

            dismiss();
        });


        return binding.getRoot();
    }

    private void endTask() {
        String MONTH = Constants.CurrentMonth();
        Constants.databaseReference().child(Constants.SEND_REQUESTS).child(MONTH).child(Constants.auth().getCurrentUser().getUid()).child(model.getID()).removeValue().addOnSuccessListener(unused1 -> {
            Constants.dismissDialog();
            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Constants.dismissDialog();
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void rejectRequest() {
        String MONTH = Constants.CurrentMonth();
        if (b) {
            Constants.databaseReference().child(Constants.SEND_REQUESTS).child(MONTH).child(Constants.auth().getCurrentUser().getUid()).child(model.getID()).removeValue().addOnSuccessListener(unused -> {
                Constants.databaseReference().child(Constants.REQUESTS).child(MONTH).child(model.getUserID()).child(model.getID()).removeValue().addOnSuccessListener(unused1 -> {
                    Constants.dismissDialog();
                    new FcmNotificationsSender(model.getUserID(), "Task Ended", "Your Request for \'" + model.getName() + "\' is rejected", context, (Activity) context).SendNotifications();
                    Toast.makeText(context, "Task Ended", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Constants.dismissDialog();
                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            model.setAccepted(Constants.REJ);
            model.getDate().setSelected(false);
            UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
            model.setUserID(stashUser.getID());
            model.setUserImage(stashUser.getImage());
            model.setUserHandle(stashUser.getUsername());
            model.setUsername(stashUser.getName());
            Constants.databaseReference().child(Constants.SEND_REQUESTS).child(MONTH).child(model.getUserID()).child(model.getID()).setValue(model).addOnSuccessListener(unused -> {
                Constants.databaseReference().child(Constants.REQUESTS).child(MONTH).child(Constants.auth().getCurrentUser().getUid()).child(model.getID()).removeValue().addOnSuccessListener(unused1 -> {
                    Constants.dismissDialog();
                    new FcmNotificationsSender(model.getUserID(), "Task Rejected", "\'" + model.getName() +"\' is ended", context, (Activity) context).SendNotifications();
                    Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Constants.dismissDialog();
                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
/*        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            BottomSheetBehavior.from((View) getView().getParent()).setState(BottomSheetBehavior.STATE_EXPANDED);
        }*/
    }

}
