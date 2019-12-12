package com.rootekstudio.repeatsandroid.community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RCmainListAdapter;
import com.rootekstudio.repeatsandroid.RepeatsHelper;

import java.util.ArrayList;
import java.util.Objects;

public class RepeatsCommunityStartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<String> resultNames;
    ArrayList<QueryDocumentSnapshot> documents;
    static ArrayList<String> setItems;
    static String setName;
    FirebaseFirestore db;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RepeatsHelper.DarkTheme(this, false);
        setContentView(R.layout.activity_repeats_community_start);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.progressBarSearchC);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.resultsRecycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        final EditText search = findViewById(R.id.searchRC);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if(id == EditorInfo.IME_ACTION_SEARCH) {
                    String queryText = search.getText().toString();
                    if (!queryText.equals("")) {
                        progressBar.setVisibility(View.VISIBLE);
                        search(queryText);
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        Objects.requireNonNull(getCurrentFocus()).clearFocus();

                        return true;
                    }
                }
                return false;
            }
        });
    }

    void search(String text) {
        db.collection("sets")
                .orderBy("displayName")
                .startAt(text)
                .endAt(text + "\uf8ff")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            documents = new ArrayList<>();
                            resultNames = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String a = (String)document.get("availability");
                                if(a.equals("PUBLIC")) {
                                    documents.add(document);
                                    resultNames.add(document.get("displayName").toString());
                                }
                            }

                            mAdapter = new RCmainListAdapter(resultNames,0);
                            mAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(mAdapter);

                            progressBar.setVisibility(View.GONE);
                        } else {
                            Log.d("tag", "Error getting documents: ", task.getException());
                        }
                    }
                });

//        for(int i = 0; i < 100; i++) {
//            resultNames.add("test");
//        }
//
//        mAdapter = new RCmainListAdapter(resultNames);
//        mAdapter.notifyDataSetChanged();
//        recyclerView.setAdapter(mAdapter);

    }

    public void searchResultClick(View view) {
        int tag = (Integer) view.findViewById(R.id.setNameListItemRC).getTag();
        setItems = new ArrayList<>();
        QueryDocumentSnapshot doc = documents.get(tag);
        ArrayList<?> questions = (ArrayList<?>) doc.get("questions");
        ArrayList<?> answers = (ArrayList<?>) doc.get("answers");
        setName = doc.get("displayName").toString();

        setItems.add(getString(R.string.questions));
        setItems.add(getString(R.string.answers));

        for(int i = 0; i < questions.size(); i++) {
            setItems.add(questions.get(i).toString());
            setItems.add(answers.get(i).toString());
        }

        Intent intent = new Intent(this, PreviewAndDownloadSetActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
