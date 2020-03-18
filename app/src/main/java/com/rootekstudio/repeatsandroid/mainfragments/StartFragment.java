package com.rootekstudio.repeatsandroid.mainfragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsSetInfo;
import com.rootekstudio.repeatsandroid.community.RepeatsCommunityStartActivity;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;
import com.rootekstudio.repeatsandroid.fastlearning.FastLearningConfigActivity;
import com.rootekstudio.repeatsandroid.readaloud.ReadAloudActivity;
import com.rootekstudio.repeatsandroid.readaloud.ReadAloudConfigActivity;

import java.util.Collections;
import java.util.List;

public class StartFragment extends Fragment {
    private RepeatsSetInfo fastLearningSetRecommendation = null;
    private RepeatsSetInfo readAloudSetRecommendation = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        generateData();
        View view = inflater.inflate(R.layout.mainfragment_start, container, false);
        RelativeLayout fastLearningRecommendation = view.findViewById(R.id.fastLearningRecommendation);
        RelativeLayout readAloudRecommendation = view.findViewById(R.id.readAloudRecommendation);
        RelativeLayout repeatsCommunityRecommendation = view.findViewById(R.id.repeatsCommunityRecommendation);

        RelativeLayout fastLearningRelative = view.findViewById(R.id.featureFastLearningMain);
        RelativeLayout readAloudRelative = view.findViewById(R.id.featureReadAloudMain);
        RelativeLayout notificationsRelative = view.findViewById(R.id.featureNotificationsMain);

        if(fastLearningSetRecommendation != null || readAloudSetRecommendation != null) {
            TextView setNameFL = fastLearningRecommendation.findViewById(R.id.text1FirstRecommendation);
            setNameFL.setText(fastLearningSetRecommendation.getitle());
            fastLearningRecommendation.setTag(fastLearningSetRecommendation.getTableName());
            fastLearningRecommendation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String setID = view.getTag().toString();
                    Intent intent = new Intent(getContext(), FastLearningConfigActivity.class);
                    intent.putExtra("setID", setID);
                    startActivity(intent);
                }
            });

            TextView setNameRA = readAloudRecommendation.findViewById(R.id.text1SecRecommendation);
            setNameRA.setText(readAloudSetRecommendation.getitle());
            readAloudRecommendation.setTag(readAloudSetRecommendation.getTableName());
            readAloudRecommendation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String setID = view.getTag().toString();
                    Intent intent = new Intent(getContext(), ReadAloudActivity.class);
                    intent.putExtra("setID", setID);
                    intent.putExtra("newReadAloud", true);
                    startActivity(intent);
                }
            });
        }
        else {
            try {
                fastLearningRecommendation.setVisibility(View.GONE);
                readAloudRecommendation.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        repeatsCommunityRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RepeatsCommunityStartActivity.class);
                startActivity(intent);
            }
        });

        fastLearningRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FastLearningConfigActivity.class);
                startActivity(intent);
            }
        });

        readAloudRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ReadAloudConfigActivity.class);
                startActivity(intent);
            }
        });

        notificationsRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
                bottomNavigationView.setSelectedItemId(R.id.app_bar_settings);
            }
        });

        return view;
    }

    private void generateData() {
        DatabaseHelper DB = new DatabaseHelper(getContext());
        List<RepeatsSetInfo> setsInfo = DB.AllItemsLIST(DatabaseHelper.ORDER_BY_WRONG_ANSWERS_RATIO);
        Collections.shuffle(setsInfo);
        if(setsInfo.size() > 1) {
            fastLearningSetRecommendation = setsInfo.get(0);
            readAloudSetRecommendation = setsInfo.get(1);
        }
        else if(setsInfo.size() == 1) {
            fastLearningSetRecommendation = setsInfo.get(0);
            readAloudSetRecommendation = setsInfo.get(0);
        }
    }
}
