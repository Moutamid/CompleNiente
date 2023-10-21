package com.moutamid.calenderapp.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.activities.NewEventActivity;
import com.moutamid.calenderapp.activities.SelectUserActivity;
import com.moutamid.calenderapp.activities.UserProfileActivity;
import com.moutamid.calenderapp.models.CalendarDate;
import com.moutamid.calenderapp.models.TaskModel;
import com.moutamid.calenderapp.models.UserModel;
import com.moutamid.calenderapp.notifications.FcmNotificationsSender;
import com.moutamid.calenderapp.utilis.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserVH> implements Filterable {
    Context context;
    ArrayList<UserModel> list;
    ArrayList<UserModel> listAll;
    ArrayList<TaskModel> calendarTaskList;
    Activity activity;

    public UsersAdapter(Context context, Activity activity, ArrayList<UserModel> list) {
        this.context = context;
        this.activity = activity;
        this.list = list;
        this.listAll = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserVH(LayoutInflater.from(context).inflate(R.layout.users_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserVH holder, int position) {
        UserModel model = list.get(holder.getAdapterPosition());

        holder.name.setText(model.getName());
        holder.username.setText("@" + model.getUsername());
        holder.bio.setText(model.getBio());
        Glide.with(context).load(model.getImage()).placeholder(R.drawable.profile_icon).into(holder.profileImage);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date dateOfBirth;
        try {
            dateOfBirth = sdf.parse(model.getDob());
            int age = calculateAge(dateOfBirth);
            holder.age.setText("Age " + age);
        } catch (ParseException e) {
           e.getLocalizedMessage();
        }

        holder.start.setOnClickListener(v -> {
            Stash.put("PassUser", model);
            context.startActivity(new Intent(context, UserProfileActivity.class));
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private int calculateAge(Date dateOfBirth) {
        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dateOfBirth);

        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<UserModel> filterList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filterList.addAll(listAll);
            } else {
                for (UserModel listModel : listAll){
                    if (listModel.getUsername().contains(constraint.toString().toLowerCase())){
                        filterList.add(listModel);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends UserModel>) results.values);
            notifyDataSetChanged();
        }
    };

    public class UserVH extends RecyclerView.ViewHolder {
        TextView name, username, bio, age;
        CircleImageView profileImage;
        MaterialButton start;

        public UserVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
            bio = itemView.findViewById(R.id.bio);
            age = itemView.findViewById(R.id.age);
            profileImage = itemView.findViewById(R.id.profileImage);
            start = itemView.findViewById(R.id.start);
        }
    }

}
