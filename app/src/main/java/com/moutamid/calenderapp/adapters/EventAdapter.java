package com.moutamid.calenderapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.activities.EventDetailActivity;
import com.moutamid.calenderapp.models.CalendarDate;
import com.moutamid.calenderapp.models.TaskModel;
import com.moutamid.calenderapp.utilis.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventVH> {
    Context context;
    ArrayList<TaskModel> list;

    public EventAdapter(Context context, ArrayList<TaskModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public EventVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventVH(LayoutInflater.from(context).inflate(R.layout.event_profile_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventVH holder, int position) {
        TaskModel taskModel = list.get(holder.getAdapterPosition());

        holder.name.setText(taskModel.getName());
        holder.location.setText(taskModel.getLocation());
        Glide.with(context).load(taskModel.getTaskImage()).placeholder(R.drawable.event).into(holder.eventImage);

        long currentDate = new Date().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        long timestamp = taskModel.getDate().getDate().getTime();

        if (timestamp > currentDate) {
            String formattedDate = dateFormat.format(new Date(timestamp));
            String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(taskModel.getStartTime());
            holder.date.setText(formattedDate +", "+time);
        } else {
            // Date has passed, adjust based on recurrence
            long adjustedDate = Constants.adjustDate(timestamp, taskModel.getRecurrence());
            TaskModel update = taskModel;
            CalendarDate calendarDate = update.getDate();
            calendarDate.setDate(new Date(adjustedDate));
            Map<String, Object> map = new HashMap<>();
            map.put("date", calendarDate);
            Constants.databaseReference().child(Constants.SEND_REQUESTS).child(Constants.auth().getCurrentUser().getUid())
                    .child(taskModel.getID()).updateChildren(map);
            String formattedDate = dateFormat.format(new Date(adjustedDate));
            String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(taskModel.getStartTime());
            holder.date.setText(formattedDate +", "+time);
        }

        holder.itemView.setOnClickListener(v -> {
            Stash.put("EVENT", taskModel);
            context.startActivity(new Intent(context, EventDetailActivity.class).putExtra("eventID", taskModel.getID()));
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class EventVH extends RecyclerView.ViewHolder{
        ImageView eventImage;
        TextView name, date, location;
        public EventVH(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            location = itemView.findViewById(R.id.location);
        }
    }

}
