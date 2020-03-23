package com.rootekstudio.repeatsandroid.firstrun;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.rootekstudio.repeatsandroid.R;

public class Fragment0 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.firstrun0, container, false);
        TextView terms = view.findViewById(R.id.termsOfUseView);
        terms.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }
}
