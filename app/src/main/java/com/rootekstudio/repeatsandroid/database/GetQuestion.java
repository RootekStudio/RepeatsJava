package com.rootekstudio.repeatsandroid.database;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GetQuestion {
    private String question;
    private String answer;
    private String setName;
    private String pictureName;
    private int ignoreChars;
    private String setID;
    private int itemID;

    public GetQuestion(Context context) {
        RepeatsDatabase DB = RepeatsDatabase.getInstance(context);
        List<String> all = DB.getAllSetsIDs();
        int count = all.size();

        if (count == 0) {
            return;
        }

        Random random = new Random();
        int randomint = random.nextInt(count);
        SingleSetInfo single = DB.singleSetInfo(all.get(randomint));

        setName = single.getSetName();
        setID = single.getSetID();
        ignoreChars = single.getIgnoreChars();

        List<SetSingleItem> set = DB.allItemsInSet(setID, -1);
        int setcount = set.size();
        Random randomset = new Random();
        int randomsetint = randomset.nextInt(setcount);

        SetSingleItem singleSetDB = set.get(randomsetint);
        question = singleSetDB.getQuestion();
        answer = singleSetDB.getAnswer();
        pictureName = singleSetDB.getImage();
        itemID = singleSetDB.getItemID();
    }

    //get question using rules from Advanced delivery
    public GetQuestion(Context context, String setsIDs) {
        String chosenSetID;
        List<String> setsIDsList = new ArrayList<>();

        Scanner scanner = new Scanner(setsIDs);
        while(scanner.hasNextLine()) {
            setsIDsList.add(scanner.nextLine());
        }

        int count = setsIDsList.size();
        if (count == 1) {
            chosenSetID = setsIDsList.get(0);
        } else {
            Random random = new Random();
            int randomint = random.nextInt(count);
            chosenSetID = setsIDsList.get(randomint);
        }
        setID = chosenSetID;

        RepeatsDatabase DB = RepeatsDatabase.getInstance(context);
        SingleSetInfo singleTitle = DB.singleSetInfo(chosenSetID);
        setName = singleTitle.getSetName();
        ignoreChars = singleTitle.getIgnoreChars();

        List<SetSingleItem> allQuestions = DB.allItemsInSet(chosenSetID, -1);
        int allQcount = allQuestions.size();
        SetSingleItem single;

        if (allQcount == 1) {
            single = allQuestions.get(0);
        } else {
            Random randomset = new Random();
            int randomsetint = randomset.nextInt(allQcount);
            single = allQuestions.get(randomsetint);
        }

        question = single.getQuestion();
        answer = single.getAnswer();
        pictureName = single.getImage();
        itemID = single.getItemID();
    }

    public GetQuestion() {
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public void setIgnoreChars(int ignoreChars) {
        this.ignoreChars = ignoreChars;
    }

    public void setSetID(String setID) {
        this.setID = setID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getSetName() {
        return setName;
    }

    public String getPictureName() {
        return pictureName;
    }

    public int getIgnoreChars() { return ignoreChars; }

    public String getSetID() {
        return setID;
    }

    public int getItemID() {
        return itemID;
    }
}

