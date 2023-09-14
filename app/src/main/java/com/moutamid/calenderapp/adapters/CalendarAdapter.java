package com.moutamid.calenderapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.moutamid.calenderapp.R;
import com.moutamid.calenderapp.models.CalendarDate;
import com.moutamid.calenderapp.utilis.Constants;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private Context context;
    private List<CalendarDate> calendarData;
    private OnDateClickListener onDateClickListener;

    public CalendarAdapter(Context context, List<CalendarDate> calendarData, OnDateClickListener onDateClickListener) {
        this.context = context;
        this.calendarData = calendarData;
        this.onDateClickListener = onDateClickListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.calendar_item, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        CalendarDate date = calendarData.get(position);

        // Customize the view for each calendar cell
        holder.dateTextView.setText(Constants.getDays(date.getDate()));

        switch (date.getMonthType()) {
            case CURRENT:
                holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                holder.card.setStrokeColor(context.getResources().getColor(R.color.stroke));

                if (date.isSelected()) {
                    holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.yellow));
                    holder.card.setStrokeColor(context.getResources().getColor(R.color.yellow));
                    holder.dashes.setVisibility(View.VISIBLE);
                } else {
                    holder.itemView.setOnClickListener(view -> onDateClickListener.onDateClick(date));
                }

                break;
            case NEXT:
            case PREVIOUS:
                holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                holder.card.setStrokeColor(context.getResources().getColor(R.color.white));
                break;
        }

        // Indicate today's date with a border
        if (date.isToday()) {
            holder.card.setStrokeColor(context.getResources().getColor(R.color.greenLight));
            holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.greenLight));
//            holder.dateTextView.setTextColor(context.getResources().getColor(R.color.white));
        }

    }

    @Override
    public int getItemCount() {
        return calendarData.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        MaterialCardView card;
        ImageView dashes;

        public CalendarViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            card = itemView.findViewById(R.id.card);
            dashes = itemView.findViewById(R.id.dashes);
        }
    }

    public interface OnDateClickListener {
        void onDateClick(CalendarDate date);
    }
}
