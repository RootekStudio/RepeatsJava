package com.rootekstudio.repeatsandroid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.Share;

public class ShareActivity extends AppCompatActivity {
    boolean shareToRC = true;
    String howShareRC = "PUBLIC";
    String name;
    String setId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RepeatsHelper.DarkTheme(this, false);

        setContentView(R.layout.activity_share);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        setId = intent.getStringExtra("id");

        TextView textViewName = findViewById(R.id.setNameShare);
        textViewName.setText(name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void selectHowShare(View view) {
        if(view.getId()==R.id.communityRadio) {
;           findViewById(R.id.publicRadio).setEnabled(true);
//            findViewById(R.id.groupRadio).setEnabled(true);
            findViewById(R.id.privateRadio).setEnabled(true);
            shareToRC = true;
        }
        else if(view.getId() == R.id.JSONradio) {
            findViewById(R.id.publicRadio).setEnabled(false);
//            findViewById(R.id.groupRadio).setEnabled(false);
            findViewById(R.id.privateRadio).setEnabled(false);

            shareToRC = false;
        }
    }

    public void selectShareCommunity(View view) {
        if(view.getId() == R.id.publicRadio) {
            howShareRC = "PUBLIC";
        }
//        else if(view.getId() == R.id.groupRadio) {
//            howShareRC = "GROUP";
//        }
        else if(view.getId() == R.id.privateRadio) {
            howShareRC = "PRIVATE";
        }
    }

    public void shareClick(View view) {
        view.setEnabled(false);
        findViewById(R.id.progressBarSharing).setVisibility(View.VISIBLE);

        if(shareToRC) {
            if(howShareRC.equals("PUBLIC")) {
                Share.shareToCommunity(this, setId, name,"PUBLIC", this);
            }
//            else if(howShareRC.equals("GROUP")) {
//                Share.shareToCommunity(this, setId, name,"GROUP", this);
//            }
            else if(howShareRC.equals("PRIVATE")) {
                Share.shareToCommunity(this, setId, name,"PRIVATE", this);
            }
        }
        else {
            Share.ShareClick(this, name, setId, this);
            finish();
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
