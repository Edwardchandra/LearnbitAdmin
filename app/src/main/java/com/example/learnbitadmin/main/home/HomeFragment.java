package com.example.learnbitadmin.main.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.learnbitadmin.R;
import com.example.learnbitadmin.main.home.adapter.HomeViewPagerAdapter;
import com.example.learnbitadmin.main.home.tab.search.SearchActivity;
import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Button searchButton;

    private int pagePosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        searchButton = view.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(this);

        setupViewPager();

        return view;
    }

    private void setupViewPager(){
        HomeViewPagerAdapter viewPagerAdapter = new HomeViewPagerAdapter(getChildFragmentManager(), 0);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pagePosition = position;

                setupSearchView();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupSearchView(){
        if (pagePosition == 0){
            searchButton.setText("Search Courses");
        }else if (pagePosition == 1){
            searchButton.setText("Search Withdraws");
        }
    }

    private void searchItem(){
        if (pagePosition == 0){

        }else if (pagePosition == 1){

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.searchButton:
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(getContext(), "nothing happened", Toast.LENGTH_SHORT).show();
        }
    }
}
