package com.rootekstudio.repeatsandroid.community;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PreviewAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> setsItems;

    public PreviewAdapter(Context cnt, ArrayList<String> set) {
        context = cnt;
        setsItems = set;
    }

    @Override
    public int getCount() {
        return setsItems.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView;
        if (view == null) {
            textView = new TextView(context);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PT, 7);
        } else {
            textView = (TextView) view;
        }

        textView.setText(setsItems.get(i));
        return textView;
    }
}
