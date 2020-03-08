package com.rootekstudio.repeatsandroid;

public class RepeatsSingleSetDB
{
    private int ID;
    private String Question;
    private String Answer;
    private String Imag;
    private int goodAnswers;
    private int wrongAnswers;
    private int allAnswers;

    public RepeatsSingleSetDB(){}

    public RepeatsSingleSetDB(int ID, String Question, String Answer, String Imag, int goodAnswers, int wrongAnswers, int allAnswers)
    {
        this.ID = ID;
        this.Question = Question;
        this.Answer = Answer;
        this.Imag = Imag;
        this.goodAnswers = goodAnswers;
        this.wrongAnswers = wrongAnswers;
        this.allAnswers = allAnswers;
    }

    public int getID() {return ID; }

    public String getQuestion()
    {
        return Question;
    }

    public String getAnswer()
    {
        return Answer;
    }

    public String getImag()
    {
        return Imag;
    }

    public int getGoodAnswers() {return goodAnswers;}

    public int getWrongAnswers() {return wrongAnswers;}

    public int getAllAnswers() {return allAnswers;}

    public void setID(int ID) { this.ID = ID; }

    public void setQuestion(String Question)
    {
        this.Question = Question;
    }

    public void setAnswer(String Answer)
    {
        this.Answer = Answer;
    }

    public void setImag(String Imag)
    {
        this.Imag = Imag;
    }

    public void setGoodAnswers(int goodAnswers) {this.goodAnswers = goodAnswers;}

    public void setWrongAnswers(int wrongAnswers) {this.wrongAnswers = wrongAnswers;}

    public void setAllAnswers(int allAnswers) {this.allAnswers = allAnswers;}
}
