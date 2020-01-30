package com.rootekstudio.repeatsandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ReadSetsAloud {
    TextToSpeech textToSpeech;
    Context context;

//    public ReadSetsAloud(Context context) {
//        this.context = context;
//        initTTS();
//    }







    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }
}
