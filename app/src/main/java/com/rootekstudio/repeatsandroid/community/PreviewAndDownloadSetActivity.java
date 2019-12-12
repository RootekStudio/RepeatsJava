package com.rootekstudio.repeatsandroid.community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rootekstudio.repeatsandroid.PreviewAdapter;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.RepeatsListDB;
import com.rootekstudio.repeatsandroid.activities.MainActivity;
import com.rootekstudio.repeatsandroid.community.RepeatsCommunityStartActivity;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PreviewAndDownloadSetActivity extends AppCompatActivity {
    ArrayList<String> setItems;
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

        Intent intent = getIntent();
        databaseSetID = intent.getStringExtra("databaseSetID");
        if(databaseSetID != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("sets").document(databaseSetID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult().exists()) {
                                    ArrayList<?> questions = (ArrayList<?>) task.getResult().get("questions");
                                    ArrayList<?> answers = (ArrayList<?>) task.getResult().get("answers");

                                    setItems = new ArrayList<>();

                                    for(int i = 0; i < questions.size(); i++) {
                                        setItems.add(questions.get(i).toString());
                                        setItems.add(answers.get(i).toString());
                                    }

                                    setName = task.getResult().get("displayName").toString();

                                    GridView gridView = findViewById(R.id.gridSetItemsList);
                                    gridView.setAdapter(new PreviewAdapter(context, setItems));

                                    findViewById(R.id.downloadSetButton).setEnabled(true);

                                    TextView textView = findViewById(R.id.setNamePreview);
                                    textView.setText(setName);
                                }


                            } else {
                                Log.d("tag", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        else {
            setItems = RepeatsCommunityStartActivity.setItems;
            setName = RepeatsCommunityStartActivity.setName;

            GridView gridView = findViewById(R.id.gridSetItemsList);
            gridView.setAdapter(new PreviewAdapter(this, setItems));

            TextView textView = findViewById(R.id.setNamePreview);
            textView.setText(setName);

            findViewById(R.id.downloadSetButton).setEnabled(true);
        }
    }

    public void downloadSet(View view) {
        MaterialButton button = (MaterialButton) view;
        button.setText(R.string.downloading);
        button.setEnabled(false);

        DatabaseHelper DB = new DatabaseHelper(this);

        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = s.format(new Date());
        String id = "R" + date;

        SimpleDateFormat createD = new SimpleDateFormat("dd.MM.yyyy");
        String createDate = createD.format(new Date());

        ArrayList<String> set = setItems;
        ArrayList<String> questions = new ArrayList<>();
        ArrayList<String> answers = new ArrayList<>();

        for(int i = 2; i< set.size(); i += 2) {
            questions.add(set.get(i));
        }

        for(int i = 3; i < set.size(); i += 2) {
            answers.add(set.get(i));
        }

        RepeatsListDB list = new RepeatsListDB(setName, id, createDate, "true", "", "false");

        //Registering set in database
        DB.CreateSet(id);
        DB.AddName(list);
        DB.insertSetToDatabase(id, questions, answers, null);

        Toast.makeText(this, R.string.successDownload, Toast.LENGTH_SHORT).show();

        if(databaseSetID != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else {
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
