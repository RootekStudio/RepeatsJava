package com.rootekstudio.repeatsandroid.community;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rootekstudio.repeatsandroid.R;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        recyclerView = findViewById(R.id.recyclerViewGroupsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        QueryDocumentSnapshot doc = GroupsListActivity.selectedDocument;
        ArrayList<?> setsID = (ArrayList<?>)doc.get("sets");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("sets");

    }
}
