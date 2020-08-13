package com.rootekstudio.repeatsandroid.fastlearning;

import com.rootekstudio.repeatsandroid.database.SetSingleItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class FastLearningInfo {
    static List<FastLearningSetsListItem> selectedSets;
    static List<SetSingleItem> selectedQuestions;
    static HashMap<String, List<SetSingleItem>> setsContent;
    static boolean randomQuestions;
    static boolean ignoreChars;
    static int questionsCount;
    static int allAvailableQuestionsCount;
    static String setsFromNotification;

    static void reset() {
        selectedSets = new ArrayList<>();
        setsContent = new HashMap<>();
        selectedQuestions = new ArrayList<>();
        randomQuestions = true;
        ignoreChars = false;
        questionsCount = 0;
        allAvailableQuestionsCount = 0;
        setsFromNotification = null;
    }
}
