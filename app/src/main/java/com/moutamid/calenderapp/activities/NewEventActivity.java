package com.moutamid.calenderapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.chip.Chip;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.database.DataSnapshot;
import com.moutamid.calenderapp.databinding.ActivityNewEventBinding;
import com.moutamid.calenderapp.models.CalendarDate;
import com.moutamid.calenderapp.models.MonthType;
import com.moutamid.calenderapp.models.TaskModel;
import com.moutamid.calenderapp.models.UserModel;
import com.moutamid.calenderapp.notifications.FcmNotificationsSender;
import com.moutamid.calenderapp.utilis.Constants;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import noman.weekcalendar.listener.OnDateClickListener;

public class NewEventActivity extends AppCompatActivity {
    ActivityNewEventBinding binding;
    UserModel userModel;
    final Calendar calendar = Calendar.getInstance();
    Uri imageUri;
    long milies;
    CalendarDate date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Constants.initDialog(this);

        userModel = (UserModel) Stash.getObject("PassUser", UserModel.class);

        binding.friendName.setText(userModel.getName());

        binding.toolbar.title.setText("Create Event");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        milies = new Date().getTime();

        reminder = milies;
        binding.hour.setText(Constants.getHours(reminder));
        binding.minute.setText(Constants.getMinutes(reminder));
        binding.zone.setText(Constants.getZone(reminder));

        Date d = new Date(milies);
        date = new CalendarDate(d, true, true, getMonthType());

        String month = new SimpleDateFormat("MMMM dd", Locale.getDefault()).format(milies);
        binding.month.setText(month);

        binding.eventImage.setOnClickListener(v -> {
            ImagePicker.with(this).start(1);
        });

        binding.datePicker.setOnClickListener(v -> openTimePicker());

        binding.start.setOnClickListener(v -> {
            ArrayList<TaskModel> calendarTaskList = new ArrayList<>();
            Constants.databaseReference().child(Constants.ACTIVE_TASKS).child(Constants.CurrentMonth()).child(userModel.getID())
                    .get().addOnSuccessListener(snapshot -> {
                        Constants.dismissDialog();
                        if (snapshot.exists()) {
                            calendarTaskList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                TaskModel taskModel = dataSnapshot.getValue(TaskModel.class);
                                if (!taskModel.isEnded()) {
                                    calendarTaskList.add(taskModel);
                                }
                            }
                            CalendarDate date = (CalendarDate) Stash.getObject(Constants.DATE, CalendarDate.class);
                            if (calendarTaskList.size() > 0) {
                                boolean isSelected = false;
                                for (TaskModel taskModel : calendarTaskList) {
                                    String dayMonth = "ddMM";
                                    String listDate = new SimpleDateFormat(dayMonth, Locale.getDefault()).format(taskModel.getDate().getDate());
                                    String calenderDate = new SimpleDateFormat(dayMonth, Locale.getDefault()).format(date.getDate());
                                    if (listDate.equals(calenderDate)) {
                                        isSelected = taskModel.getDate().isSelected();
                                        break;
                                    }
                                }

                                if (isSelected) {
                                    Toast.makeText(this, "User is not available for today", Toast.LENGTH_SHORT).show();
                                } else {
                                    startEvent();
                                }

                            } else {
                                startEvent();
                            }

                        } else {
                            startEvent();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Constants.dismissDialog();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        binding.next.setOnClickListener(v -> {
            binding.weekCalendar.moveToNext();
        });
        binding.prev.setOnClickListener(v -> {
            binding.weekCalendar.moveToPrevious();
        });

        binding.weekCalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(DateTime dateTime) {
                milies = dateTime.getMillis();
                String month = new SimpleDateFormat("MMMM dd", Locale.getDefault()).format(milies);
                String cc = new SimpleDateFormat("MMMM dd", Locale.getDefault()).format(new Date());
                binding.month.setText(month);
                Date d = new Date(milies);
                boolean isToday = month.equals(cc);
                date = new CalendarDate(d, true, isToday, getMonthType());
            }

        });

    }

    private MonthType getMonthType() {
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.setTimeInMillis(milies);

        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        int month = selectedCalendar.get(Calendar.MONTH);
        int year = selectedCalendar.get(Calendar.YEAR);

        if (year == currentYear && month == currentMonth) {
            return MonthType.CURRENT;
        } else if (year < currentYear || (year == currentYear && month < currentMonth)) {
            return MonthType.PREVIOUS;
        } else {
            return MonthType.NEXT;
        }
    }

    private void startEvent() {
        String MONTH = Constants.CurrentMonth();
        date.setSelected(false);
        if (valid()) {
            Constants.showDialog();
            String ID = UUID.randomUUID().toString();
            Constants.storageReference(Constants.auth().getCurrentUser().getUid()).child(Constants.EventsPics).putFile(imageUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                    TaskModel sendTaskModel = getSenderModel(ID, uri.toString());
                                    TaskModel recieverTaskModel = getReceiverModel(ID, uri.toString());

                                    Constants.databaseReference().child(Constants.REQUESTS).child(MONTH).child(userModel.getID()).child(ID).setValue(recieverTaskModel)
                                            .addOnSuccessListener(unused -> {
                                                Constants.databaseReference().child(Constants.SEND_REQUESTS).child(MONTH).child(Constants.auth().getCurrentUser().getUid()).child(ID).setValue(sendTaskModel)
                                                        .addOnSuccessListener(unused1 -> {
                                                            new FcmNotificationsSender("/topics/" + userModel.getID(), "Incoming Request", "Someone want to work with you", this, this).SendNotifications();
                                                            Constants.dismissDialog();
                                                            Toast.makeText(this, "A request is send to the user", Toast.LENGTH_SHORT).show();
                                                        }).addOnFailureListener(e -> {
                                                            Constants.dismissDialog();
                                                            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                        });
                                            }).addOnFailureListener(e -> {
                                                Constants.dismissDialog();
                                                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            });

                                }).addOnFailureListener(e -> {
                                    Constants.dismissDialog();
                                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                });
                            }).addOnFailureListener(e -> {
                        Constants.dismissDialog();
                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private TaskModel getSenderModel(String ID, String image) {
        TaskModel taskModel = new TaskModel();

        taskModel.setID(ID);
        taskModel.setName(binding.name.getEditText().getText().toString());
        taskModel.setLocation(binding.location.getEditText().getText().toString());
        taskModel.setUserID(userModel.getID());
        taskModel.setUsername(userModel.getName());
        taskModel.setUserImage(userModel.getImage());
        taskModel.setUserHandle(userModel.getUsername());
        taskModel.setEnded(false);
        taskModel.setDescription("");
        taskModel.setDate(date);
        taskModel.setRecurrence(getRecurrence());
        taskModel.setIsAccepted(Constants.PEN);
        taskModel.setTaskImage(image);
        taskModel.setStartTime(reminder);

        return taskModel;
    }

    private TaskModel getReceiverModel(String ID, String image) {
        TaskModel taskModel = new TaskModel();
        UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        taskModel.setID(ID);
        taskModel.setName(binding.name.getEditText().getText().toString());
        taskModel.setLocation(binding.location.getEditText().getText().toString());
        taskModel.setUserID(stashUser.getID());
        taskModel.setUsername(stashUser.getName());
        taskModel.setUserImage(stashUser.getImage());
        taskModel.setUserHandle(stashUser.getUsername());
        taskModel.setEnded(false);
        taskModel.setDescription("");
        taskModel.setDate(date);
        taskModel.setRecurrence(getRecurrence());
        taskModel.setIsAccepted(Constants.PEN);
        taskModel.setTaskImage(image);
        taskModel.setStartTime(reminder);
        return taskModel;
    }

    private String getRecurrence() {
        String rec = "";
        for (int i = 0; i < binding.queenSourceChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) binding.queenSourceChipGroup.getChildAt(i);
            if (chip.isChecked()) {
                rec = chip.getText().toString();
            }
        }
        return rec;
    }

    private boolean valid() {
        if (imageUri != null){
            Toast.makeText(this, "Add Event Image", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.name.getEditText().getText().toString().isEmpty()){
            Toast.makeText(this, "Name is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.location.getEditText().getText().toString().isEmpty()){
            Toast.makeText(this, "Location is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (getRecurrence().isEmpty()){
            Toast.makeText(this, "Select Recurrence", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void openTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setMinute(0)
                .setTitleText("Select Time")
                .setPositiveButtonText("Add")
                .setNegativeButtonText("Cancel")
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            reminder = calendar.getTimeInMillis();

            binding.hour.setText(Constants.getHours(reminder));
            binding.minute.setText(Constants.getMinutes(reminder));
            binding.zone.setText(Constants.getZone(reminder));
        });

        timePicker.show(getSupportFragmentManager(), "timePicker");
    }
    long reminder;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                imageUri = data.getData();
                binding.addtext.setVisibility(View.GONE);
                binding.eImage.setVisibility(View.VISIBLE);
                binding.eImage.setImageURI(imageUri);
            }
        }
    }
}