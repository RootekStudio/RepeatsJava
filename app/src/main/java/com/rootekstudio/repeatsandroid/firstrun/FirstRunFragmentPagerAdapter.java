package com.rootekstudio.repeatsandroid.firstrun;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FirstRunFragmentPagerAdapter extends FragmentStateAdapter {

    FirstRunFragmentPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new Fragment0();
        } else if (position == 1) {
            return new Fragment1();
        } else if (position == 2) {
            return new Fragment2();
        } else if (position == 3) {
            return new Fragment3();
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
