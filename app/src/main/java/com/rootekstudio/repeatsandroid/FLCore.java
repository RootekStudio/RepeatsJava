package com.rootekstudio.repeatsandroid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FLCore {
    private static int answered;
    private int correctAnswers;

    private static List<RepeatsSingleSetDB> selectedItems;
    private static String ignore;

    public Boolean done = false;
    private int allSetItemsSize;

    public void start(List<RepeatsSingleSetDB> allSetItems, String ignoreChars) {
        ignore = ignoreChars;
        selectedItems = new ArrayList<>();
        allSetItemsSize = allSetItems.size();
        answered = 0;
        correctAnswers = 0;

        if(allSetItemsSize < 10) {
            for (int i = 0; i < allSetItemsSize; i++) {
                selectedItems.add(allSetItems.get(i));
            }
        }
        else {
            for (int i = 0; i < 10; i++) {
                Random random = new Random();
                int index = random.nextInt((allSetItemsSize-1));
                selectedItems.add(allSetItems.get(index));
            }
        }
    }

    public boolean checkAnswered(String answer) {
        if (CheckAnswer.isAnswerCorrect(answer, selectedItems.get(answered).getAnswer(), ignore)) {
            correctAnswers++;

            if(allSetItemsSize < 10) {
                if(correctAnswers == allSetItemsSize) {
                    done = true;
                }
            }
            else {
                if(correctAnswers == 10) {
                    done = true;
                }
            }

            answered++;
            return true;

        } else {

            if(selectedItems.size() == 1 ) {
                done = true;
            }
            else if(selectedItems.size() == 2) {
                selectedItems.add(selectedItems.get(1));
                selectedItems.set(1, selectedItems.get(answered));
            }
            else if(selectedItems.size()-1 == answered){
                selectedItems.add(selectedItems.get(answered));
            }
            else {
                int selectedLastIndex = selectedItems.size();
                Random random = new Random();
                int index = random.nextInt(selectedLastIndex - (answered+1)) + (answered+1);
                selectedItems.add(selectedItems.get(index));
                selectedItems.set(index, selectedItems.get(answered));
            }

            answered++;
            return false;
        }
    }

    public static String[] gQuestionAndAnswer()
    {
        RepeatsSingleSetDB single = selectedItems.get(answered);
        return new String[]{single.getQuestion(), single.getAnswer(), single.getImag(), String.valueOf(single.getID())};
    }
}
