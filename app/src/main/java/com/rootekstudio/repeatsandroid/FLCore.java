package com.rootekstudio.repeatsandroid;

public class FLCore {

    static public void core(String answer, SingleFLitem FLitem) {
        if(CheckAnswer.isAnswerCorrect(answer, FLitem.getCorrectAnswer(), "false")) {
            int correct = FLitem.getCorrectAnswersInt();
            correct++;
            FLitem.setCorrectAnswersInt(correct);
        }
        else {
            int wrong = FLitem.getWrongAnswersInt();
            wrong++;
            FLitem.setWrongAnswersInt(wrong);
        }
    }
}
