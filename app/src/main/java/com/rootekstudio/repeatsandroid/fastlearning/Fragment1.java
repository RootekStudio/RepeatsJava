package com.rootekstudio.repeatsandroid.fastlearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.rootekstudio.repeatsandroid.R;

public class Fragment1 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fastlearning_fragment1, container, false);

        RadioButton randomQuestions = view.findViewById(R.id.randomQuestionsButton);
        RadioButton manuallySelectQuestions = view.findViewById(R.id.manuallySelectQuestions);
        TextView maxTextView = view.findViewById(R.id.maxQuestionsTextView);
        final RelativeLayout relativeSeekBar = view.findViewById(R.id.relativeSeekBar);
        final SeekBar seekBar = view.findViewById(R.id.seekBarFastLearning);
        final TextView seekBarSelectedAnswers = view.findViewById(R.id.seekBarSelectedAnswers);
        CheckBox ignoreChars = view.findViewById(R.id.checkBoxIgnoreCharsFL);
        ignoreChars.setChecked(FastLearningInfo.ignoreChars);

        ignoreChars.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                FastLearningInfo.ignoreChars = b;
            }
        });

        randomQuestions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    FastLearningInfo.questionsCount = seekBar.getProgress();
                    FastLearningInfo.randomQuestions = true;
                    relativeSeekBar.setVisibility(View.VISIBLE);
                }
            }
        });

        manuallySelectQuestions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    FastLearningInfo.randomQuestions = false;
                    FastLearningInfo.questionsCount = 0;
                    relativeSeekBar.setVisibility(View.GONE);
                }
            }
        });

        maxTextView.setText(String.valueOf(FastLearningInfo.allAvailableQuestionsCount));
        seekBar.setMax(FastLearningInfo.allAvailableQuestionsCount);
        seekBar.setProgress(FastLearningInfo.allAvailableQuestionsCount);
        FastLearningInfo.questionsCount = FastLearningInfo.allAvailableQuestionsCount;

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                FastLearningInfo.questionsCount = i;
                String text = FastLearningInfo.questionsCount + " " + getString(R.string.questions2);
                seekBarSelectedAnswers.setText(text);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        String seekBarSelectedAnswersText = FastLearningInfo.questionsCount + " " + getString(R.string.questions2);
        seekBarSelectedAnswers.setText(seekBarSelectedAnswersText);

        return view;
    }
}
