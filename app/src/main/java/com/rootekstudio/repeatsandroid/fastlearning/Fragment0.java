package com.rootekstudio.repeatsandroid.fastlearning;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import java.util.List;
import java.util.Objects;

public class Fragment0 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fastlearning_fragment0, container, false);

        if(FastLearningInfo.selectedSets.size() == 0) {
            getActivity().findViewById(R.id.nextConfigFL).setEnabled(false);
        }

        DatabaseHelper DB = new DatabaseHelper(getContext());
        LinearLayout linearLayout = view.findViewById(R.id.linearSetsListFL);
        List<FastLearningSetsListItem> setsList = DB.setsIdAndNameList();

        for(int i = 0; i < setsList.size(); i++) {
            final FastLearningSetsListItem singleItem = setsList.get(i);
            View singleView = LayoutInflater.from(linearLayout.getContext()).inflate(R.layout.fastlearning_list_singleitem, null);

            final CheckBox checkBox = singleView.findViewById(R.id.checkBoxListViewFL);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if(checked) {
                        FastLearningInfo.selectedSets.add(singleItem);
                        if(FastLearningInfo.selectedSets.size() == 1) {
                            getActivity().findViewById(R.id.nextConfigFL).setEnabled(true);
                        }
                    }
                    else {
                        FastLearningInfo.selectedSets.remove(singleItem);
                        if(FastLearningInfo.selectedSets.size() == 0) {
                            getActivity().findViewById(R.id.nextConfigFL).setEnabled(false);
                        }
                    }
                }
            });

            singleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkBox.isChecked()) {
                        checkBox.setChecked(false);
                    }
                    else {
                        checkBox.setChecked(true);
                    }
                }
            });

            TextView setNameTextView = singleView.findViewById(R.id.setNameListViewItemFL);
            setNameTextView.setText(singleItem.getSetName());

            linearLayout.addView(singleView);
        }


        return view;
    }
}
