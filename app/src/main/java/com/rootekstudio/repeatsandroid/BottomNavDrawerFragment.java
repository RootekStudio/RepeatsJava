package com.rootekstudio.repeatsandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomNavDrawerFragment extends BottomSheetDialogFragment {

    public static BottomNavDrawerFragment newInstance(){
        return new BottomNavDrawerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.main_nav_view, container, false);
    }

}
