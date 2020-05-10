package com.rootekstudio.repeatsandroid.readaloud;

import com.rootekstudio.repeatsandroid.database.SetSingleItem;

import java.util.List;

public class ReadAloudConnector {
    static ReadAloudService readAloudService;
    static int speakItemIndex = 0;
    static int speakItemSetIndex = 0;
    public static String locale0;
    public static String locale1;
    static boolean isActivityAlive = false;
    public static boolean isTTSStopped = true;
    static List<SetSingleItem> singleSet;
    static String setName;
    static String setID;
    static float speechRate = 0.5f;
    public static boolean returnFromSettings = false;

    static void reset() {
        readAloudService = null;
        ReadAloudConnector.isTTSStopped = false;
        ReadAloudConnector.speakItemIndex = 0;
        ReadAloudConnector.speechRate = 0.5f;
        ReadAloudConnector.isActivityAlive = true;
        ReadAloudConnector.singleSet = null;
        ReadAloudConnector.speakItemSetIndex = 0;
        ReadAloudConnector.locale0 = null;
        ReadAloudConnector.locale1 = null;
        ReadAloudConnector.setID = null;
        ReadAloudConnector.setName = null;
        ReadAloudConnector.returnFromSettings = false;
    }
}
