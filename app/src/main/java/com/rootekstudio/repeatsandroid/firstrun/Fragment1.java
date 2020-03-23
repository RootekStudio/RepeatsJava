package com.rootekstudio.repeatsandroid.firstrun;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.rootekstudio.repeatsandroid.R;
import com.rootekstudio.repeatsandroid.notifications.RepeatsNotificationTemplate;

public class Fragment1 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.firstrun1, container, false);
        Button button = view.findViewById(R.id.sendTestNotifiButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RepeatsNotificationTemplate.NotifiTemplate(getContext(), false, null);
            }
        });
        return view;
    }
}
