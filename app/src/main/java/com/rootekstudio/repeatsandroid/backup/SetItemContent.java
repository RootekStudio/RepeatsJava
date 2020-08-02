package com.rootekstudio.repeatsandroid.backup;

public class SetItemContent {
    private int id;
    private String question;
    private String answer;
    private String image;
    private int goodAnswers;
    private int wrongAnswers;

    public SetItemContent() {}

    public SetItemContent(int id, String question, String answer, String image, int goodAnswers, int wrongAnswers) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.image = image;
        this.goodAnswers = goodAnswers;
        this.wrongAnswers = wrongAnswers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getGoodAnswers() {
        return goodAnswers;
    }

    public void setGoodAnswers(int goodAnswers) {
        this.goodAnswers = goodAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }
}
