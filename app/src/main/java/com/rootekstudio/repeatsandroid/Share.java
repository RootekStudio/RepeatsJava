package com.rootekstudio.repeatsandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Share {
    public static void ShareClick(final Context context, final String name, final String setID, final Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view1 = layoutInflater.inflate(R.layout.progress, null);
        ProgressBar progressBar = view1.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        builder.setView(view1);
        builder.setMessage(R.string.savingInProgress);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                ArrayList<String> arrayName = new ArrayList<>();
                arrayName.add(name);

                ArrayList<String> arraySetsID = new ArrayList<>();
                arraySetsID.add(setID);

                SetToFile.saveSetsToFile(context, arraySetsID, arrayName);

                Uri zipUri = Uri.fromFile(SetToFile.zipFile);

                OutputStream outputStream = null;
                try {
                    outputStream = context.getContentResolver().openOutputStream(zipUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                ZipSet.zip(SetToFile.filesToShare, outputStream);

                RepeatsHelper.shareSets(context, activity);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }
        });

        thread.start();

    }

    public static void shareToCommunity(final Context context, String setID, String name, final String availability, final Activity activity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new user with a first and last name

        DatabaseHelper DB = new DatabaseHelper(context);
        List<RepeatsSingleSetDB> singleSet = DB.AllItemsSET(setID);

        ArrayList<String> questions = new ArrayList<>();
        ArrayList<String> answers = new ArrayList<>();

        for(int i = 0; i<singleSet.size(); i++) {
            questions.add(singleSet.get(i).getQuestion());
            answers.add(singleSet.get(i).getAnswer());
        }

        final Map<String, Object> set = new HashMap<>();
        set.put("availability", availability);
        set.put("displayName", name);
        set.put("questions", questions);
        set.put("answers", answers);

// Add a new document with a generated ID
        db.collection("sets")
                .add(set)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(context, R.string.successShare, Toast.LENGTH_SHORT).show();
                        if(availability.equals("PRIVATE")) {
                            Task<ShortDynamicLink> shortDynamicLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                                    .setLink(Uri.parse("https://rootekstudio.wordpress.com/" + "shareset/" +documentReference.getId()))
                                    .setDomainUriPrefix("https://repeats.page.link")
                                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                                    .buildShortDynamicLink()
                                    .addOnCompleteListener(activity, new OnCompleteListener<ShortDynamicLink>() {
                                        @Override
                                        public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                                            String dynamicLink = task.getResult().getShortLink().toString();

                                            Intent shareLink = new Intent(Intent.ACTION_SEND);
                                            shareLink.setType("text/plain");
                                            shareLink.putExtra(Intent.EXTRA_SUBJECT, "something");
                                            shareLink.putExtra(Intent.EXTRA_TEXT, dynamicLink);
                                            activity.startActivity(Intent.createChooser(shareLink, context.getString(R.string.share)));
                                        }
                                    });
                        }
//                        activity.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, R.string.somethingWentWrong, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
