package com.rootekstudio.repeatsandroid;

public class SingleFLitem {
    public String FLquestion;
    public String correctAnswer;
    public int correctAnswersInt;
    public int wrongAnswersInt;

    public SingleFLitem(String FLquestion, String correctAnswer, int correctAnswersInt, int wrongAnswersInt) {
        this.FLquestion = FLquestion;
        this.correctAnswer = correctAnswer;
        this.correctAnswersInt = correctAnswersInt;
        this.wrongAnswersInt = wrongAnswersInt;
    }

    public String getFLQuestion() {return FLquestion;}
    public String getCorrectAnswer() {return correctAnswer;}
    public int getCorrectAnswersInt() {return correctAnswersInt; }
    public int getWrongAnswersInt() {return wrongAnswersInt;}

    public void setFLquestion(String FLquestion) {this.FLquestion = FLquestion;}
    public void setCorrectAnswer(String correctAnswer) {this.correctAnswer = correctAnswer;}
    public void setCorrectAnswersInt(int correctAnswersInt) {this.correctAnswersInt = correctAnswersInt;}
    public void setWrongAnswersInt(int wrongAnswersInt) {this.wrongAnswersInt = wrongAnswersInt;}

}
