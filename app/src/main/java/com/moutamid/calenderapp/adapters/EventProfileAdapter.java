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
import com.moutamid.calenderapp.models.TaskModel;

import java.util.ArrayList;
import java.util.Locale;

public class EventProfileAdapter extends RecyclerView.Adapter<EventProfileAdapter.EventVH> {
    Context context;
    ArrayList<TaskModel> list;

    public EventProfileAdapter(Context context, ArrayList<TaskModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public EventVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventVH(LayoutInflater.from(context).inflate(R.layout.event_profile_card2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventVH holder, int position) {
        TaskModel taskModel = list.get(holder.getAdapterPosition());

        holder.name.setText(taskModel.getName());
        Glide.with(context).load(taskModel.getTaskImage()).placeholder(R.drawable.event).into(holder.eventImage);
        String date = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(taskModel.getDate().getDate());
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(taskModel.getStartTime());
        holder.date.setText(date +", "+time);

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
        TextView name, date;
        public EventVH(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
        }
    }

}
