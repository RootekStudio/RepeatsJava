package com.rootekstudio.repeatsandroid.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.rootekstudio.repeatsandroid.Backup;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.mainpage.PreferenceFragment;

public class SettingsActivity extends AppCompatActivity {
    public static FragmentActivity activity;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        context = this;

        RepeatsHelper.DarkTheme(this, false);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new PreferenceFragment())
                .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.SELECT_FILE_TO_RESTORE) {
            if (resultCode == RESULT_OK) {
                Backup.restoreBackup(context, data, this);
            }
        }
        if (requestCode == RequestCodes.PICK_CATALOG) {
            if (resultCode == RESULT_OK) {
                Backup.saveBackupLocally(context, data, this);
            }
        }
    }


}
