package com.rootekstudio.repeatsandroid.mainpage;

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

        if (repeatsList.size() == 0) {
            RelativeLayout emptyInfo = view.findViewById(R.id.EmptyHereText);
            emptyInfo.setVisibility(View.VISIBLE);
        }

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
             AddSetNavFragment addSetNavFragment = AddSetNavFragment.newInstance();
             addSetNavFragment.show(getActivity().getSupportFragmentManager(), "setNav");
        }
    };
}
