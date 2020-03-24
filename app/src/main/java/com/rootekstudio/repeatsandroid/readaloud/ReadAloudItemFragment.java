package com.rootekstudio.repeatsandroid.readaloud;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.rootekstudio.repeatsandroid.R;

import java.util.ArrayList;

public class ReadAloudItemFragment extends Fragment {
    TextView firstLanguage;
    public TextView firstWord;
    TextView secondLanguage;
    public TextView secondWord;
    TextView wordCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayList<String> info = getArguments().getStringArrayList("info");
        int speakItemIndex = getArguments().getInt("speakItemIndex");

        View view = inflater.inflate(R.layout.read_aloud_single_item, container, false);
        firstLanguage = view.findViewById(R.id.firstLanguageRead);
        firstWord = view.findViewById(R.id.firstWordRead);
        secondLanguage = view.findViewById(R.id.secondLanguageRead);
        secondWord = view.findViewById(R.id.secondWordRead);
        wordCount = view.findViewById(R.id.wordCountRead);

        firstLanguage.setText(info.get(0));
        firstWord.setText(info.get(1));
        secondLanguage.setText(info.get(2));
        secondWord.setText(info.get(3));
        wordCount.setText(info.get(4));

        if (speakItemIndex % 2 == 0) {
            firstWord.setTypeface(Typeface.DEFAULT_BOLD);
            secondWord.setTypeface(Typeface.DEFAULT);
        } else {
            firstWord.setTypeface(Typeface.DEFAULT);
            secondWord.setTypeface(Typeface.DEFAULT_BOLD);
        }

        return view;
    }
}
