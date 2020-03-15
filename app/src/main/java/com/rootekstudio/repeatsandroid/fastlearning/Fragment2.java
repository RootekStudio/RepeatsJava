package com.rootekstudio.repeatsandroid.fastlearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsSingleItem;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import java.util.List;
import java.util.Objects;

public class Fragment2 extends Fragment {
    private LinearLayout linearList;
    private MaterialButton button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fastlearning_fragment2, container, false);
        linearList = view.findViewById(R.id.linearLayoutQuestionsListFL);
        button = getActivity().findViewById(R.id.nextConfigFL);
        button.setEnabled(false);
        generateList();
        return view;
    }

    void generateList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseHelper DB = new DatabaseHelper(getContext());
                final List<RepeatsSingleItem> questionsList = DB.getItemsForFastLearning(FastLearningInfo.selectedSets);
                LayoutInflater inflater = LayoutInflater.from(getContext());

                for (int i = 0; i < questionsList.size(); i++) {
                    final RepeatsSingleItem singleItem = questionsList.get(i);
                    final View view = inflater.inflate(R.layout.fastlearning_list_singleitem, linearList, false);

                    final TextView textCount = getActivity().findViewById(R.id.questionsCountTextView);
                    TextView textView = view.findViewById(R.id.setNameListViewItemFL);
                    final CheckBox checkBox = view.findViewById(R.id.checkBoxListViewFL);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String textQuestionsCount = getString(R.string.selected) + ": " + FastLearningInfo.selectedQuestions.size() + " " + getString(R.string.questions2);
                            textCount.setText(textQuestionsCount);
                        }
                    });

                    if (singleItem.getSetID().equals("new_set")) {
                        checkBox.setVisibility(View.GONE);
                        String text = getString(R.string.Set) + ": " + singleItem.getQuestion();
                        textView.setText(text);
                        view.setEnabled(false);
                    } else {
                        textView.setText(singleItem.getQuestion());
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                if (b) {
                                    FastLearningInfo.selectedQuestions.add(singleItem);
                                    FastLearningInfo.questionsCount++;
                                    if(FastLearningInfo.questionsCount == 1) {
                                        button.setEnabled(true);
                                    }
                                } else {
                                    FastLearningInfo.selectedQuestions.remove(singleItem);
                                    FastLearningInfo.questionsCount--;
                                    if(FastLearningInfo.questionsCount == 0) {
                                        button.setEnabled(false);
                                    }
                                }

                                String text = getString(R.string.selected) + ": " + FastLearningInfo.selectedQuestions.size() + " " + getString(R.string.questions2);
                                textCount.setText(text);
                            }
                        });

                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (checkBox.isChecked()) {
                                    checkBox.setChecked(false);
                                } else {
                                    checkBox.setChecked(true);
                                }
                            }
                        });
                    }

                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            linearList.addView(view);
                        }
                    });
                }
            }
        }).start();

    }
}
