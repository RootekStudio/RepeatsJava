package com.rootekstudio.repeatsandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.TimeViewHolder> {
    private List<AdvancedTimeItem> advancedTimeItems;

    public static class TimeViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView name, days, hours, frequency, sets;

        public TimeViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.cardViewAdvanced);
            name = view.findViewById(R.id.advancedList_name);
            days = view.findViewById(R.id.advancedList_days);
            hours = view.findViewById(R.id.advancedList_hours);
            frequency = view.findViewById(R.id.advancedList_freq);
            sets = view.findViewById(R.id.advancedList_sets);
        }
    }

    public TimeAdapter(List<AdvancedTimeItem> timeItems){
        advancedTimeItems = timeItems;
    }

    @NonNull
    @Override
    public TimeAdapter.TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_row, parent, false);
        TimeViewHolder tvh = new TimeViewHolder(v);
        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TimeAdapter.TimeViewHolder holder, int position) {
        AdvancedTimeItem item = advancedTimeItems.get(position);
        holder.cardView.setTag(item.getId());
        holder.name.setText(item.getName());
        holder.days.setText(item.getDays());
        holder.hours.setText(item.getHours());
        holder.frequency.setText(item.getFrequency());
        holder.sets.setText(item.getSets());
    }

    @Override
    public int getItemCount() {
        return advancedTimeItems.size();
    }
}
