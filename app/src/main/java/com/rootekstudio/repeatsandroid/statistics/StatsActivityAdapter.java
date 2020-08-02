package com.rootekstudio.repeatsandroid.statistics;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
        TextView percentTextView = view.findViewById(R.id.percentTextSetStats);
        TextView correctTextView = view.findViewById(R.id.correctCountSetStats);
        TextView wrongTextView = view.findViewById(R.id.wrongCountSetStats);
        TextView allTextView = view.findViewById(R.id.allCountSetStats);
//        TextView goodPercentTextView = view.findViewById(R.id.goodPercentTextViewItem);
//        TextView wrongPercentTextView = view.findViewById(R.id.wrongPercentTextViewItem);
//        View goodPercentView = view.findViewById(R.id.goodProgress);
//        View wrongPercentView = view.findViewById(R.id.wrongProgress);
//        RelativeLayout relativeGraphSetStats = view.findViewById(R.id.relativeGraphSetStats);
        TextView clickToSeeDetailsTextView = view.findViewById(R.id.clickToSeeDetailsTextView);
        ProgressBar progressBar = view.findViewById(R.id.progressBarSetStats);

        view.setOnClickListener(view1 -> {
            Intent intent = new Intent(view1.getContext(), SetStatsActivity.class);
            intent.putExtra("setID", item.getSetID());
            intent.putExtra("setName", item.getName());
            view1.getContext().startActivity(intent);
        });

        setName.setText(item.getName());

        if (item.getAllAnswers() != 0) {
            float goodPercentFloat = (float) (item.getGoodAnswers() * 100) / item.getAllAnswers();
            int goodPercentInt = Math.round(goodPercentFloat);
            String goodPercent = goodPercentInt + "%";
            percentTextView.setText(goodPercent);
            progressBar.setProgress(goodPercentInt);

            correctTextView.setText(String.valueOf(item.getGoodAnswers()));
            wrongTextView.setText(String.valueOf(item.getWrongAnswers()));
            allTextView.setText(String.valueOf(item.getAllAnswers()));

        } else {
            progressBar.setBackground(view.getContext().getDrawable(R.drawable.progress_bar_empty_shape));
            view.findViewById(R.id.setStatsTextLinear).setVisibility(View.GONE);
            view.findViewById(R.id.noDataTextView).setVisibility(View.VISIBLE);
            clickToSeeDetailsTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return setsStats.size();
    }
}
