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
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.models.ChatsModel;

import java.util.ArrayList;
import java.util.Locale;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ChatVH> {

    private Context context;
    private ArrayList<ChatsModel> list;

    public GalleryAdapter(Context context, ArrayList<ChatsModel> list) {
        this.context = context;
        this.list = list;
    }

    private void showFullPreview(int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.full_preview);

        ViewPager2 pager = dialog.findViewById(R.id.viewPager);
        MaterialCardView back = dialog.findViewById(R.id.back);

        back.setOnClickListener(v -> dialog.dismiss());

        MediaPagerAdapter adapter = new MediaPagerAdapter(context, list);
        pager.setAdapter(adapter);

        pager.setCurrentItem(position);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

    }

    @NonNull
    @Override
    public ChatVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatVH(LayoutInflater.from(context).inflate(R.layout.gallery_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatVH holder, int position) {
        ChatsModel model = list.get(holder.getAdapterPosition());

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
            showFullPreview(holder.getAdapterPosition());
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatVH extends RecyclerView.ViewHolder{
        ImageView image, video;
        public ChatVH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            video = itemView.findViewById(R.id.video);
        }
    }

}
