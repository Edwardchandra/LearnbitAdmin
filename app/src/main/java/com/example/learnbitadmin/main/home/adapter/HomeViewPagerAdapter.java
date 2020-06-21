package com.example.learnbitadmin.main.home.adapter;

import com.example.learnbitadmin.main.home.tab.course.CourseFragment;
import com.example.learnbitadmin.main.home.tab.terminate.TerminateFragment;
import com.example.learnbitadmin.main.home.tab.withdraw.WithdrawFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    public HomeViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new CourseFragment();
            case 1:
                return new WithdrawFragment();
            case 2:
                return new TerminateFragment();
            default:
                return new CourseFragment();
        }
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Courses";
            case 1:
                return "Withdraws";
            case 2:
                return "Terminate Request";
            default:
                return "Courses";
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

}
