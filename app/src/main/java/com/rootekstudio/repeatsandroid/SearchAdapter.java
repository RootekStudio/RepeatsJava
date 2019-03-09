package com.rootekstudio.repeatsandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends BaseAdapter
{
    Context context;
    private LayoutInflater inflater;
    private List<SearchItem> items;
    private ArrayList<SearchItem> arrayItems;
    private Boolean IsDark;

    SearchAdapter(Context context, List<SearchItem> items)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.items = items;
        this.arrayItems = new ArrayList<>();
        this.arrayItems.addAll(items);
        IsDark = RepeatsHelper.DarkTheme(context);
    }

    @Override
    public int getCount()
    {
        return items.size();
    }

    @Override
    public Object getItem(int position)
    {
        return items.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
            convertView = inflater.inflate(R.layout.search_item, null);
            TextView t1 = convertView.findViewById(R.id.SearchQ);
            TextView t2 = convertView.findViewById(R.id.SearchA);
            TextView t3 =  convertView.findViewById(R.id.SearchS);

            SearchItem item = items.get(position);
            t1.setText(item.gQuestion());
            t2.setText(item.gAnswer());
            t3.setText(item.gTitle());

        return convertView;
    }

    void search(String text)
    {
        text = text.toLowerCase(Locale.getDefault()).replaceAll("[^A-Za-z0-9]", "");
        items.clear();

        if(text.length() == 0)
        {
            items.addAll(arrayItems);
        }
        else
        {
            for(SearchItem x : arrayItems)
            {
                if(x.gItem().toLowerCase(Locale.getDefault()).contains(text))
                {
                    items.add(x);
                }
            }
        }
        notifyDataSetChanged();
    }
}
