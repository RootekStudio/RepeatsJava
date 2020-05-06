package com.rootekstudio.repeatsandroid.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.firstrun.FirstRunActivity;

public class AppInfoActivity extends AppCompatActivity {
    int clicked = 0;
    String versionName;
    String versionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getVersionInfo();

        TextView versionNameTextView =      findViewById(R.id.versionNameTextView   );
        TextView versionNumberTextView =    findViewById(R.id.versionNumberTextView );
        ImageView logo =                    findViewById(R.id.logoAppInfo           );
        
        versionNameTextView.setText(versionName);
        versionNumberTextView.setText(versionNumber);
        if(!RepeatsHelper.DarkTheme(this, true)) {
            logo.setImageDrawable(getDrawable(R.drawable.repeats_for_light_bg));
        }
    }

    private void getVersionInfo() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
            versionNumber = String.valueOf(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void numberClick(View view) {
        clicked++;
        if(clicked == 5) {
            startActivity(new Intent(this, FirstRunActivity.class));
            clicked = 0;
        }
    }

    public void termsOfUseClick(View view) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://rootekstudio.wordpress.com/warunki-uzytkowania")));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.browserNotFound, Toast.LENGTH_LONG).show();
        }
    }

    public void privacyPolicyClick(View view) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://rootekstudio.wordpress.com/polityka-prywatnosci")));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.browserNotFound, Toast.LENGTH_LONG).show();
        }
    }

    public void sendFeedbackClick(View view) {
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("plain/text");
        send.putExtra(Intent.EXTRA_EMAIL, new String[]{"rootekstudio@outlook.com"});
        send.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.FeedbackSubject) + " " + versionName);

        startActivity(Intent.createChooser(send, getString(R.string.SendFeedback)));
    }

    public void rateAppClick(View view) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.rootekstudio.repeatsandroid")));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.storeNotFound, Toast.LENGTH_LONG).show();
        }
    }

    public void openSourceLicencesClick(View view) {
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.openSourceLicences));
        startActivity(new Intent(this, OssLicensesMenuActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
