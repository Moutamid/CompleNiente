package com.moutamid.calenderapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.chip.Chip;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.database.DataSnapshot;
import com.moutamid.calenderapp.R;
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

import de.hdodenhof.circleimageview.CircleImageView;
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

        userModel = (UserModel) Stash.getObject("PassUser", UserModel.class);
        UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);

        userModel.setPassword("");
        stashUser.setPassword("");

        particepents.add(userModel);
        particepents.add(stashUser);

        setProfileImages();

        binding.addMore.setOnClickListener(v -> {
            startActivity(new Intent(this, SelectUserActivity.class));
        });

        binding.toolbar.title.setText("Create Event");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        milies = new Date().getTime();

        reminder = milies;
        binding.hour.setText(Constants.getHours(reminder));
        binding.minute.setText(Constants.getMinutes(reminder));
//        binding.zone.setText(Constants.getZone(reminder));

        Date d = new Date(milies);
        date = new CalendarDate(d, false, true, getMonthType());
        Stash.put(Constants.DATE, date);
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

        binding.weekCalendar.setOnDateClickListener(dateTime -> {
            milies = dateTime.getMillis();
            String month1 = new SimpleDateFormat("MMMM dd", Locale.getDefault()).format(milies);
            String cc = new SimpleDateFormat("MMMM dd", Locale.getDefault()).format(new Date());
            binding.month.setText(month1);
            Date d1 = new Date(milies);
            boolean isToday = month1.equals(cc);
            date = new CalendarDate(d1, false, isToday, getMonthType());
            Stash.put(Constants.DATE, date);
        });
    }

    private void setProfileImages() {
        binding.imagesLayout.removeAllViews();
        for (int i = 0; i < particepents.size(); i++) {
            CircleImageView circleImageView = new CircleImageView(this);
            circleImageView.setBorderWidth(1);
            circleImageView.setBorderColor(getResources().getColor(R.color.stroke));
            float scale = getResources().getDisplayMetrics().density;
            int pixels = (int) (60 * scale + 0.5f);
            int leftMarginInPixels = (int) (10 * scale + 0.5f);

            circleImageView.setLayoutParams(new ViewGroup.LayoutParams(pixels, pixels));
            ViewGroup.LayoutParams layoutParams = circleImageView.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                // If yes, cast it to MarginLayoutParams
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                marginLayoutParams.leftMargin = leftMarginInPixels;
                circleImageView.setLayoutParams(marginLayoutParams);
            }

            Glide.with(this).load(particepents.get(i).getImage()).placeholder(R.drawable.profile_icon).into(circleImageView);
            binding.imagesLayout.addView(circleImageView);
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        UserModel selectedUser = (UserModel) Stash.getObject("SELECTED_USER", UserModel.class);
        if (selectedUser != null) {
            selectedUser.setPassword("");
            boolean check = false;
            for (UserModel user : particepents) {
                if (user.getID().equals(selectedUser.getID())) {
                    check = true;
                    break;
                }
            }
            if (!check) {
                particepents.add(selectedUser);
                setProfileImages();
                Stash.clear("SELECTED_USER");
            } else {
                Toast.makeText(this, "This user already selected!", Toast.LENGTH_SHORT).show();
            }
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
                            for (int i = 0; i < particepents.size(); i++) {
                                UserModel userModel1 = particepents.get(i);
                                if (!Constants.auth().getCurrentUser().getUid().equals(userModel1.getID())) {
                                    Constants.databaseReference().child(Constants.REQUESTS).child(userModel1.getID()).child(ID).setValue(recieverTaskModel)
                                            .addOnSuccessListener(unused -> {
                                                Constants.databaseReference().child(Constants.SEND_REQUESTS).child(Constants.auth().getCurrentUser().getUid()).child(ID).setValue(sendTaskModel)
                                                        .addOnSuccessListener(unused1 -> {
                                                            new FcmNotificationsSender("/topics/" + userModel1.getID(), "Incoming Request", "Someone want to work with you", this, this).SendNotifications();
                                                        }).addOnFailureListener(e -> {
                                                            Constants.dismissDialog();
                                                            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                        });
                                            }).addOnFailureListener(e -> {
                                                Constants.dismissDialog();
                                                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                }

                                if (i == particepents.size() - 1) {
                                    Constants.dismissDialog();
                                    Toast.makeText(this, "A request is send to all users", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }

                            }
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

    ArrayList<UserModel> particepents = new ArrayList<>();

    private TaskModel getSenderModel(String ID, String image) {
        TaskModel taskModel = new TaskModel();
        taskModel.setID(ID);
        taskModel.setName(binding.name.getEditText().getText().toString());
        taskModel.setLocation(binding.location.getEditText().getText().toString());
        taskModel.setUser(particepents);
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
        taskModel.setID(ID);
        taskModel.setName(binding.name.getEditText().getText().toString());
        taskModel.setLocation(binding.location.getEditText().getText().toString());
        taskModel.setUser(particepents);
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
        if (imageUri == null) {
            Toast.makeText(this, "Add Event Image", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.name.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Name is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.location.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Location is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (getRecurrence().isEmpty()) {
            Toast.makeText(this, "Select Recurrence", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void openTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
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
//            binding.zone.setText(Constants.getZone(reminder));
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