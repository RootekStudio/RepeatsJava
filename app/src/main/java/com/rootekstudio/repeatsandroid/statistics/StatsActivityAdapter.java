package com.rootekstudio.repeatsandroid.statistics;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rootekstudio.repeatsandroid.R;

import java.util.List;

public class StatsActivityAdapter extends RecyclerView.Adapter<StatsActivityAdapter.MainViewHolder> {
    private List<SetStats> setsStats;
    private int dp;

    static class MainViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;

        MainViewHolder(LinearLayout lr) {
            super(lr);
            linearLayout = lr;
        }
    }

    public StatsActivityAdapter(List<SetStats> setsStats, int dp) {
        this.setsStats = setsStats;
        this.dp = dp;
    }

    @Override
    public StatsActivityAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stats_single_set, parent, false);

        StatsActivityAdapter.MainViewHolder mainViewHolder = new StatsActivityAdapter.MainViewHolder(linearLayout);
        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(StatsActivityAdapter.MainViewHolder holder, int position) {
        LinearLayout view = holder.linearLayout;
        final SetStats item = setsStats.get(position);

        TextView setName = view.findViewById(R.id.setNameStatsItem);
        TextView goodPercentTextView = view.findViewById(R.id.goodPercentTextViewItem);
        TextView wrongPercentTextView = view.findViewById(R.id.wrongPercentTextViewItem);
        View goodPercentView = view.findViewById(R.id.goodProgress);
        View wrongPercentView = view.findViewById(R.id.wrongProgress);
        RelativeLayout relativeGraphSetStats = view.findViewById(R.id.relativeGraphSetStats);
        TextView clickToSeeDetailsTextView = view.findViewById(R.id.clickToSeeDetailsTextView);

        view.setOnClickListener(view1 -> {
            Intent intent = new Intent(view1.getContext(), SetStatsActivity.class);
            intent.putExtra("setID", item.getSetID());
            intent.putExtra("setName", item.getName());
            view1.getContext().startActivity(intent);
        });

        setName.setText(item.getName());

        if (item.getAllAnswers() != 0) {
            float goodPercentInt = (float) (item.getGoodAnswers() * 100) / item.getAllAnswers();
            String goodPercent = Math.round(goodPercentInt) + "%";
            goodPercentTextView.setText(goodPercent);

            float wrongPercentInt = (float) (item.getWrongAnswers() * 100) / item.getAllAnswers();
            String wrongPercent = Math.round(wrongPercentInt) + "%";
            wrongPercentTextView.setText(wrongPercent);

            float goodWidth = (goodPercentInt / 100) * dp;
            float wrongWidth = (wrongPercentInt / 100) * dp;

            RelativeLayout.LayoutParams goodLayoutParams = new RelativeLayout.LayoutParams(Math.round(goodWidth), 10);
            goodLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            goodLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            RelativeLayout.LayoutParams wrongLayoutParams = new RelativeLayout.LayoutParams(Math.round(wrongWidth), 10);

            wrongLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            wrongLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            goodPercentView.setLayoutParams(goodLayoutParams);
            wrongPercentView.setLayoutParams(wrongLayoutParams);
        } else {
            relativeGraphSetStats.setVisibility(View.GONE);
            clickToSeeDetailsTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return setsStats.size();
    }
}
