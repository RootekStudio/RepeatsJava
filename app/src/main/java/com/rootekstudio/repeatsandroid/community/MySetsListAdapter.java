package com.rootekstudio.repeatsandroid.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rootekstudio.repeatsandroid.R;

import java.util.ArrayList;

public class MySetsListAdapter extends RecyclerView.Adapter<MySetsListAdapter.ListHolder> {
    private ArrayList<String> names;

    static class ListHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ListHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.setNameMySetsList);
        }
    }

    public MySetsListAdapter(ArrayList<String> n) {names = n;}

    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_setslist_singleitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListHolder holder, int position) {
        holder.textView.setText(names.get(position));
        holder.textView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }
}
