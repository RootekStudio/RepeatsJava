package com.rootekstudio.repeatsandroid.search;

public class SearchItem {
    private String Question;
    private String Answer;
    private String Title;
    private String TableName;
    private String AllItem;

    public SearchItem(String Question, String Answer, String Title, String TableName) {
        this.Question = Question;
        this.Answer = Answer;
        this.Title = Title;
        this.TableName = TableName;
        this.AllItem = Question + System.getProperty("line.separator") + Answer + System.getProperty("line.separator") + Title;
    }

    String gQuestion() {
        return Question;
    }

    String gAnswer() {
        return Answer;
    }

    String gTitle() {
        return Title;
    }

    String gTableName() {
        return TableName;
    }

    String gItem() {
        return AllItem;
    }

    public void sQuestion(String Question) {
        this.Question = Question;
    }

    public void sAnswer(String Answer) {
        this.Answer = Answer;
    }

    public void sTitle(String Title) {
        this.Title = Title;
    }

    public void sTableName(String TableName) {
        this.TableName = TableName;
    }
}
