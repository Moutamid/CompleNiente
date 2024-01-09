package com.moutamid.calenderapp.bottomsheets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
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

import java.util.Locale;
import java.util.UUID;

public class TaskRequestBottomSheet extends BottomSheetDialogFragment {
    TaskRequestBottomsheetBinding binding;
    Context context;
    TaskModel model;
    boolean b;
    private static final String TAG = "TaskRequestBottomSheet";

    public TaskRequestBottomSheet(TaskModel model, boolean b) {
        this.model = model;
        this.b = b;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = TaskRequestBottomsheetBinding.inflate(getLayoutInflater(), container, false);
        context = binding.getRoot().getContext();

        Constants.initDialog(context);

        binding.username.setText(model.getUser().get(1).getName());
        binding.userID.setText("@" + model.getUser().get(1).getUsername());
        binding.taskName.setText(model.getName());
        binding.taskDesc.setText(model.getDescription());

        String date = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(model.getDate().getDate());
        binding.date.setText(date);

        Glide.with(context).load(model.getUser().get(1).getImage()).placeholder(R.drawable.profile_icon).into(binding.profileImage);

        if (b) {
            binding.accept.setVisibility(View.GONE);
            binding.endReject.setText(getString(R.string.reject_delete));
        }

        if (model.isAccepted().equals(Constants.YES)) {
            binding.accept.setVisibility(View.GONE);
            binding.endReject.setText(getString(R.string.end_task));
        } else {
            if (!b) {
                binding.accept.setVisibility(View.VISIBLE);
            }
            binding.endReject.setText(getString(R.string.reject_delete));
        }

        if (model.isAccepted().equals(Constants.YES)) {
            binding.status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
            binding.statusText.setText(getString(R.string.accepted));
        } else if (model.isAccepted().equals(Constants.REJ)) {
            binding.status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            binding.statusText.setText(getString(R.string.rejected));
        } else if (model.isAccepted().equals(Constants.PEN)) {
            binding.status.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange2)));
            binding.statusText.setText(getString(R.string.pending));
        }


        binding.accept.setOnClickListener(v -> {
            Constants.showDialog();
            model.setAccepted(Constants.YES);
            model.getDate().setSelected(true);
            Log.d(TAG, "login: " + Constants.auth().getCurrentUser().getUid());

            UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
            Constants.databaseReference().child(Constants.ACTIVE_TASKS).child(Constants.auth().getCurrentUser().getUid()).child(model.getID()).setValue(model).addOnSuccessListener(unused -> {
                Constants.databaseReference().child(Constants.ACTIVE_TASKS).child(model.getUser().get(1).getID()).child(model.getID()).setValue(model).addOnSuccessListener(unused1 -> {

                    Constants.databaseReference().child(Constants.SEND_REQUESTS).child(Constants.auth().getCurrentUser().getUid()).child(model.getID()).setValue(model).addOnSuccessListener(unused2 -> {
                        Constants.databaseReference().child(Constants.SEND_REQUESTS).child(model.getUser().get(1).getID()).child(model.getID()).setValue(model).addOnSuccessListener(unused8 -> {
                            Constants.databaseReference().child(Constants.REQUESTS).child(Constants.auth().getCurrentUser().getUid()).child(model.getID()).removeValue().addOnSuccessListener(unused6 -> {
                                String ID = UUID.randomUUID().toString();
                                ChatListModel sender = new ChatListModel(ID, stashUser.getImage(), stashUser.getName(), "Task : " + model.getName(), model.getID(), stashUser.getID(), model.getDate().getDate());
                                ChatListModel receiver = new ChatListModel(ID, model.getUser().get(1).getImage(), model.getUser().get(1).getName(), "Task : " + model.getName(), model.getID(), model.getUser().get(1).getID(), model.getDate().getDate());
                                Constants.databaseReference().child(Constants.CHAT_LIST).child(Constants.auth().getCurrentUser().getUid()).child(ID).setValue(receiver).addOnSuccessListener(unused3 -> {
                                    Constants.databaseReference().child(Constants.CHAT_LIST).child(model.getUser().get(1).getID()).child(ID).setValue(sender).addOnSuccessListener(unused4 -> {
                                        Constants.dismissDialog();
                                        new FcmNotificationsSender("/topics/" + model.getUser().get(1).getID(), "Richiesta accettata", "La tua richiesta per '" + model.getName() + "' è stata accettata", context, requireActivity()).SendNotifications();
                                        dismiss();
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
        });

        binding.endReject.setOnClickListener(v -> {
            Constants.showDialog();
            if (model.isAccepted().equals(Constants.PEN) || model.isAccepted().equals(Constants.REJ)) {
                rejectRequest();
            } else {
                endTask();
            }
        });


        return binding.getRoot();
    }

    private void endTask() {
        String MONTH = Constants.CurrentMonth();
        model.getDate().setSelected(false);
        Constants.databaseReference().child(Constants.SEND_REQUESTS).child(Constants.auth().getCurrentUser().getUid()).child(model.getID()).removeValue().addOnSuccessListener(unused1 -> {
            Constants.databaseReference().child(Constants.ACTIVE_TASKS).child(Constants.auth().getCurrentUser().getUid()).child(model.getID()).setValue(model)
                    .addOnSuccessListener(unused -> {
                        Constants.databaseReference().child(Constants.ACTIVE_TASKS).child(model.getUser().get(1).getID()).child(model.getID()).setValue(model)
                                .addOnSuccessListener(unused2 -> {
                                    Constants.dismissDialog();
                                    new FcmNotificationsSender("/topics/" + model.getUser().get(1).getID(), "Attività terminata", "La tua attività '" + model.getName() + "' è terminata", context, requireActivity()).SendNotifications();
                                    Toast.makeText(context, "Attività terminata", Toast.LENGTH_SHORT).show();
                                    dismiss();
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
    }

    private void rejectRequest() {
        String MONTH = Constants.CurrentMonth();
        if (b) {
            Constants.databaseReference().child(Constants.SEND_REQUESTS).child(Constants.auth().getCurrentUser().getUid()).child(model.getID()).removeValue().addOnSuccessListener(unused -> {
                Constants.databaseReference().child(Constants.REQUESTS).child(model.getUser().get(1).getID()).child(model.getID()).removeValue().addOnSuccessListener(unused1 -> {
                    Constants.dismissDialog();
                    new FcmNotificationsSender("/topics/" + model.getUser().get(1).getID(), "Attività terminata", "La tua attività '" + model.getName() + "' è terminata", context, requireActivity()).SendNotifications();
                    Toast.makeText(context, "Attività terminata", Toast.LENGTH_SHORT).show();
                    dismiss();
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
            String userID = model.getUser().get(1).getID();
            UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
            Constants.databaseReference().child(Constants.SEND_REQUESTS).child(Constants.auth().getCurrentUser().getUid()).child(model.getID()).setValue(model).addOnSuccessListener(unused -> {
                Constants.databaseReference().child(Constants.SEND_REQUESTS).child(userID).child(model.getID()).setValue(model).addOnSuccessListener(unused1 -> {
                    Constants.databaseReference().child(Constants.REQUESTS).child(Constants.auth().getCurrentUser().getUid()).child(model.getID()).removeValue().addOnSuccessListener(unused2 -> {
                        Constants.dismissDialog();
                        new FcmNotificationsSender("/topics/" + userID, "Compito rifiutato", "La tua richiesta per '" + model.getName() + "' è stata respinta", context, requireActivity()).SendNotifications();
                        Toast.makeText(context, "Compito rifiutato", Toast.LENGTH_SHORT).show();
                        dismiss();
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
