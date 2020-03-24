package com.rootekstudio.repeatsandroid.fastlearning;

public class FastLearningSetsListItem {
    private String setID;
    private String setName;
    private int allAnswers;

    public FastLearningSetsListItem(String setID, String setName, int allAnswers) {
        this.setID = setID;
        this.setName = setName;
        this.allAnswers = allAnswers;
    }

    public void setSetID(String setID) {
        this.setID = setID;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public void setAllAnswers(int allAnswers) {
        this.allAnswers = allAnswers;
    }

    public String getSetID() {
        return setID;
    }

    public String getSetName() {
        return setName;
    }

    public int getAllAnswers() {
        return allAnswers;
    }
}
