package com.rootekstudio.repeatsandroid.community;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.SetsConfigHelper;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.mainpage.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PreviewAndDownloadSetActivity extends AppCompatActivity {
    HashMap<Integer, String[]> setItems;
    String setName;
    Context context;
    String databaseSetID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RepeatsHelper.DarkTheme(this, false);
        setContentView(R.layout.activity_preview_and_download_set);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;

        TextView elementsCountTextView = findViewById(R.id.textViewElementsCountRC);
        TextView shareDateTextView = findViewById(R.id.textViewShareDateRC);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewDownloadSet);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        Intent intent = getIntent();
        databaseSetID = intent.getStringExtra("databaseSetID");
        if (databaseSetID != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("sets").document(databaseSetID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    ArrayList<?> questions = (ArrayList<?>) task.getResult().get("questions");
                                    ArrayList<?> answers = (ArrayList<?>) task.getResult().get("answers");

                                    setItems = new HashMap<>();

                                    for (int i = 0; i < questions.size(); i++) {
                                        setItems.put(i, new String[] {questions.get(i).toString(), answers.get(i).toString()});
                                    }

                                    setName = task.getResult().get("displayName").toString();

                                    recyclerView.setAdapter(new PreviewAdapter(setItems));
                                    findViewById(R.id.downloadSetButton).setEnabled(true);

                                    TextView textView = findViewById(R.id.setNamePreview);
                                    textView.setText(setName);

                                    String elementsString = setItems.size() + " " + getText(R.string.items).toString();
                                    elementsCountTextView.setText(elementsString);

                                    String shareDateString = getText(R.string.shareDate) + " " + task.getResult().get("creationDate").toString();
                                    shareDateTextView.setText(shareDateString);

                                }
                            } else {
                                Log.d("tag", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        } else {
            setItems = RepeatsHelper.setItems;
            setName = RepeatsHelper.setName;

            recyclerView.setAdapter(new PreviewAdapter(setItems));

            TextView textView = findViewById(R.id.setNamePreview);
            textView.setText(setName);

            String elementsString = setItems.size() + " " + getText(R.string.items).toString();
            elementsCountTextView.setText(elementsString);

            String shareDateString = getText(R.string.shareDate) + " " + RepeatsHelper.setCreationDate;
            shareDateTextView.setText(shareDateString);

            findViewById(R.id.downloadSetButton).setEnabled(true);
        }
    }

    public void downloadSet(View view) {
        MaterialButton button = (MaterialButton) view;
        button.setEnabled(false);

        String id = new SetsConfigHelper(this).createNewSet(false, "");

        HashMap<Integer, String[]> set = setItems;
        ArrayList<String> questions = new ArrayList<>();
        ArrayList<String> answers = new ArrayList<>();

        for(int i = 1; i < set.size(); i++) {
            questions.add(Objects.requireNonNull(set.get(i))[0]);
            answers.add(Objects.requireNonNull(set.get(i))[1]);
        }

        new RepeatsDatabase(this).insertSetToDatabase(id, questions, answers, null);

        Toast.makeText(this, R.string.successDownload, Toast.LENGTH_SHORT).show();

        if (databaseSetID != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
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
