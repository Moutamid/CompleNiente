package com.moutamid.calenderapp.adapters;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.interfaces.AddImageClick;
import com.moutamid.calenderapp.models.ShareContentModel;

import java.io.IOException;
import java.util.ArrayList;

public class AddImageAdapter extends RecyclerView.Adapter<AddImageAdapter.ImageViewHolder> {
    Context context;
    ArrayList<ShareContentModel> imagesList;
    AddImageClick click;
    private final int limit = 5;

    public AddImageAdapter(Context context, ArrayList<ShareContentModel> imagesList, AddImageClick click) {
        this.context = context;
        this.imagesList = imagesList;
        this.click = click;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ShareContentModel model = imagesList.get(holder.getAdapterPosition());
        holder.itemView.setOnClickListener(v -> click.onClick(imagesList.get(holder.getAdapterPosition()), holder.getAdapterPosition()));

        if (model.getType().equals("img")){
            holder.image.setImageURI(model.getUri());
            holder.video.setVisibility(View.GONE);
        } else {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                retriever.setDataSource(context, model.getUri());
                // Get the first frame as a bitmap
                android.graphics.Bitmap frame = retriever.getFrameAtTime(1000000); // 1 second in microseconds
                holder.image.setImageBitmap(frame);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    retriever.release();
                } catch (IOException e) {
                    e.printStackTrace();
                  //  throw new RuntimeException(e);
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        if (imagesList.size() > limit){
            return limit;
        }
        return imagesList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView image, video;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            video = itemView.findViewById(R.id.video);
        }

    }

}
