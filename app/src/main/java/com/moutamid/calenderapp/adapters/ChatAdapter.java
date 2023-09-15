package com.moutamid.calenderapp.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.media.MediaMetadataRetriever;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.models.ChatsModel;
import com.moutamid.calenderapp.utilis.Constants;

import java.util.ArrayList;
import java.util.Locale;

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

        String date = new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(model.getTimestamps());
        holder.time.setText(date);

        if (model.getType().equals("img")) {
            holder.video.setVisibility(View.GONE);
            Glide.with(context).load(model.getMessage()).placeholder(R.color.white).into(holder.image);
        } else {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(model.getMessage());

            long timeInMicros = 1000000; // 1 second in microseconds
            android.graphics.Bitmap thumbnail = retriever.getFrameAtTime(timeInMicros);

            Glide.with(context)
                    .load(thumbnail)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(holder.image);
        }

        holder.itemView.setOnClickListener(v -> {
            showFullPreview(model);
        });

    }

    private void showFullPreview(ChatsModel model) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.full_preview);

        ImageView imageView = dialog.findViewById(R.id.imageView);
        VideoView videoView = dialog.findViewById(R.id.videoView);
        MaterialCardView back = dialog.findViewById(R.id.back);

        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        back.setOnClickListener(v -> dialog.dismiss());

        boolean isImage = model.getType().equals("img");
        if (isImage){
            videoView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.GONE);
        }

        if (isImage){
            Glide.with(context).load(model.getMessage()).placeholder(R.color.black).into(imageView);
        } else {
            videoView.setVideoPath(model.getMessage());
            videoView.start();
        }

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

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
        ImageView image, video;
        TextView time;
        public ChatVH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            video = itemView.findViewById(R.id.video);
            time = itemView.findViewById(R.id.time);
        }
    }

}
