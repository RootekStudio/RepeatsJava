package com.rootekstudio.repeatsandroid.statistics;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsSingleItem;

import java.util.List;

public class SetStatsActivityAdapter extends RecyclerView.Adapter<SetStatsActivityAdapter.MainViewHolder> {
    private List<RepeatsSingleItem> setQuestionStats;

    static class MainViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;

        MainViewHolder(RelativeLayout rl) {
            super(rl);
            relativeLayout = rl;
        }
    }

    public SetStatsActivityAdapter(List<RepeatsSingleItem> setsStats) {
        this.setQuestionStats = setsStats;
    }

    @Override
    public SetStatsActivityAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stats_single_question, parent, false);

        SetStatsActivityAdapter.MainViewHolder mainViewHolder = new SetStatsActivityAdapter.MainViewHolder(relativeLayout);
        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(SetStatsActivityAdapter.MainViewHolder holder, int position) {
        RelativeLayout view = holder.relativeLayout;
        RepeatsSingleItem item = setQuestionStats.get(position);

        TextView question = view.findViewById(R.id.questionTextViewStats);
        TextView answer = view.findViewById(R.id.answerTextViewStats);
        TextView goodAnswers = view.findViewById(R.id.goodAnswersSingleStats);
        TextView wrongAnswers = view.findViewById(R.id.wrongAnswersSingleStats);
        TextView allAnswers = view.findViewById(R.id.allAnswersSingleStats);

        if (position == 0) {
            question.setText(view.getContext().getString(R.string.Question));
            answer.setText(view.getContext().getString(R.string.Answer));
            goodAnswers.setText("+");
            wrongAnswers.setText("-");
            allAnswers.setText("=");
        } else {
            question.setText(item.getQuestion());
            answer.setText(item.getAnswer());
            goodAnswers.setText(String.valueOf(item.getGoodAnswers()));
            wrongAnswers.setText(String.valueOf(item.getWrongAnswers()));
            allAnswers.setText(String.valueOf(item.getAllAnswers()));
        }
    }

    @Override
    public int getItemCount() {
        return setQuestionStats.size();
    }
}
