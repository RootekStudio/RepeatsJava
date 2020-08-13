package com.rootekstudio.repeatsandroid.fastlearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.SetSingleItem;

import java.util.List;
import java.util.Objects;

public class Fragment2 extends Fragment {
    private LinearLayout linearList;
    private MaterialButton button;
    private TextView textCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fastlearning_fragment2, container, false);
        linearList = view.findViewById(R.id.linearLayoutQuestionsListFL);
        button = requireActivity().findViewById(R.id.nextConfigFL);
        button.setEnabled(false);

        textCount = view.findViewById(R.id.questionsCountTextView);
        String textQuestionsCount = getString(R.string.selected) + ": " + FastLearningInfo.selectedQuestions.size() + " " + getString(R.string.questions2);
        textCount.setText(textQuestionsCount);

        generateList();
        return view;
    }

    private void generateList() {
        new Thread(() -> {
            RepeatsDatabase DB = RepeatsDatabase.getInstance(requireContext());
            final List<SetSingleItem> questionsList = DB.getItemsForFastLearning(FastLearningInfo.selectedSets);
            LayoutInflater inflater = LayoutInflater.from(requireContext());

            String setID = "";
            for (int i = 0; i < questionsList.size(); i++) {
                SetSingleItem singleItem = questionsList.get(i);

                //add view with set name
                if (!singleItem.getSetID().equals(setID)) {
                    setID = singleItem.getSetID();
                    View view = inflater.inflate(R.layout.fastlearning_list_singleitem, linearList, false);
                    TextView textView = view.findViewById(R.id.setNameListViewItemFL);
                    CheckBox checkBox = view.findViewById(R.id.checkBoxListViewFL);

                    checkBox.setVisibility(View.GONE);
                    String text = getString(R.string.Set) + ": " + singleItem.getSetName();
                    textView.setText(text);
                    view.setEnabled(false);

                    requireActivity().runOnUiThread(() -> linearList.addView(view));
                }

                //add view with question and checkbox
                View view = inflater.inflate(R.layout.fastlearning_list_singleitem, linearList, false);
                TextView textView = view.findViewById(R.id.setNameListViewItemFL);
                CheckBox checkBox = view.findViewById(R.id.checkBoxListViewFL);

                textView.setText(singleItem.getQuestion());
                checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                    if (b) {
                        FastLearningInfo.selectedQuestions.add(singleItem);
                        FastLearningInfo.questionsCount++;
                        if (FastLearningInfo.questionsCount == 1) {
                            button.setEnabled(true);
                        }
                    } else {
                        FastLearningInfo.selectedQuestions.remove(singleItem);
                        FastLearningInfo.questionsCount--;
                        if (FastLearningInfo.questionsCount == 0) {
                            button.setEnabled(false);
                        }
                    }

                    String text = getString(R.string.selected) + ": " + FastLearningInfo.selectedQuestions.size() + " " + getString(R.string.questions2);
                    textCount.setText(text);
                });

                view.setOnClickListener(view1 -> {
                    if (checkBox.isChecked()) {
                        checkBox.setChecked(false);
                    } else {
                        checkBox.setChecked(true);
                    }
                });

                requireActivity().runOnUiThread(() -> linearList.addView(view));
            }
        }).start();

    }
}
