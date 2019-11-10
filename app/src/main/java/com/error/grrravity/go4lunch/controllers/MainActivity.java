package com.error.grrravity.go4lunch.controllers;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.controllers.base.BaseActivity;
import com.error.grrravity.go4lunch.controllers.fragments.GoogleMapsFragment;
import com.error.grrravity.go4lunch.controllers.fragments.RestaurantsFragment;
import com.error.grrravity.go4lunch.controllers.fragments.CoworkerFragment;
import com.error.grrravity.go4lunch.utils.alarm_and_receiver.NetworkReceiver;
import com.error.grrravity.go4lunch.utils.auth.AuthenticationActivity;
import com.error.grrravity.go4lunch.utils.helper.NetworkChecker;
import com.error.grrravity.go4lunch.utils.helper.UserHelper;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.gms.tasks.OnSuccessListener;

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
    @BindView(R.id.activity_welcome_drawer_layout)
    DrawerLayout mDrawerLayout;

    private static final int SIGN_OUT_TASK = 10;
    public static final String ID = "ID";
    private static final String GET_RESTAURANTID = "restaurantId";

    private LatLngBounds latLngBounds;


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
                    showFragment(CoworkerFragment.newInstance());
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

    // Handle Navigation View Click
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.main_activity_drawer_lunch:
                UserHelper.getBookingRestaurant(UserHelper.getCurrentUser().getUid()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String restaurantId = task.getResult().getString(GET_RESTAURANTID);
                        if (restaurantId != null) {
                            Intent intent = new Intent(this, RestaurantDetailActivity.class);
                            intent.putExtra(ID, restaurantId);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Pas de restaurant réservé !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                break;
            case R.id.main_activity_drawer_settings:
               // Intent settingsIntent = new Intent(this, ProfileActivity.class);
               // startActivity(settingsIntent);
                break;
            case R.id.main_activity_drawer_logout:
                this.LogOut();
                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    //Request for signin out
    private void LogOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUI(SIGN_OUT_TASK));
    }

    private OnSuccessListener<Void> updateUI(final int origin){
        return aVoid -> {
            if (origin == SIGN_OUT_TASK) {
                Intent intent = new Intent(this, AuthenticationActivity.class);
                startActivity(intent);
                finish();
            }
        };
    }
}
