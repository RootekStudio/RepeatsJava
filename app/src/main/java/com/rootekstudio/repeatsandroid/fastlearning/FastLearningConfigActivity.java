package com.rootekstudio.repeatsandroid.fastlearning;

import android.content.Intent;
import android.os.Bundle;
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
        Fragment0 fragment0 = new Fragment0();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutFastLearning, fragment0);
        fragmentTransaction.commit();
    }

    public void nextClick(View view) {
        if (configStage == 0) {
            Fragment0 fragment0 = (Fragment0) getSupportFragmentManager().findFragmentById(R.id.frameLayoutFastLearning);
            assert fragment0 != null;

            for (int i = 0; i < FastLearningInfo.selectedSets.size(); i++) {
                FastLearningSetsListItem singleItem = FastLearningInfo.selectedSets.get(i);
                List<RepeatsSingleItem> setItems = DB.AllItemsSET(singleItem.getSetID(), -1);
                FastLearningInfo.setsContent.put(singleItem.getSetID(), setItems);
                FastLearningInfo.allAvailableQuestionsCount += setItems.size();
            }

            Fragment1 fragment1 = new Fragment1();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayoutFastLearning, fragment1);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            configStage++;
        } else if (configStage == 1) {
            if (FastLearningInfo.randomQuestions) {
                FastLearningInfo.selectedQuestions = chooseQuestions();
                navigateToFastLearning();
            } else {
                Fragment2 fragment2 = new Fragment2();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayoutFastLearning, fragment2);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

            configStage++;
        } else if (configStage == 2) {
            navigateToFastLearning();
        }
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
        if (configStage == 0) {
            FastLearningInfo.reset();
        } else if (configStage == 1) {
            findViewById(R.id.nextConfigFL).setEnabled(true);
        }

        super.onBackPressed();
    }

}
