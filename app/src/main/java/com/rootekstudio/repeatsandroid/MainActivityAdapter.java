package com.rootekstudio.repeatsandroid;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MainViewHolder> {
    private List<RepeatsListDB> repeatsList;

    static class MainViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        MainViewHolder(RelativeLayout rl) {
            super(rl);
            relativeLayout = rl;
        }
    }

    public MainActivityAdapter(List<RepeatsListDB> repeatsListDB) {
        repeatsList = repeatsListDB;
    }

    @Override
    public MainActivityAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mainactivitylistitem, parent, false);

        MainViewHolder mainViewHolder = new MainViewHolder(relativeLayout);
        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        RelativeLayout view = holder.relativeLayout;
        RepeatsListDB Item = repeatsList.get(position);

        RelativeLayout but = view.findViewById(R.id.RelativeMAIN);
        ImageButton options = view.findViewById(R.id.optionsMainItem);

        String tablename = Item.getTableName();
        String title = Item.getitle();
        String IgnoreChars = Item.getIgnoreChars();

        but.setTag(R.string.Tag_id_0, tablename);
        but.setTag(R.string.Tag_id_1, title);
        but.setTag(R.string.Tag_id_2, IgnoreChars);

        options.setTag(tablename);

        TextView Name = view.findViewById(R.id.NameTextView);
        TextView Date = view.findViewById(R.id.DateTextView);

        Name.setText(Item.getitle());
        Date.setText(Item.getCreateDate());
    }

    @Override
    public int getItemCount() {
        return repeatsList.size();
    }
}
