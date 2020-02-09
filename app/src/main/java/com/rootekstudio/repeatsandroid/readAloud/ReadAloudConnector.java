package com.rootekstudio.repeatsandroid.readAloud;

import com.rootekstudio.repeatsandroid.RepeatsSingleSetDB;

import java.util.List;

public class ReadAloudConnector {
    static ReadAloudService readAloudService;
    static int speakItemIndex = 0;
    static int speakItemSetIndex = 0;
    public static String locale0;
    public static String locale1;
    static boolean isActivityAlive = false;
    public static boolean isTTSStopped = true;
    static List<RepeatsSingleSetDB> singleSet;
    static String setName;
    public static String setID;
    static float speechRate = 0.5f;
    public static boolean returnFromSettings = false;
}
