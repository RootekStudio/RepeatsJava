package com.rootekstudio.repeatsandroid.statistics;

public class SetStats {
    private String setID;
    private String name;
    private int goodAnswers;
    private int wrongAnswers;
    private int allAnswers;

    public SetStats() {

    }

    public SetStats(String setID, String name, int goodAnswers, int wrongAnswers, int allAnswers) {
    }

    public void setSetID(String setID) {
        this.setID = setID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGoodAnswers(int goodAnswers) {
        this.goodAnswers = goodAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public void setAllAnswers(int allAnswers) {
        this.allAnswers = allAnswers;
    }

    public String getSetID() {
        return setID;
    }

    public String getName() {
        return name;
    }

    public int getGoodAnswers() {
        return goodAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public int getAllAnswers() {
        return goodAnswers + wrongAnswers;
    }
}
