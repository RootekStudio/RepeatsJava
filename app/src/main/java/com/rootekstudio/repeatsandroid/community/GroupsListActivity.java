package com.rootekstudio.repeatsandroid.community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RCmainListAdapter;
import com.rootekstudio.repeatsandroid.activities.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupsListActivity extends AppCompatActivity {

    Context context;
    ArrayList<String> resultNames;
    ArrayList<QueryDocumentSnapshot> documents;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    static QueryDocumentSnapshot selectedDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);
        context = this;
        recyclerView = findViewById(R.id.recyclerViewGroupsList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("groups")
                .whereArrayContains("members", MainActivity.mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            resultNames = new ArrayList<>();
                            documents = new ArrayList<>();

                            for(QueryDocumentSnapshot document : task.getResult()) {
                                documents.add(document);
                                resultNames.add(document.get("displayName").toString());
                            }

                            mAdapter = new RCmainListAdapter(resultNames, 1);
                            mAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(mAdapter);
                        }
                    }
                });
    }

    public void groupClick(View view) {
        int tag = (Integer)view.getTag();
        selectedDocument = documents.get(tag);
        Intent intent = new Intent(this, GroupActivity.class);
        startActivity(intent);
    }

    public void createGroupClick(View view) {
        AlertDialog.Builder ALERTbuilder = new AlertDialog.Builder(this);
        AlertDialog dialog;

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        final View view1 = layoutInflater.inflate(R.layout.ask, null);
        final EditText editText = view1.findViewById(R.id.EditAsk);

        editText.requestFocus();
        editText.setHint(R.string.groupName);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER ) {
                    EditText name = (EditText)view;
                    String nameText = name.getText().toString();
                    createGroup(nameText);
                }
                return false;
            }
        });

        ALERTbuilder.setView(view1);
        ALERTbuilder.setMessage(R.string.creatingGroup);
        ALERTbuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        ALERTbuilder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = editText.getText().toString();
                createGroup(text);

            }
        });

        dialog = ALERTbuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    void createGroup(String name) {
        if(!name.equals("")){
            ArrayList<String> members = new ArrayList<>();
            members.add(MainActivity.mAuth.getCurrentUser().getUid());
            ArrayList<String> sets = new ArrayList<>();

            Map<String, Object> groupInfo = new HashMap<>();
            groupInfo.put("admin", MainActivity.mAuth.getCurrentUser().getUid());
            groupInfo.put("displayName", name);
            groupInfo.put("members", members);
            groupInfo.put("sets", sets);

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("groups")
                    .add(groupInfo)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Intent intent = new Intent(context, GroupActivity.class);
                            startActivity(intent);
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("tag", e);
                        }
                    });
        }
    }
}
