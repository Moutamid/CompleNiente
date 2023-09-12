package com.moutamid.calenderapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.activities.AddTaskActivity;
import com.moutamid.calenderapp.adapters.CalendarAdapter;
import com.moutamid.calenderapp.databinding.FragmentHomeBinding;
import com.moutamid.calenderapp.models.CalendarDate;
import com.moutamid.calenderapp.models.MonthType;
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
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);
        context = binding.getRoot().getContext();

        Constants.initDialog(context);

        binding.greeting.setText(Constants.greetingMessage());

        String d = new SimpleDateFormat(Constants.MONTH_FORMAT, Locale.getDefault()).format(new Date());
        binding.month.setText(d);

        if (Stash.getString(Constants.USERNAME, "").isEmpty()){
            getUserDetails();
        } else {
            binding.name.setText(Stash.getString(Constants.USERNAME, ""));
            Glide.with(context).load(Stash.getString(Constants.USER_IMAGE, "")).placeholder(R.drawable.profile_icon).into(binding.profileImage);
        }

        GridLayoutManager layoutManager = new GridLayoutManager(context, 7);
        binding.calendarRecyclerView.setLayoutManager(layoutManager);

        CalendarAdapter adapter = new CalendarAdapter(context, generateCalendarData(), onDateClickListener);
        binding.calendarRecyclerView.setAdapter(adapter);

        return binding.getRoot();
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

            calendarData.add(new CalendarDate(date, false, isToday, monthType));
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
        }
    };

    private void getUserDetails() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.USER).child(Constants.auth().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()){
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        Stash.put(Constants.USERNAME, userModel.getName());
                        Stash.put(Constants.USER_IMAGE, userModel.getImage());
                        Stash.put(Constants.EMAIL, userModel.getEmail());
                        binding.name.setText(userModel.getName());
                        Glide.with(context).load(userModel.getImage()).placeholder(R.drawable.profile_icon).into(binding.profileImage);
                    } else {
                        Constants.createSnackbar(context, binding.getRoot(), "User data not found");
                    }
                    Constants.dismissDialog();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Constants.createSnackbar(context, binding.getRoot(), e.getLocalizedMessage(), "Dismiss");
                });
    }
}