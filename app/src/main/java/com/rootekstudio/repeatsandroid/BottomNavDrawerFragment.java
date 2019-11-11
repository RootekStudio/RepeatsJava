package com.rootekstudio.repeatsandroid;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.rootekstudio.repeatsandroid.activities.MainActivity;
import com.rootekstudio.repeatsandroid.activities.ProfileInfoActivity;
import com.rootekstudio.repeatsandroid.activities.SettingsActivity;
import com.rootekstudio.repeatsandroid.activities.SignInActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BottomNavDrawerFragment extends BottomSheetDialogFragment {

    public static BottomNavDrawerFragment newInstance() {
        return new BottomNavDrawerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_nav_view, container, false);
    }

    @Override
    public void onViewCreated(@NotNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavigationView nav = view.findViewById(R.id.navigation);
        View header = nav.getHeaderView(0);

        TextView nick = header.findViewById(R.id.userNick);
        TextView userMail = header.findViewById(R.id.userEmail);

        String getNick = MainActivity.userNick;
        String getEmail = MainActivity.userEmail;

        if(getEmail != null){
            String welcome = getString(R.string.welcomeUser) + " " + getNick + "!";
            nick.setText(welcome);
            userMail.setText(getEmail);
            nav.getMenu().getItem(0).setVisible(false);
        }
        else {
            nick.setText(getString(R.string.unregistered));
            userMail.setText(getString(R.string.please_signin_nav));
            nav.getMenu().getItem(1).setVisible(false);
            ImageView profileImage = nav.getHeaderView(0).findViewById(R.id.profileImage);

            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
            profileImage.setColorFilter(cf);
            profileImage.setImageAlpha(128);
        }

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.editProfile) {
                    Intent intent = new Intent(getContext(), ProfileInfoActivity.class);
                    startActivity(intent);
                }
                else if(menuItem.getItemId() == R.id.settings) {
                    Intent intent = new Intent(getContext(), SettingsActivity.class);
                    startActivity(intent);
                }
                else if(menuItem.getItemId()==R.id.loginMenuItem) {
                    Intent intent = new Intent(getContext(), SignInActivity.class);
                    startActivity(intent);

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
