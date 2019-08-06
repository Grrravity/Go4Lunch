package com.error.grrravity.go4lunch.controllers;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import android.widget.Toolbar;

import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.controllers.base.BaseActivity;
import com.error.grrravity.go4lunch.views.ScrollableViewPager;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_activity_viewpager) ScrollableViewPager mViewPager;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.main_activity_nav_view) NavigationView mNavigationView;

    // TODO BOTTOM BAR

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.configureToolbar();
        setTitle(getString(R.string.app_name));
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }

}
