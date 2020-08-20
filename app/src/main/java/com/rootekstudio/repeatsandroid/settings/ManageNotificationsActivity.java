package com.rootekstudio.repeatsandroid.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.Values;
import com.rootekstudio.repeatsandroid.notifications.NotificationInfo;
import com.rootekstudio.repeatsandroid.notifications.NotificationsScheduler;

import java.util.List;

public class ManageNotificationsActivity extends AppCompatActivity {

    LinearLayout mainLinearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_notifications);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mainLinearLayout = findViewById(R.id.linearManageNotifications);
        mainLinearLayout.removeAllViews();

        List<NotificationInfo> notificationInfoList = RepeatsDatabase.getInstance(this).getInfoAboutAllNotifications(Values.ORDER_BY_ID_DESC);
        for(int i = 0; i < notificationInfoList.size(); i++) {
            NotificationInfo notificationInfo = notificationInfoList.get(i);

            View view = LayoutInflater.from(this).inflate(R.layout.single_notification_settings_card,null);

            FrameLayout frame = view.findViewById(R.id.manageNotificationsFrame);
            TextView setName = view.findViewById(R.id.setNameNotificationSettings);
            RelativeLayout switchAndNameRelative = view.findViewById(R.id.relativeSwitchNotificationSettings);
            Switch notificationSwitch = view.findViewById(R.id.notificationSwitchSettings);
            RelativeLayout changeSettingsRelative = view.findViewById(R.id.relativeChangeSettingsNotification);

            frame.setTag(notificationInfo.getSetID());
            setName.setText(RepeatsDatabase.getInstance(this).setNameResolver(notificationInfo.getSetID()));

            if(notificationInfo.getMode() == 1) {
                notificationSwitch.setChecked(true);
            } else {
                notificationSwitch.setChecked(false);
            }

            switchAndNameRelative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Switch localSwitch = v.findViewById(R.id.notificationSwitchSettings);
                    localSwitch.setChecked(!localSwitch.isChecked());
                }
            });

            notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    View viewParent = (View)buttonView.getParent().getParent().getParent();
                    String setID = (String)viewParent.getTag();
                    if(RepeatsDatabase.getInstance(buttonView.getContext()).singleSetNotificationInfo(setID).getHours() == null) {
                        Toast.makeText(ManageNotificationsActivity.this, getString(R.string.set_rules_for_notifi), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ManageNotificationsActivity.this, EditNotificationsForSetActivity.class);
                        intent.putExtra("setID", setID);
                        intent.putExtra("fromSettings", true);
                        intent.putExtra("requestedTurnOn", true);
                        startActivity(intent);
                        buttonView.setChecked(false);
                    } else {
                        RepeatsDatabase.getInstance(buttonView.getContext()).updateNotificationEnabled(setID, isChecked);
                        NotificationsScheduler.restartNotifications(buttonView.getContext());
                    }
                }
            });

            changeSettingsRelative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View viewParent = (View)v.getParent().getParent();
                    String setID = (String)viewParent.getTag();

                    Intent intent = new Intent(v.getContext(), EditNotificationsForSetActivity.class);
                    intent.putExtra("setID", setID);
                    intent.putExtra("fromSettings", true);
                    startActivity(intent);
                }
            });

            mainLinearLayout.addView(view);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}