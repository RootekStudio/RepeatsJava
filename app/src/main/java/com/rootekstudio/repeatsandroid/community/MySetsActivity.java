package com.rootekstudio.repeatsandroid.community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rootekstudio.repeatsandroid.MySetsListAdapter;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RCmainListAdapter;
import com.rootekstudio.repeatsandroid.RepeatsHelper;
import com.rootekstudio.repeatsandroid.activities.MainActivity;

import java.util.ArrayList;

public class MySetsActivity extends AppCompatActivity {

    ArrayList<QueryDocumentSnapshot> documents;
    private RecyclerView.Adapter mAdapter;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RepeatsHelper.DarkTheme(this, false);
        setContentView(R.layout.activity_my_sets);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        documents = new ArrayList<>();

        final RecyclerView recyclerView = findViewById(R.id.mySetsRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        db = FirebaseFirestore.getInstance();

        final ArrayList<String> resultNames = new ArrayList<>();
        db.collection("sets")
                .whereEqualTo("userID", MainActivity.mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                documents.add(document);
                                resultNames.add(document.get("displayName").toString());
                            }

                            mAdapter = new MySetsListAdapter(resultNames);
                            mAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(mAdapter);
                        }
                    }
                });
    }

    public void previewSetMySets(View view) {
        int id = (Integer)view.findViewById(R.id.setNameMySetsList).getTag();
    }

    public void createLinkMySets(View view) {
        View vParent = (View)view.getParent();
        int id = (Integer)vParent.findViewById(R.id.setNameMySetsList).getTag();
        Task<ShortDynamicLink> shortDynamicLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://rootekstudio.wordpress.com/" + "shareset/" +documents.get(id).getId()))
                .setDomainUriPrefix("https://repeats.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        String dynamicLink = task.getResult().getShortLink().toString();

                        Intent shareLink = new Intent(Intent.ACTION_SEND);
                        shareLink.setType("text/plain");
                        shareLink.putExtra(Intent.EXTRA_SUBJECT, "something");
                        shareLink.putExtra(Intent.EXTRA_TEXT, dynamicLink);
                        startActivity(Intent.createChooser(shareLink, getString(R.string.share)));
                    }
                });
    }

    public void deleteSetMySets(View view) {
        View vParent = (View)view.getParent();
        int id = (Integer)vParent.findViewById(R.id.setNameMySetsList).getTag();
        db.collection("sets").document(documents.get(id).getId()).delete();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
