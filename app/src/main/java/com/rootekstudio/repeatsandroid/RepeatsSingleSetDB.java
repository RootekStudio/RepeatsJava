package com.rootekstudio.repeatsandroid;

public class RepeatsSingleSetDB
{
    public String Question;
    public String Answer;
    public String Imag;

    public RepeatsSingleSetDB(){}

    public RepeatsSingleSetDB(String Question, String Answer, String Imag)
    {
        this.Question = Question;
        this.Answer = Answer;
        this.Imag = Imag;
    }

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
