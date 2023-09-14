package com.moutamid.calenderapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.models.ChatsModel;
import com.moutamid.calenderapp.utilis.Constants;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatVH> {

    private Context context;
    private ArrayList<ChatsModel> list;

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    public ChatAdapter(Context context, ArrayList<ChatsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChatVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_LEFT) {
            view = LayoutInflater.from(context).inflate(R.layout.chat_row_left, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.chat_row_right, parent, false);
        }
        return new ChatVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatVH holder, int position) {
        ChatsModel model = list.get(holder.getAdapterPosition());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get currently signed in user
        return Constants.auth().getCurrentUser().getUid().equals(list.get(position).getSenderID()) ? MSG_TYPE_RIGHT : MSG_TYPE_LEFT;
    }

    public class ChatVH extends RecyclerView.ViewHolder{
        public ChatVH(@NonNull View itemView) {
            super(itemView);
        }
    }

}
