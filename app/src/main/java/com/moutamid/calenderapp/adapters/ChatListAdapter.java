package com.moutamid.calenderapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.activities.ChatActivity;
import com.moutamid.calenderapp.models.ChatListModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListVH>{

    Context context;
    ArrayList<ChatListModel> list;

    public ChatListAdapter(Context context, ArrayList<ChatListModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChatListVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatListVH(LayoutInflater.from(context).inflate(R.layout.chat_item_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListVH holder, int position) {
        ChatListModel model = list.get(holder.getAdapterPosition());
        holder.username.setText(model.getName());

        holder.lastMessage.setText(model.getMessage());

        Glide.with(context).load(model.getImage()).placeholder(R.drawable.profile_icon).into(holder.profileImage);

        holder.itemView.setOnClickListener(v -> {
            context.startActivity(new Intent(context, ChatActivity.class).putExtra("ChatPerson", model));
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatListVH extends RecyclerView.ViewHolder{
        CircleImageView profileImage;
        MaterialTextView username, lastMessage;
        public ChatListVH(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            username = itemView.findViewById(R.id.username);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }

}
