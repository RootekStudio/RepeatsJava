package com.rootekstudio.repeatsandroid.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rootekstudio.repeatsandroid.R;

import java.util.ArrayList;

public class RCmainListAdapter extends RecyclerView.Adapter<RCmainListAdapter.ListHolder> {
    ArrayList<String> names;
    int mode;

    static class ListHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ListHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.setNameListItemRC);
        }
    }

    public RCmainListAdapter(ArrayList<String> n, int m) {names = n; mode = m;}

    @NonNull
    @Override
    public RCmainListAdapter.ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        if(mode == 0) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_listitem_main_rc, parent, false);
        }
        else if(mode == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_groupslist_rc, parent, false);
        }

        return new ListHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RCmainListAdapter.ListHolder holder, int position) {
        holder.textView.setText(names.get(position));
        holder.textView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }
}
