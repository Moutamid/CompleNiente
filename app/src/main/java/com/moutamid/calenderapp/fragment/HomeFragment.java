package com.moutamid.calenderapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.activities.SelectUserActivity;
import com.moutamid.calenderapp.activities.UserProfileActivity;
import com.moutamid.calenderapp.adapters.CalendarAdapter;
import com.moutamid.calenderapp.adapters.EventAdapter;
import com.moutamid.calenderapp.adapters.TaskAdapter;
import com.moutamid.calenderapp.bottomsheets.TaskRequestBottomSheet;
import com.moutamid.calenderapp.databinding.FragmentHomeBinding;
import com.moutamid.calenderapp.interfaces.TaskClickListener;
import com.moutamid.calenderapp.models.CalendarDate;
import com.moutamid.calenderapp.models.MonthType;
import com.moutamid.calenderapp.models.TaskModel;
import com.moutamid.calenderapp.models.UserModel;
import com.moutamid.calenderapp.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    public FragmentHomeBinding binding;
    Context context;

    ArrayList<TaskModel> taskList, calendarTaskList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);
        context = binding.getRoot().getContext();

//        setStatusBarTranslucent();

        binding.greeting.setText(Constants.greetingMessage());

        String d = new SimpleDateFormat(Constants.MONTH_FORMAT, Locale.getDefault()).format(new Date());
//        binding.month.setText(d);

        taskList = new ArrayList<>();
        calendarTaskList = new ArrayList<>();

//        GridLayoutManager layoutManager = new GridLayoutManager(context, 7);
//        binding.calendarRecyclerView.setLayoutManager(layoutManager);

        binding.RC.setLayoutManager(new LinearLayoutManager(context));
        binding.RC.setHasFixedSize(false);

        binding.create.setOnClickListener(v -> {
            if (binding.username.getEditText().getText().toString().isEmpty()) {
                Toast.makeText(context, "User name is empty", Toast.LENGTH_SHORT).show();
            } else {
                Constants.showDialog();
                Constants.databaseReference().child(Constants.USER).get()
                        .addOnSuccessListener(dataSnapshot -> {
                            Constants.dismissDialog();
                            if (dataSnapshot.exists()) {
                                UserModel temp = null;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    UserModel model = snapshot.getValue(UserModel.class);

                                    if (model.getUsername().equals(binding.username.getEditText().getText().toString().trim()) ||
                                            model.getEmail().equals(binding.username.getEditText().getText().toString().trim())) {
                                        temp = model;
                                        break;
                                    }
                                }

                                if (temp != null) {

                                    if (temp.getID().equals(Constants.auth().getCurrentUser().getUid())){
                                        Toast.makeText(context, "You can't create event with your self", Toast.LENGTH_SHORT).show();
                                    } else {
                                        context.startActivity(new Intent(context, UserProfileActivity.class).putExtra("userID", temp.getID()));
                                    }
                                } else {
                                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .addOnFailureListener(e -> {
                            Constants.dismissDialog();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.initDialog(requireContext());
        UserModel user = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        if (user == null) {
            getUserDetails();
        } else {
            binding.name.setText(user.getName());
            Glide.with(context).load(user.getImage()).placeholder(R.drawable.profile_icon).into(binding.profileImage);
//            getThisMonthTasks();
            getSendRequests();
        }

    }

    private void getSendRequests() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.SEND_REQUESTS).child(Constants.CurrentMonth()).child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            taskList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                TaskModel taskModel = dataSnapshot.getValue(TaskModel.class);
                                if (!taskModel.isEnded()) {
                                    taskList.add(taskModel);
                                }
                                if (taskList.size() > 0) {
                                    binding.RC.setVisibility(View.VISIBLE);
                                    binding.noItemLayout.setVisibility(View.GONE);
                                } else {
                                    binding.RC.setVisibility(View.GONE);
                                    binding.noItemLayout.setVisibility(View.VISIBLE);
                                }

                                EventAdapter adapter = new EventAdapter(context, taskList);
                                binding.RC.setAdapter(adapter);

                            }
                        }
                        Constants.dismissDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Constants.dismissDialog();
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    TaskClickListener listener = model -> {
        TaskRequestBottomSheet bottomSheetFragment = new TaskRequestBottomSheet(model, true);
        bottomSheetFragment.show(requireActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
    };

/*
    private void getThisMonthTasks() {
        Constants.databaseReference().child(Constants.ACTIVE_TASKS).child(Constants.CurrentMonth()).child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            calendarTaskList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                TaskModel taskModel = dataSnapshot.getValue(TaskModel.class);
                                if (!taskModel.isEnded()){
                                    calendarTaskList.add(taskModel);
                                }
                            }
                        }
                        CalendarAdapter calendarAdapter = new CalendarAdapter(context, generateCalendarData(), onDateClickListener);
                        binding.calendarRecyclerView.setAdapter(calendarAdapter);
                        Constants.dismissDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Constants.dismissDialog();
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private List<CalendarDate> generateCalendarData() {
        List<CalendarDate> calendarData = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        Calendar todayCalendar = Calendar.getInstance();

        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // Calculate the number of days in the previous month
        int daysInPreviousMonth = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - daysInPreviousMonth + 1);

        for (int i = 0; i < 42; i++) {
            Date date = calendar.getTime();
            boolean isToday = isToday(date, todayCalendar);
            MonthType monthType = getMonthType(calendar, currentMonth, currentYear);

            boolean isSelected = false;
            for (TaskModel model : calendarTaskList) {
                String dayMonth = "ddMM";
                String listDate = new SimpleDateFormat(dayMonth, Locale.getDefault()).format(model.getDate().getDate());
                String calenderDate = new SimpleDateFormat(dayMonth, Locale.getDefault()).format(date);
                if (listDate.equals(calenderDate)){
                    isSelected = model.getDate().isSelected();
                    break;
                }
            }
            calendarData.add(new CalendarDate(date, isSelected, isToday, monthType));
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        }

        return calendarData;
    }

    private MonthType getMonthType(Calendar calendar, int currentMonth, int currentYear) {
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        if (year == currentYear && month == currentMonth) {
            return MonthType.CURRENT;
        } else if (year < currentYear || (year == currentYear && month < currentMonth)) {
            return MonthType.PREVIOUS;
        } else {
            return MonthType.NEXT;
        }
    }

    private boolean isToday(Date date, Calendar todayCalendar) {
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        return todayCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR)
                && todayCalendar.get(Calendar.MONTH) == dateCalendar.get(Calendar.MONTH)
                && todayCalendar.get(Calendar.DAY_OF_MONTH) == dateCalendar.get(Calendar.DAY_OF_MONTH);
    }


    // Handle date click events
    private CalendarAdapter.OnDateClickListener onDateClickListener = new CalendarAdapter.OnDateClickListener() {
        @Override
        public void onDateClick(CalendarDate date) {
            // Handle click events here
            Stash.put(Constants.DATE, date);
            startActivity(new Intent(context, SelectUserActivity.class));
        }
    };

 */

    private void getUserDetails() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.USER).child(Constants.auth().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        Stash.put(Constants.STASH_USER, userModel);
                        binding.name.setText(userModel.getName());
                        Glide.with(context).load(userModel.getImage()).placeholder(R.drawable.profile_icon).into(binding.profileImage);
                    } else {
                        Constants.createSnackbar(context, binding.getRoot(), "User data not found");
                    }
                    Constants.dismissDialog();
//                    getThisMonthTasks();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Constants.createSnackbar(context, binding.getRoot(), e.getLocalizedMessage(), "Dismiss");
                });
    }

}