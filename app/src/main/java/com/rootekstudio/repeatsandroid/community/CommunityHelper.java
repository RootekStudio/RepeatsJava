package com.rootekstudio.repeatsandroid.community;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

class CommunityHelper {
    static void getSetAndStartPreviewActivity(int id, Context context, ArrayList<QueryDocumentSnapshot> documents) {
        HashMap<Integer, String[]> setItems = new HashMap<>();
        QueryDocumentSnapshot doc = documents.get(id);
        ArrayList<?> questions = (ArrayList<?>) doc.get("questions");
        ArrayList<?> answers = (ArrayList<?>) doc.get("answers");

        for (int i = 0; i < questions.size(); i++) {
            setItems.put(i, new String[] {questions.get(i).toString(), answers.get(i).toString()});
        }

        SetData.setSetName(doc.get("displayName").toString());
        SetData.setSetCreationDate(doc.get("creationDate").toString());
        SetData.setSetItems(setItems);

        Intent intent = new Intent(context, PreviewAndDownloadSetActivity.class);
        context.startActivity(intent);
    }
}
