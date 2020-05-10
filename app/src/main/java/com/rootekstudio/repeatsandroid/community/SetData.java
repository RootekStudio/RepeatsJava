package com.rootekstudio.repeatsandroid.community;

import java.util.HashMap;

class SetData {
    private static HashMap<Integer, String[]> setItems;
    private static String setName;
    private static String setCreationDate;

    static HashMap<Integer, String[]> getSetItems() {
        return setItems;
    }

    static String getSetName() {
        return setName;
    }

    static String getSetCreationDate() {
        return setCreationDate;
    }

    static void setSetItems(HashMap<Integer, String[]> setItems) {
        SetData.setItems = setItems;
    }

    static void setSetName(String setName) {
        SetData.setName = setName;
    }

    static void setSetCreationDate(String setCreationDate) {
        SetData.setCreationDate = setCreationDate;
    }
}
