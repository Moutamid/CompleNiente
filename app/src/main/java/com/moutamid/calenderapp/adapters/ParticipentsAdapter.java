package com.moutamid.calenderapp.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.os.UserManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.activities.UserProfileActivity;
import com.moutamid.calenderapp.models.ChatsModel;
import com.moutamid.calenderapp.models.UserModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticipentsAdapter extends RecyclerView.Adapter<ParticipentsAdapter.ChatVH> {

    private Context context;
    private ArrayList<UserModel> list;

    public ParticipentsAdapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChatVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatVH(LayoutInflater.from(context).inflate(R.layout.participents, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatVH holder, int position) {
        UserModel model = list.get(holder.getAdapterPosition());
        Glide.with(context).load(model.getImage()).placeholder(R.drawable.profile_icon).into(holder.image);
        holder.itemView.setOnClickListener(v -> {
            context.startActivity(new Intent(context, UserProfileActivity.class).putExtra("userID", model.getID()));
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatVH extends RecyclerView.ViewHolder {
        CircleImageView image;

        public ChatVH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }

}
