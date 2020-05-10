package com.rootekstudio.repeatsandroid.mainpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.community.RepeatsCommunityStartActivity;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.SingleSetInfo;
import com.rootekstudio.repeatsandroid.database.Values;
import com.rootekstudio.repeatsandroid.fastlearning.FastLearningConfigActivity;
import com.rootekstudio.repeatsandroid.readaloud.ReadAloudActivity;
import com.rootekstudio.repeatsandroid.readaloud.ReadAloudConfigActivity;

import java.util.Collections;
import java.util.List;

public class StartFragment extends Fragment {
    private SingleSetInfo fastLearningSetRecommendation = null;
    private SingleSetInfo readAloudSetRecommendation = null;
    private List<SingleSetInfo> setsInfo;

    public StartFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        generateData();
        View view = inflater.inflate(R.layout.mainfragment_start, container, false);
        RelativeLayout fastLearningRecommendation = view.findViewById(R.id.fastLearningRecommendation);
        RelativeLayout readAloudRecommendation = view.findViewById(R.id.readAloudRecommendation);
        RelativeLayout repeatsCommunityRecommendation = view.findViewById(R.id.repeatsCommunityRecommendation);
        RelativeLayout turnOnNotificationsRecommendation = view.findViewById(R.id.notifiRecommendation);
        LinearLayout recommendedLinear = view.findViewById(R.id.recommendedLinear);
        LinearLayout suggestionsLinear = view.findViewById(R.id.suggestionsLinear);

        RelativeLayout fastLearningRelative = view.findViewById(R.id.featureFastLearningMain);
        RelativeLayout readAloudRelative = view.findViewById(R.id.featureReadAloudMain);
        RelativeLayout notificationsRelative = view.findViewById(R.id.featureNotificationsMain);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String notifiMode = sharedPreferences.getString("ListNotifi", "0");

        if (notifiMode.equals("0") && setsInfo.size() != 0) {
            turnOnNotificationsRecommendation.setVisibility(View.VISIBLE);
        }

        if (setsInfo.size() > 1) {
            fastLearningSetRecommendation = setsInfo.get(0);
            readAloudSetRecommendation = setsInfo.get(1);
        } else if (setsInfo.size() == 1) {
            fastLearningSetRecommendation = setsInfo.get(0);
            readAloudSetRecommendation = setsInfo.get(0);
        } else {
            suggestionsLinear.setVisibility(View.GONE);
        }

        if (!notifiMode.equals("0") && setsInfo.size() >= 4) {
            recommendedLinear.setVisibility(View.GONE);
        }

        if (setsInfo.size() < 4) {
            repeatsCommunityRecommendation.setVisibility(View.VISIBLE);
        }

        if (fastLearningSetRecommendation != null || readAloudSetRecommendation != null) {
            TextView setNameFL = fastLearningRecommendation.findViewById(R.id.text1FirstRecommendation);
            setNameFL.setText(fastLearningSetRecommendation.getSetName());
            fastLearningRecommendation.setTag(fastLearningSetRecommendation.getSetID());
            fastLearningRecommendation.setOnClickListener(view14 -> {
                String setID = view14.getTag().toString();
                Intent intent = new Intent(requireContext(), FastLearningConfigActivity.class);
                intent.putExtra("setID", setID);
                startActivity(intent);
            });

            TextView setNameRA = readAloudRecommendation.findViewById(R.id.text1SecRecommendation);
            setNameRA.setText(readAloudSetRecommendation.getSetName());
            readAloudRecommendation.setTag(readAloudSetRecommendation.getSetID());
            readAloudRecommendation.setOnClickListener(view15 -> {
                String setID = view15.getTag().toString();
                Intent intent = new Intent(requireContext(), ReadAloudActivity.class);
                intent.putExtra("setID", setID);
                intent.putExtra("newReadAloud", true);
                startActivity(intent);
            });
        } else {
            try {
                fastLearningRecommendation.setVisibility(View.GONE);
                readAloudRecommendation.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        turnOnNotificationsRecommendation.setOnClickListener(view16 -> {
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setSelectedItemId(R.id.app_bar_settings);
        });

        repeatsCommunityRecommendation.setOnClickListener(view17 -> {
            Intent intent = new Intent(requireContext(), RepeatsCommunityStartActivity.class);
            startActivity(intent);
        });

        fastLearningRelative.setOnClickListener(view13 -> {
            Intent intent = new Intent(requireContext(), FastLearningConfigActivity.class);
            startActivity(intent);
        });

        readAloudRelative.setOnClickListener(view12 -> {
            Intent intent = new Intent(requireContext(), ReadAloudConfigActivity.class);
            startActivity(intent);
        });

        notificationsRelative.setOnClickListener(view1 -> {
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setSelectedItemId(R.id.app_bar_settings);
        });

        try {
            if (!sharedPreferences.contains("version")) {
                RepeatsHelper.saveVersion(requireContext());
            } else {
                if (!sharedPreferences.getString("version", "2.6").equals(RepeatsHelper.version)) {
                    view.findViewById(R.id.infoLayout).setVisibility(View.VISIBLE);
                    RepeatsHelper.saveVersion(requireContext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void generateData() {
        setsInfo = RepeatsDatabase.getInstance(requireContext()).allSetsInfo(Values.ORDER_BY_WRONG_ANSWERS_RATIO);
        Collections.shuffle(setsInfo);
    }
}
