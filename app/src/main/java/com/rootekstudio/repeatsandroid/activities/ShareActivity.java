package com.rootekstudio.repeatsandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.Share;

import java.util.ArrayList;

public class ShareActivity extends AppCompatActivity {
    boolean shareToRC = true;
    String howShareRC = "PUBLIC";
    String name;
    String setId;
    ArrayList<String> tagsArray;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RepeatsHelper.DarkTheme(this, false);

        setContentView(R.layout.activity_share);
        editText = findViewById(R.id.editTextTags);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        setId = intent.getStringExtra("id");

        TextView textViewName = findViewById(R.id.setNameShare);
        textViewName.setText(name);

        String nameTag = name.replaceAll(" ", ";");
        nameTag = nameTag + ";";

        editText.setText(nameTag);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void selectHowShare(View view) {
        if(view.getId()==R.id.communityRadio) {
;           findViewById(R.id.publicRadio).setEnabled(true);
            findViewById(R.id.privateRadio).setEnabled(true);
            findViewById(R.id.imageSharingInfo).setVisibility(View.VISIBLE);
            findViewById(R.id.tagsLinear).setVisibility(View.VISIBLE);
            shareToRC = true;
        }
        else if(view.getId() == R.id.JSONradio) {
            findViewById(R.id.publicRadio).setEnabled(false);
            findViewById(R.id.privateRadio).setEnabled(false);
            findViewById(R.id.imageSharingInfo).setVisibility(View.GONE);
            findViewById(R.id.tagsLinear).setVisibility(View.GONE);

            shareToRC = false;
        }
    }

    public void selectShareCommunity(View view) {
        if(view.getId() == R.id.publicRadio) {
            howShareRC = "PUBLIC";
        }
        else if(view.getId() == R.id.privateRadio) {
            howShareRC = "PRIVATE";
        }
    }

    public void shareClick(View view) {
        view.setEnabled(false);
        findViewById(R.id.progressBarSharing).setVisibility(View.VISIBLE);

        String tags = editText.getText().toString();

        if(!tags.endsWith(";")) {
            tags = tags + ";";
        }

        tagsArray = new ArrayList<>();

        outerloop:
        while(tags.contains(";")) {
            String tag = tags.substring(0, tags.indexOf(";"));
            while (tag.equals("")) {
                tags = tags.replaceFirst(";", "");
                if(tags.length()==0) {
                    break outerloop;
                }
                tag = tags.substring(0, tags.indexOf(";"));
            }
            tags = tags.replace(tag + ";", "");

            String upper = tag.substring(0,1).toUpperCase() + tag.substring(1);
            tagsArray.add(upper);
            String lower = tag.substring(0,1).toLowerCase() + tag.substring(1);
            tagsArray.add(lower);
        }

        if(shareToRC) {
            if(howShareRC.equals("PUBLIC")) {
                Share.shareToCommunity(this, setId, name,"PUBLIC", tagsArray, this);
            }
            else if(howShareRC.equals("PRIVATE")) {
                Share.shareToCommunity(this, setId, name,"PRIVATE", tagsArray,this);
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
