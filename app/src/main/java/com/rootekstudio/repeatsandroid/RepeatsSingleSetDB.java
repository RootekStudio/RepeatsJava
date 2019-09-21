package com.rootekstudio.repeatsandroid;

public class RepeatsSingleSetDB
{
    public int ID;
    public String Question;
    public String Answer;
    public String Imag;

    public RepeatsSingleSetDB(){}

    public RepeatsSingleSetDB(int ID, String Question, String Answer, String Imag)
    {
        this.ID = ID;
        this.Question = Question;
        this.Answer = Answer;
        this.Imag = Imag;
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
}
