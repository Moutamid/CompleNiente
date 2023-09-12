package com.moutamid.calenderapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.interfaces.AddImageClick;

import java.util.ArrayList;

public class AddImageAdapter extends RecyclerView.Adapter<AddImageAdapter.ImageViewHolder> {
    Context context;
    ArrayList<Uri> imagesList;
    AddImageClick click;
    private final int limit = 5;

    public AddImageAdapter(Context context, ArrayList<Uri> imagesList, AddImageClick click) {
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
        holder.image.setImageURI(imagesList.get(holder.getAdapterPosition()));
        holder.itemView.setOnClickListener(v -> click.onClick(imagesList.get(holder.getAdapterPosition()), holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        if (imagesList.size() > limit){
            return limit;
        }
        return imagesList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }

    }

}
