package com.rootekstudio.repeatsandroid.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rootekstudio.repeatsandroid.R;

import java.util.HashMap;
import java.util.Objects;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ListHolder> {
    HashMap<Integer, String[]> questionsAndAnswersList;

    public PreviewAdapter(HashMap<Integer, String[]> questionsAndAnswersList) {
        this.questionsAndAnswersList = questionsAndAnswersList;
    }

    static class ListHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;

        ListHolder(View view) {
            super(view);
            linearLayout = view.findViewById(R.id.linearQuestionAndAnswer);
        }
    }

    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.question_and_answer, parent, false);
        return new ListHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ListHolder holder, int position) {
        LinearLayout linearLayout = holder.linearLayout;

        TextView question = linearLayout.findViewById(R.id.questionTextView);
        TextView answer = linearLayout.findViewById(R.id.answerTextView);

        question.setText(Objects.requireNonNull(questionsAndAnswersList.get(position))[0]);
        answer.setText(Objects.requireNonNull(questionsAndAnswersList.get(position))[1]);
    }

    @Override
    public int getItemCount() {
        return questionsAndAnswersList.size();
    }
}
