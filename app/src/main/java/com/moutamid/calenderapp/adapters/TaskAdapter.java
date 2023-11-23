package com.moutamid.calenderapp.adapters;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.interfaces.TaskClickListener;
import com.moutamid.calenderapp.models.TaskModel;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskVH> {
    Context context;
    ArrayList<TaskModel> list;
    TaskClickListener listener;


    public TaskAdapter(Context context, ArrayList<TaskModel> list, TaskClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskVH(LayoutInflater.from(context).inflate(R.layout.task_request_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskVH holder, int position) {
        TaskModel taskModel = list.get(holder.getAdapterPosition());

        holder.username.setText(taskModel.getUser().get(1).getUsername());
        holder.taskName.setText(taskModel.getName());
        Glide.with(context).load(taskModel.getUser().get(1).getImage()).placeholder(R.drawable.profile_icon).into(holder.profileImage);
        String date = new SimpleDateFormat("MMM dd", Locale.getDefault()).format(taskModel.getDate().getDate());
        holder.date.setText(date);

        holder.itemView.setOnClickListener(v -> {
            listener.onClick(taskModel);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TaskVH extends RecyclerView.ViewHolder{
        TextView username, taskName, date;
        CircleImageView profileImage;
        public TaskVH(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            taskName = itemView.findViewById(R.id.taskName);
            date = itemView.findViewById(R.id.date);
            profileImage = itemView.findViewById(R.id.profileImage);
        }
    }

}
