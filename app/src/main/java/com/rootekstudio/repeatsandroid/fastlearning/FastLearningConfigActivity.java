package com.rootekstudio.repeatsandroid.fastlearning;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsSingleItem;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FastLearningConfigActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    int configStage;
    DatabaseHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_learning_config);
        getSupportActionBar().hide();

        DB = new DatabaseHelper(this);
        FastLearningInfo.reset();
        configStage = 0;
        fragmentManager = getSupportFragmentManager();

        String selectedSetID = getIntent().getStringExtra("setID");
        if (selectedSetID != null) {
            List<RepeatsSingleItem> setItems = DB.AllItemsSET(selectedSetID, -1);
            FastLearningSetsListItem newItem = DB.singleSetIdNameAndStats(selectedSetID);
            FastLearningInfo.selectedSets.add(newItem);
            FastLearningInfo.setsContent.put(selectedSetID, setItems);
            FastLearningInfo.allAvailableQuestionsCount += setItems.size();
            FastLearningInfo.questionsCount += setItems.size();

            navigateToFragment1();
            findViewById(R.id.nextConfigFL).setEnabled(true);
            configStage++;
        } else {
            navigateToFragment0();
        }
    }

    public void nextClick(View view) {
        if (configStage == 0) {
            FastLearningInfo.setsContent = new HashMap<>();
            FastLearningInfo.allAvailableQuestionsCount = 0;
            FastLearningInfo.questionsCount = 0;
            for (int i = 0; i < FastLearningInfo.selectedSets.size(); i++) {
                FastLearningSetsListItem singleItem = FastLearningInfo.selectedSets.get(i);
                List<RepeatsSingleItem> setItems = DB.AllItemsSET(singleItem.getSetID(), -1);
                FastLearningInfo.setsContent.put(singleItem.getSetID(), setItems);
                FastLearningInfo.allAvailableQuestionsCount += setItems.size();
                FastLearningInfo.questionsCount += setItems.size();
            }
            navigateToFragment1();
            configStage++;
        } else if (configStage == 1) {
            if (FastLearningInfo.randomQuestions) {
                FastLearningInfo.selectedQuestions = chooseQuestions();
                navigateToFastLearning();
            } else {
                navigateToFragment2();
            }

            configStage++;
        } else if (configStage == 2) {
            navigateToFastLearning();
        }
    }

    private void navigateToFragment0() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutFastLearning, new Fragment0());
        fragmentTransaction.commit();
    }

    private void navigateToFragment1() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutFastLearning, new Fragment1());
        fragmentTransaction.commit();
    }

    private void navigateToFragment2() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutFastLearning, new Fragment2());
        fragmentTransaction.commit();
    }

    private void navigateToFastLearning() {
        Intent intent = new Intent(this, FastLearningActivity.class);
        startActivity(intent);
        finish();
    }

    private ArrayList<RepeatsSingleItem> chooseQuestions() {
        ArrayList<RepeatsSingleItem> selectedQuestions = new ArrayList<>();

        //getting questions from sets with certain conditions
        for (List<RepeatsSingleItem> singleItemList : FastLearningInfo.setsContent.values()) {
            int addedQuestionsFromSingleSet = 0;
            for (int i = 0; i < singleItemList.size(); i++) {
                if (addedQuestionsFromSingleSet >= FastLearningInfo.questionsCount / FastLearningInfo.setsContent.size()) {
                    break;
                }

                RepeatsSingleItem singleItem = singleItemList.get(i);
                float wrongAnswers = singleItem.getWrongAnswers();
                float allAnswers = singleItem.getAllAnswers();

                float wrongAnswersRatio = wrongAnswers / allAnswers;

                if (wrongAnswersRatio > 0.5 || allAnswers == 0) {
                    selectedQuestions.add(singleItem);
                    addedQuestionsFromSingleSet++;
                }
            }
        }

        //while user requested more questions than size of selectedQuestions
        while (selectedQuestions.size() < FastLearningInfo.questionsCount) {
            int availableSpace = FastLearningInfo.questionsCount - selectedQuestions.size();
            int addedExtraQuestionsFromSingleSet = 0;

            //if there is more sets than available place to put questions
            if (availableSpace < FastLearningInfo.setsContent.size()) {
                //sort sets by All answers
                ArrayList<FastLearningSetsListItem> sortedSets = new ArrayList<>();
                int lowestAllAnswers = -1;
                for (int i = 0; i < FastLearningInfo.setsContent.size(); i++) {
                    FastLearningSetsListItem singleSetInfo = FastLearningInfo.selectedSets.get(i);
                    if (i == 0) {
                        lowestAllAnswers = singleSetInfo.getAllAnswers();
                        sortedSets.add(singleSetInfo);
                        continue;
                    }

                    if (lowestAllAnswers > singleSetInfo.getAllAnswers()) {
                        sortedSets.add(0, singleSetInfo);
                    } else {
                        sortedSets.add(singleSetInfo);
                    }
                }

                //take one question from each set while availableSpace != 0
                for (int i = 0; i < sortedSets.size(); i++) {
                    List<RepeatsSingleItem> singleItemList = FastLearningInfo.setsContent.get(sortedSets.get(i).getSetID());
                    Collections.shuffle(singleItemList);
                    for (int j = 0; j < singleItemList.size(); j++) {
                        RepeatsSingleItem singleItem = singleItemList.get(j);
                        if (!selectedQuestions.contains(singleItem)) {
                            selectedQuestions.add(singleItem);
                            availableSpace--;
                            break;
                        }
                    }

                    if (availableSpace == 0) {
                        break;
                    }
                }
                //if there is more available place for questions than sets
            } else {
                for (List<RepeatsSingleItem> singleItemList : FastLearningInfo.setsContent.values()) {
                    addedExtraQuestionsFromSingleSet = 0;
                    Collections.shuffle(singleItemList);
                    for (int i = 0; i < singleItemList.size(); i++) {
                        if (addedExtraQuestionsFromSingleSet >= availableSpace / FastLearningInfo.setsContent.size()) {
                            break;
                        } else {
                            RepeatsSingleItem singleItem = singleItemList.get(i);

                            if (!selectedQuestions.contains(singleItem)) {
                                selectedQuestions.add(singleItem);
                                addedExtraQuestionsFromSingleSet++;
                            }
                        }
                    }
                }
            }
        }
        Collections.shuffle(selectedQuestions);
        return selectedQuestions;
    }

    public void previousClick(View view) {
        onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        configStage--;
        if(configStage == -1) {
            super.onBackPressed();
        }
        else if(configStage == 0) {
            navigateToFragment0();
        }
        else if (configStage == 1) {
            navigateToFragment1();
            findViewById(R.id.nextConfigFL).setEnabled(true);
        }
    }
}
