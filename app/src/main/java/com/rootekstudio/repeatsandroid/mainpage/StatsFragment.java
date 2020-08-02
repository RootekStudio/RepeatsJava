package com.rootekstudio.repeatsandroid.mainpage;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.transition.Hold;
import com.google.android.material.transition.MaterialFadeThrough;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.database.RepeatsDatabase;
import com.rootekstudio.repeatsandroid.database.Values;
import com.rootekstudio.repeatsandroid.statistics.SetStats;
import com.rootekstudio.repeatsandroid.statistics.StatsActivityAdapter;

import java.util.List;

public class StatsFragment extends Fragment {
    private int usableWidth;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RepeatsDatabase DB;

    public StatsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEnterTransition(new MaterialFadeThrough());
        setExitTransition(new MaterialFadeThrough());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mainfragment_stats, container, false);
        DB = RepeatsDatabase.getInstance(requireContext());

        usableWidth = getUsableWidth();
        recyclerView = view.findViewById(R.id.recyclerViewStats);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        ProgressBar progressBar = view.findViewById(R.id.progressBarAppStats);
        TextView percentGoodAppTextView = view.findViewById(R.id.goodPercentAppStats);
        TextView goodCountAppStats = view.findViewById(R.id.goodAnswersCountAppStats);
        TextView wrongCountAppStats = view.findViewById(R.id.wrongAnswersCountAppStats);
        TextView allCountAppStats = view.findViewById(R.id.allAnswersCountAppStats);

        LinearLayout linearSortBy = view.findViewById(R.id.linearSortByStats);
        linearSortBy.setOnClickListener(sortStatsClick);

        int goodAnswers = DB.columnSum(Values.sets_info, Values.good_answers);
        int wrongAnswers = DB.columnSum(Values.sets_info, Values.wrong_answers);
        int allAnswers = goodAnswers + wrongAnswers;

        if(allAnswers == 0) {
            view.findViewById(R.id.textAppStats).setVisibility(View.GONE);
            view.findViewById(R.id.noDataAppStatsTextView).setVisibility(View.VISIBLE);
            view.findViewById(R.id.textCorrectAnswersInProgressBar).setVisibility(View.GONE);
            progressBar.setBackground(view.getContext().getDrawable(R.drawable.progress_bar_empty_shape));
        }
        else {
            float goodPercentFloat = (float) (goodAnswers * 100) / allAnswers;
            int goodPercentInt = Math.round(goodPercentFloat);
            String goodPercent = goodPercentInt + "%";

            progressBar.setProgress(goodPercentInt);
            percentGoodAppTextView.setText(goodPercent);
            goodCountAppStats.setText(String.valueOf(goodAnswers));
            wrongCountAppStats.setText(String.valueOf(wrongAnswers));
            allCountAppStats.setText(String.valueOf(allAnswers));
        }



        List<SetStats> setsStats = DB.selectSetsStatsInfo(Values.ORDER_BY_GOOD_ANSWERS_RATIO);
        adapter = new StatsActivityAdapter(setsStats, usableWidth);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private View.OnClickListener sortStatsClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PopupMenu popupMenu = new PopupMenu(requireContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.sort_options, popupMenu.getMenu());
            MenuPopupHelper menuPopupHelper = new MenuPopupHelper(getContext(), (MenuBuilder) popupMenu.getMenu(), view);
            menuPopupHelper.setForceShowIcon(true);
            menuPopupHelper.show();

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.sortGoodAnswers) {
                    List<SetStats> setsStats = DB.selectSetsStatsInfo(Values.ORDER_BY_GOOD_ANSWERS_RATIO);
                    adapter = new StatsActivityAdapter(setsStats, usableWidth);
                    recyclerView.setAdapter(adapter);
                } else if (item.getItemId() == R.id.sortWrongAnswers) {
                    List<SetStats> setsStats = DB.selectSetsStatsInfo(Values.ORDER_BY_WRONG_ANSWERS_RATIO);
                    adapter = new StatsActivityAdapter(setsStats, usableWidth);
                    recyclerView.setAdapter(adapter);
                } else if (item.getItemId() == R.id.sortCreationDateAscending) {
                    List<SetStats> setsStats = DB.selectSetsStatsInfo(Values.ORDER_BY_ID_ASC);
                    adapter = new StatsActivityAdapter(setsStats, usableWidth);
                    recyclerView.setAdapter(adapter);

                } else if (item.getItemId() == R.id.sortCreationDateDescending) {
                    List<SetStats> setsStats = DB.selectSetsStatsInfo(Values.ORDER_BY_ID_DESC);
                    adapter = new StatsActivityAdapter(setsStats, usableWidth);
                    recyclerView.setAdapter(adapter);
                }
                return true;
            });
        }
    };

    private int getUsableWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) requireContext().getSystemService(Context.WINDOW_SERVICE);
        assert windowmanager != null;
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);

        float density = displayMetrics.density;
        float widthDp = displayMetrics.widthPixels / density;
        float relativeWidthDp = widthDp - 50;
        return Math.round(relativeWidthDp * density);
    }
}
