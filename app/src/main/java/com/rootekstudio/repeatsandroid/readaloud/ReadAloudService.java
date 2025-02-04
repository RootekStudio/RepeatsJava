package com.rootekstudio.repeatsandroid.readaloud;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.database.SetSingleItem;
import com.rootekstudio.repeatsandroid.mainpage.MainActivity;

import java.util.List;
import java.util.Locale;

public class ReadAloudService extends Service {
    private final IBinder binder = new ReadAloudBinder();
    private TextToSpeech textToSpeech;
    private TextToSpeech textToSpeech1;
    private List<SetSingleItem> singleSet;
    private String locale0;
    private String locale1;
    private NotificationCompat.Builder builder;

    class ReadAloudBinder extends Binder {
        ReadAloudService getService() {
            return ReadAloudService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locale0 = ReadAloudConnector.locale0;
        locale1 = ReadAloudConnector.locale1;

        singleSet = ReadAloudConnector.singleSet;
        ReadAloudConnector.isTTSStopped = false;
        Intent readAloudIntent = new Intent(this, ReadAloudActivity.class);
        readAloudIntent.putExtra("loadedFromNotification", true);
        readAloudIntent.putExtra("setID", ReadAloudConnector.setID);
        readAloudIntent.putExtra("speedRate", ReadAloudConnector.speechRate);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, readAloudIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        builder = new NotificationCompat.Builder(this, "RepeatsReadAloudChannel")
                .setContentTitle(getString(R.string.playing_set) + " " + ReadAloudConnector.setName)
                .setContentText(getString(R.string.click_here_read_notifi))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ear);

        startForeground(RequestCodes.READ_ALOUD_NOTIFICATION_ID, builder.build());

        initTTS();
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        if (textToSpeech1 != null) {
            textToSpeech1.stop();
            textToSpeech1.shutdown();
        }

        stopForeground(true);
        super.onDestroy();
    }

    private void initTTS() {
        textToSpeech = new TextToSpeech(this, i -> {
            if (i == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(new Locale(locale0));
                textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);
                textToSpeech.setSpeechRate(ReadAloudConnector.speechRate);

                textToSpeech1 = new TextToSpeech(ReadAloudService.this, i1 -> {
                    if (i1 == TextToSpeech.SUCCESS) {
                        textToSpeech1.setLanguage(new Locale(locale1));
                        textToSpeech1.setOnUtteranceProgressListener(utteranceProgressListener1);
                        textToSpeech1.setSpeechRate(ReadAloudConnector.speechRate);

                        textToSpeech.speak(" ", TextToSpeech.QUEUE_ADD, null, "starting");
                        textToSpeech1.speak(" ", TextToSpeech.QUEUE_ADD, null, "starting");
                    }
                });
            } else {
                Toast.makeText(ReadAloudService.this, getString(R.string.cannotLoadTTS), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ReadAloudService.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                stopSelf();
            }
        });
    }

    UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String s) {
        }

        @Override
        public void onDone(String s) {
            if (!ReadAloudConnector.isTTSStopped) {
                if (ReadAloudConnector.isActivityAlive) {
                    if (s.equals("starting")) {
                        return;
                    }

                    Intent intent = new Intent();
                    intent.setAction("com.rootekstudio.repeatsandroid");
                    intent.putExtra("speakingDone0", true);
                    sendBroadcast(intent);

                } else {
                    speak1();
                }
            }
        }

        @Override
        public void onError(String s) {
        }
    };

    UtteranceProgressListener utteranceProgressListener1 = new UtteranceProgressListener() {
        @Override
        public void onStart(String s) {
        }

        @Override
        public void onDone(String s) {
            if (!ReadAloudConnector.isTTSStopped) {
                if (ReadAloudConnector.isActivityAlive) {
                    Intent intent = new Intent();
                    intent.setAction("com.rootekstudio.repeatsandroid");

                    if (s.equals("starting")) {
                        intent.putExtra("loadingDone", true);
                        sendBroadcast(intent);
                        return;
                    }

                    ReadAloudConnector.speakItemIndex++;
                    ReadAloudConnector.speakItemSetIndex++;
                    intent.putExtra("speakingDone1", true);
                    sendBroadcast(intent);

                } else {
                    if (ReadAloudConnector.speakItemSetIndex != singleSet.size() - 1) {
                        ReadAloudConnector.speakItemSetIndex++;
                        speak0();
                    } else {
                        stopTextToSpeech();
                        stopForegroundServ();
                    }
                }
            }
        }

        @Override
        public void onError(String s) {
        }
    };

    public void stopTextToSpeech() {
        ReadAloudConnector.isTTSStopped = true;
        textToSpeech.stop();
        textToSpeech1.stop();
    }

    public void stopForegroundServ() {
        stopForeground(true);
    }

    public void resumeForeground() {
        startForeground(RequestCodes.READ_ALOUD_NOTIFICATION_ID, builder.build());
    }

    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }

    public TextToSpeech getTextToSpeech1() {
        return textToSpeech1;
    }

    public void setSpeechRate() {
        textToSpeech.setSpeechRate(ReadAloudConnector.speechRate);
        textToSpeech1.setSpeechRate(ReadAloudConnector.speechRate);
    }

    public void speak0() {
        checkAndChangeLocale();
        if (ReadAloudConnector.isTTSStopped) {
            ReadAloudConnector.isTTSStopped = false;
        }
        SetSingleItem singleSetDB = singleSet.get(ReadAloudConnector.speakItemSetIndex);
        textToSpeech.speak(singleSetDB.getQuestion(), TextToSpeech.QUEUE_ADD, null, String.valueOf(ReadAloudConnector.speakItemIndex));
        ReadAloudConnector.speakItemIndex++;
    }

    public void speak1() {
        checkAndChangeLocale();
        SetSingleItem singleSetDB = singleSet.get(ReadAloudConnector.speakItemSetIndex);
        textToSpeech1.speak(singleSetDB.getAnswer(), TextToSpeech.QUEUE_ADD, null, String.valueOf(ReadAloudConnector.speakItemIndex));
        ReadAloudConnector.speakItemIndex++;
    }

    private void checkAndChangeLocale() {
        if (!locale0.equals(ReadAloudConnector.locale0)) {
            locale0 = ReadAloudConnector.locale0;
            textToSpeech.setLanguage(new Locale(locale0));
        } else if (!locale1.equals(ReadAloudConnector.locale1)) {
            locale1 = ReadAloudConnector.locale1;
            textToSpeech1.setLanguage(new Locale(locale1));
        }
    }
}
