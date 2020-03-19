package com.rootekstudio.repeatsandroid.mainfragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rootekstudio.repeatsandroid.MainActivityAdapter;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RepeatsSetInfo;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.activities.AddEditSetActivity;
import com.rootekstudio.repeatsandroid.community.RepeatsCommunityStartActivity;
import com.rootekstudio.repeatsandroid.database.DatabaseHelper;

import java.util.List;

public class SetsFragment extends Fragment {
    private Context context;
    private AppCompatActivity appCompatActivity;

    public SetsFragment(Context context, AppCompatActivity activity) {
        this.context = context;
        appCompatActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DatabaseHelper DB = new DatabaseHelper(context);
        List<RepeatsSetInfo> repeatsList = DB.AllItemsLIST(DatabaseHelper.ORDER_BY_ID_DESC);

        View view = LayoutInflater.from(context).inflate(R.layout.mainfragment_sets, null);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_main);
        RecyclerView.Adapter adapter = new MainActivityAdapter(repeatsList, context, appCompatActivity);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(fabClick);

        return view;
    }

    private View.OnClickListener fabClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final MaterialAlertDialogBuilder ALERTbuilder = new MaterialAlertDialogBuilder(getContext());
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            final View view1 = layoutInflater.inflate(R.layout.addnew_item, null);
            ALERTbuilder.setView(view1);
            ALERTbuilder.setTitle(R.string.AddSet);
            ALERTbuilder.setBackground(getContext().getDrawable(R.drawable.dialog_shape));
            final AlertDialog alert = ALERTbuilder.show();

            RelativeLayout relA = view1.findViewById(R.id.relAdd);
            RelativeLayout relR = view1.findViewById(R.id.relRead);
            RelativeLayout relRC = view1.findViewById(R.id.relRC);

            relA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addEditActivityIntent = new Intent(getContext(), AddEditSetActivity.class);
                    addEditActivityIntent.putExtra("ISEDIT", "FALSE");
                    addEditActivityIntent.putExtra("IGNORE_CHARS", "false");
                    alert.dismiss();

                    startActivity(addEditActivityIntent);
                }
            });

            relR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent zipPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    zipPickerIntent.setType("application/*");
                    try {
                        startActivityForResult(zipPickerIntent, RequestCodes.READ_SHARED);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(), R.string.explorerNotFound, Toast.LENGTH_LONG).show();
                    }
                    alert.dismiss();
                }
            });

            relRC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentRC = new Intent(getContext(), RepeatsCommunityStartActivity.class);
                    alert.dismiss();
                    startActivity(intentRC);
                }
            });
        }
    };
}
