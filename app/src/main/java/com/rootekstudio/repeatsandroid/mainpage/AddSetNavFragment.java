package com.rootekstudio.repeatsandroid.mainpage;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.RequestCodes;
import com.rootekstudio.repeatsandroid.activities.AddEditSetActivity;
import com.rootekstudio.repeatsandroid.textrecognition.TextRecognitionActivity;
import com.rootekstudio.repeatsandroid.community.RepeatsCommunityStartActivity;

import org.jetbrains.annotations.NotNull;

public class AddSetNavFragment extends BottomSheetDialogFragment {
    public static AddSetNavFragment newInstance() {
        return new AddSetNavFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_set_view, container, false);
    }

    @Override
    public void onViewCreated(@NotNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavigationView nav = view.findViewById(R.id.addSetNavigationView);

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.createNewSet) {
                    Intent addEditActivityIntent = new Intent(getContext(), AddEditSetActivity.class);
                    addEditActivityIntent.putExtra("ISEDIT", "FALSE");
                    addEditActivityIntent.putExtra("IGNORE_CHARS", "false");
                    startActivity(addEditActivityIntent);
                }
                else if(item.getItemId() == R.id.getSetFromCamera) {

                }
                else if(item.getItemId() == R.id.getSetFromGallery) {
                    Intent intent = new Intent(getActivity(), TextRecognitionActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.getSetFromZip) {
                    Intent zipPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    zipPickerIntent.setType("application/*");
                    try {
                        getActivity().startActivityForResult(zipPickerIntent, RequestCodes.READ_SHARED);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(), R.string.explorerNotFound, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (item.getItemId() == R.id.getSetFromCommunity) {
                    Intent intentRC = new Intent(getContext(), RepeatsCommunityStartActivity.class);
                    startActivity(intentRC);
                }
                return true;
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        dismiss();
    }

}
