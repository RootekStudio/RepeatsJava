package com.rootekstudio.repeatsandroid.mainpage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.SetsConfigHelper;
import com.rootekstudio.repeatsandroid.activities.AddEditSetActivity;
import com.rootekstudio.repeatsandroid.activities.SetSettingsActivity;
import com.rootekstudio.repeatsandroid.activities.ShareActivity;
import com.rootekstudio.repeatsandroid.database.SingleSetInfo;

import java.util.List;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MainViewHolder> {
    private List<SingleSetInfo> repeatsList;
    private Context context;
    private AppCompatActivity activity;

    static class MainViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;

        MainViewHolder(RelativeLayout rl) {
            super(rl);
            relativeLayout = rl;
        }
    }

    public MainActivityAdapter(List<SingleSetInfo> repeatsSetInfo, Context context, AppCompatActivity activity) {
        repeatsList = repeatsSetInfo;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public MainActivityAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mainactivitylistitem, parent, false);
        return new MainViewHolder(relativeLayout);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        RelativeLayout view = holder.relativeLayout;
        SingleSetInfo Item = repeatsList.get(position);

        RelativeLayout but = view.findViewById(R.id.RelativeMAIN);
        ImageButton options = view.findViewById(R.id.optionsMainItem);

        String setID = Item.getSetID();
        String setName = Item.getSetName();
        int IgnoreChars = Item.getIgnoreChars();

        but.setTag(R.string.Tag_id_0, setID);
        but.setTag(R.string.Tag_id_1, setName);
        but.setTag(R.string.Tag_id_2, IgnoreChars);
        but.setOnClickListener(setClick);

        options.setTag(R.string.Tag_id_0, setID);
        options.setTag(R.string.Tag_id_1, setName);
        options.setOnClickListener(moreOptionsClick);

        TextView Name = view.findViewById(R.id.NameTextView);
        TextView Date = view.findViewById(R.id.DateTextView);

        Name.setText(Item.getSetName());
        Date.setText(Item.getCreateDate());
    }

    @Override
    public int getItemCount() {
        return repeatsList.size();
    }

    private View.OnClickListener setClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent addEditActivityIntent = new Intent(context, AddEditSetActivity.class);
            String TITLE = view.getTag(R.string.Tag_id_0).toString();
            String TABLE_NAME = view.getTag(R.string.Tag_id_1).toString();
            String IGNORE_CHARS = view.getTag(R.string.Tag_id_2).toString();
            addEditActivityIntent.putExtra("ISEDIT", TITLE);
            addEditActivityIntent.putExtra("NAME", TABLE_NAME);
            addEditActivityIntent.putExtra("IGNORE_CHARS", IGNORE_CHARS);
            context.startActivity(addEditActivityIntent);
        }
    };

    private View.OnClickListener moreOptionsClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String selectedSetID = view.getTag(R.string.Tag_id_0).toString();
            final String selectedSetName = view.getTag(R.string.Tag_id_1).toString();

            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.getMenuInflater().inflate(R.menu.set_options, popupMenu.getMenu());
            MenuPopupHelper menuPopupHelper = new MenuPopupHelper(context, (MenuBuilder) popupMenu.getMenu(), view);
            menuPopupHelper.setForceShowIcon(true);
            menuPopupHelper.show();

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int itemId = item.getItemId();

                    if (itemId == R.id.deleteSetOption) {
                        MaterialAlertDialogBuilder ALERTbuilder = new MaterialAlertDialogBuilder(context);
                        ALERTbuilder.setBackground(context.getDrawable(R.drawable.dialog_shape));

                        ALERTbuilder.setTitle(R.string.WantDelete);
                        ALERTbuilder.setNegativeButton(R.string.Cancel, null);

                        ALERTbuilder.setPositiveButton(R.string.Delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                new SetsConfigHelper(context).deleteSet(selectedSetID);

                                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.frameLayoutMain, new SetsFragment());
                                fragmentTransaction.commit();
                            }
                        });
                        ALERTbuilder.show();

                    } else if (itemId == R.id.shareSetOption) {
                        String name = selectedSetName;
                        name = name.trim();
                        if (name.equals("") || name.equals("text") || name.equals("Text") || name.equals("text text") || name.equals("text text text")) {
                            Toast.makeText(context, R.string.cantShareSet, Toast.LENGTH_LONG).show();
                        } else {
                            Intent intentShare = new Intent(context, ShareActivity.class);
                            intentShare.putExtra("name", name);
                            intentShare.putExtra("id", selectedSetID);
                            context.startActivity(intentShare);
                        }

                    } else if (itemId == R.id.manageSetSettingsOption) {
                        Intent intent = new Intent(context, SetSettingsActivity.class);
                        intent.putExtra("setID", selectedSetID);
                        context.startActivity(intent);
                    }
                    return true;
                }
            });
        }
    };
}
