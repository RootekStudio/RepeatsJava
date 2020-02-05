package com.rootekstudio.repeatsandroid.readAloud;

import com.rootekstudio.repeatsandroid.RepeatsSingleSetDB;

import java.util.List;

public class ReadAloudConnector {
    public static int speakItemIndex = 0;
    public static int speakItemSetIndex = 0;
    public static String locale0;
    public static String locale1;
    public static boolean isActivityAlive = false;
    public static boolean isTTSStopped = true;
    public static List<RepeatsSingleSetDB> singleSet;
    public static String setName;
    public static String setID;
    public static float speechRate = 0.5f;
    public static boolean returnFromSettings = false;
}
