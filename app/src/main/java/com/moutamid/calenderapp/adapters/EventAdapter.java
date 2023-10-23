package com.moutamid.calenderapp.adapters;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.models.TaskModel;

import java.util.ArrayList;
import java.util.Locale;

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
        String date = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(taskModel.getDate().getDate());
        holder.date.setText(date);

        holder.itemView.setOnClickListener(v -> {

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
