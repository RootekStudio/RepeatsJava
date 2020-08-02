package com.rootekstudio.repeatsandroid.community;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.UIHelper;
import com.rootekstudio.repeatsandroid.settings.SharedPreferencesManager;

import java.util.ArrayList;

public class MySetsActivity extends AppCompatActivity {

    ArrayList<QueryDocumentSnapshot> documents;
    private RecyclerView.Adapter mAdapter;
    FirebaseFirestore db;
    ArrayList<String> resultNames;
    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIHelper.DarkTheme(this, false);
        setContentView(R.layout.activity_my_sets);
        progressBar = findViewById(R.id.progressBarMySets);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        documents = new ArrayList<>();

        final RecyclerView recyclerView = findViewById(R.id.mySetsRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        db = FirebaseFirestore.getInstance();
        textView = findViewById(R.id.emptyMySetsText);

        String userID = SharedPreferencesManager.getInstance(this).getUserID();

        resultNames = new ArrayList<>();
        db.collection("sets")
                .whereEqualTo("userID", userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            documents.add(document);
                            resultNames.add(document.get("displayName").toString());
                        }

                        mAdapter = new MySetsListAdapter(resultNames);
                        mAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(mAdapter);
                        progressBar.setVisibility(View.GONE);
                        if (documents.size() == 0) {
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    public void previewSetMySets(View view) {
        int id = (Integer) view.findViewById(R.id.setNameMySetsList).getTag();
        CommunityHelper.getSetAndStartPreviewActivity(id, this, documents);
    }

    public void createLinkMySets(final View view) {
        view.setEnabled(false);
        View vParent = (View) view.getParent();
        final ProgressBar progressLink = vParent.findViewById(R.id.progressLink);
        progressLink.setVisibility(View.VISIBLE);
        int id = (Integer) vParent.findViewById(R.id.setNameMySetsList).getTag();
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://kubas20020.wixsite.com/repeatsc/" + "shareset/" + documents.get(id).getId()))
                .setDomainUriPrefix("https://repeats.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, task -> {
                    String dynamicLink = task.getResult().getShortLink().toString();

                    Intent shareLink = new Intent(Intent.ACTION_SEND);
                    shareLink.setType("text/plain");
                    shareLink.putExtra(Intent.EXTRA_SUBJECT, "");
                    shareLink.putExtra(Intent.EXTRA_TEXT, dynamicLink);
                    startActivity(Intent.createChooser(shareLink, getString(R.string.share)));

                    view.setEnabled(true);
                    progressLink.setVisibility(View.GONE);
                });
    }

    public void deleteSetMySets(View view) {
        View vParent = (View) view.getParent();
        int id = (Integer) vParent.findViewById(R.id.setNameMySetsList).getTag();
        db.collection("sets").document(documents.get(id).getId()).delete();
        resultNames.remove(id);
        mAdapter.notifyDataSetChanged();
        if (resultNames.size() == 0) {
            textView.setVisibility(View.VISIBLE);
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
