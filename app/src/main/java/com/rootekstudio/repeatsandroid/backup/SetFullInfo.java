package com.rootekstudio.repeatsandroid.backup;

public class SetFullInfo {
    private String set_id;
    private String set_name;
    private String creation_date;
    private int enabled;
    private int ignore_chars;
    private String first_lang;
    private String second_lang;
    private int good_answers;
    private int wrong_answers;

    public SetFullInfo() {}

    public SetFullInfo(String set_id, String set_name, String creation_date, int enabled, int ignore_chars, String first_lang, String second_lang, int good_answers, int wrong_answers) {
        this.set_id = set_id;
        this.set_name = set_name;
        this.creation_date = creation_date;
        this.enabled = enabled;
        this.ignore_chars = ignore_chars;
        this.first_lang = first_lang;
        this.second_lang = second_lang;
        this.good_answers = good_answers;
        this.wrong_answers = wrong_answers;
    }

    public String getSet_id() {
        return set_id;
    }

    public void setSet_id(String set_id) {
        this.set_id = set_id;
    }

    public String getSet_name() {
        return set_name;
    }

    public void setSet_name(String set_name) {
        this.set_name = set_name;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public int getIgnore_chars() {
        return ignore_chars;
    }

    public void setIgnore_chars(int ignore_chars) {
        this.ignore_chars = ignore_chars;
    }

    public String getFirst_lang() {
        return first_lang;
    }

    public void setFirst_lang(String first_lang) {
        this.first_lang = first_lang;
    }

    public String getSecond_lang() {
        return second_lang;
    }

    public void setSecond_lang(String second_lang) {
        this.second_lang = second_lang;
    }

    public int getGood_answers() {
        return good_answers;
    }

    public void setGood_answers(int good_answers) {
        this.good_answers = good_answers;
    }

    public int getWrong_answers() {
        return wrong_answers;
    }

    public void setWrong_answers(int wrong_answers) {
        this.wrong_answers = wrong_answers;
    }
}
