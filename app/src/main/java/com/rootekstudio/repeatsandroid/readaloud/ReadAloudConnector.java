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
    public static String setID;
    static float speechRate = 0.5f;
    public static boolean returnFromSettings = false;
}
