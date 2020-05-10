package com.rootekstudio.repeatsandroid.community;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class DynamicLinks extends AppCompatActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    // Get deep link from result (may be null if no link is found)
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();

                        String path = deepLink.getPath();
                        if (path.startsWith("/repeatsc/shareset/")) {
                            Intent intent = new Intent(context, PreviewAndDownloadSetActivity.class);
                            intent.putExtra("databaseSetID", path.substring(19));
                            startActivity(intent);

                        }
                    }
                    finish();
                })
                .addOnFailureListener(this, e -> {
                    e.printStackTrace();
                    finish();
                });
    }
}
