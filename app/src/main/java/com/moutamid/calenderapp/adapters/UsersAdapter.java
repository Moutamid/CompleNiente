package com.moutamid.calenderapp.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import com.moutamid.calenderapp.R;
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

    public UsersAdapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
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
            Constants.initDialog(context);
            showTaskRequestDialog(model);
        });

    }

    private void showTaskRequestDialog(UserModel model) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.task_request_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        MaterialButton request = dialog.findViewById(R.id.add);
        TextInputLayout name = dialog.findViewById(R.id.name);
        TextInputLayout description = dialog.findViewById(R.id.description);

        request.setOnClickListener(v -> {
            dialog.dismiss();
            String MONTH = Constants.CurrentMonth();
            CalendarDate date = (CalendarDate) Stash.getObject(Constants.DATE, CalendarDate.class);
            date.setSelected(true);
            if (name.getEditText().getText().toString().isEmpty() || description.getEditText().getText().toString().isEmpty()){
                Toast.makeText(context, "Please fill all data", Toast.LENGTH_SHORT).show();
            } else {
                Constants.showDialog();
                String ID = UUID.randomUUID().toString();
                TaskModel sendTaskModel = new TaskModel(ID,
                        name.getEditText().getText().toString(), description.getEditText().getText().toString(),
                        model.getID(), model.getName(), model.getUsername(), model.getImage(),
                        date, false, false);
                UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
                TaskModel recieveTaskModel = new TaskModel(ID,
                        name.getEditText().getText().toString(), description.getEditText().getText().toString(),
                        stashUser.getID(), stashUser.getName(), stashUser.getUsername(), stashUser.getImage(),
                        date, false, false);
                Constants.databaseReference().child(Constants.REQUESTS).child(MONTH).child(model.getID()).child(ID).setValue(recieveTaskModel)
                        .addOnSuccessListener(unused -> {
                            Constants.databaseReference().child(Constants.SEND_REQUESTS).child(MONTH).child(Constants.auth().getCurrentUser().getUid()).child(ID).setValue(sendTaskModel)
                                    .addOnSuccessListener(unused1 -> {
                                        new FcmNotificationsSender(model.getID(), "Incoming Request", "Someone want to work with you", context, (Activity) context).SendNotifications();
                                        Constants.dismissDialog();
                                        Toast.makeText(context, "A request is send to the user", Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(e -> {
                                        Constants.dismissDialog();
                                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }).addOnFailureListener(e -> {
                            Constants.dismissDialog();
                            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });

            }
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
