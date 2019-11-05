package com.error.grrravity.go4lunch.controllers;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.controllers.base.BaseActivity;
import com.error.grrravity.go4lunch.controllers.fragments.GoogleMapsFragment;
import com.error.grrravity.go4lunch.controllers.fragments.LikedRestaurantsFragment;
import com.error.grrravity.go4lunch.controllers.fragments.RestaurantsFragment;
import com.error.grrravity.go4lunch.controllers.fragments.WorkmateFragments;
import com.error.grrravity.go4lunch.utils.alarm_and_receiver.NetworkReceiver;
import com.error.grrravity.go4lunch.utils.helper.NetworkChecker;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;


//TODO verif condition (recup dans onresume). si activé petit moment de latence

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static NetworkReceiver mNetworkStateReceiver;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.activity_main_botnav)
    BottomNavigationView mBottomNavigationView;
    @BindView(R.id.main_activity_nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.btn_find_restaurant)
    Button btnFindRest;

    private LatLngBounds latLngBounds;

    int active;

    // TODO BOTTOM BAR

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolbar();
        this.configureNavBar();
        this.configureBottomNavBar();
        setTitle(getString(R.string.app_name));

        btnFindRest.setVisibility(View.GONE);

        showFragment(GoogleMapsFragment.newInstance());

        if (!NetworkChecker.isNetworkAvailable(this)) {

        } else {
            mNetworkStateReceiver = new NetworkReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        }
    }

    private void showFragment(Fragment newInstante) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, newInstante);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void configureNavBar() {
        mNavigationView.setNavigationItemSelectedListener(this);
    }


    private void configureBottomNavBar() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {

            int id = menuItem.getItemId();

            // Set current location in the ViewPager to handle the position of the fragments
            switch (id) {
                case R.id.action_list:
                    showFragment(RestaurantsFragment.newInstance());
                    break;
                case R.id.action_coworkers:
                    showFragment(WorkmateFragments.newInstance());
                    break;
                default:
                    showFragment(GoogleMapsFragment.newInstance());
                    break;
            }
            return true;
        });
    }

    @Override
    protected void onPause() {
        mNetworkStateReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mNetworkStateReceiver, filter);
        super.onPause();
    }

    // TODO recup coords

    public LatLngBounds getLatLngBounds() {
        return latLngBounds;
    }

    public void setLatLngBounds(LatLngBounds latLngBounds) {
        this.latLngBounds = latLngBounds;
    }


    protected void configureToolbar() {
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
