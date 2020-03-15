package com.rootekstudio.repeatsandroid;

public class RepeatsSingleItem {
    private int itemID;
    private String setID;
    private String setName;
    private String Question;
    private String Answer;
    private String Imag;
    private int goodAnswers;
    private int wrongAnswers;
    private int allAnswers;

    public RepeatsSingleItem() {
    }

    public RepeatsSingleItem(int itemID, String Question, String Answer, String Imag, int goodAnswers, int wrongAnswers, int allAnswers) {
        this.itemID = itemID;
        this.Question = Question;
        this.Answer = Answer;
        this.Imag = Imag;
        this.goodAnswers = goodAnswers;
        this.wrongAnswers = wrongAnswers;
        this.allAnswers = allAnswers;
    }

    public RepeatsSingleItem(String question) {
        setID = "new_set";
        this.Question = question;
    }

    public int getItemID() {
        return itemID;
    }

    public String getQuestion() {
        return Question;
    }

    public String getAnswer() {
        return Answer;
    }

    public String getImag() {
        return Imag;
    }

    public int getGoodAnswers() {
        return goodAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public int getAllAnswers() {
        return allAnswers;
    }

    public String getSetID() {
        return setID;
    }

    public String getSetName() {
        return setName;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public void setQuestion(String Question) {
        this.Question = Question;
    }

    public void setAnswer(String Answer) {
        this.Answer = Answer;
    }

    public void setImag(String Imag) {
        this.Imag = Imag;
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

    public void setSetID(String setID) {
        this.setID = setID;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }
}
