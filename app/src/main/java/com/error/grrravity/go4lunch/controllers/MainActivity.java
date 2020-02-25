package com.error.grrravity.go4lunch.controllers;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.error.grrravity.go4lunch.BuildConfig;
import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.controllers.base.BaseActivity;
import com.error.grrravity.go4lunch.controllers.fragments.CoworkerFragment;
import com.error.grrravity.go4lunch.controllers.fragments.GoogleMapsFragment;
import com.error.grrravity.go4lunch.controllers.fragments.RestaurantsFragment;
import com.error.grrravity.go4lunch.controllers.fragments.SettingsFragment;
import com.error.grrravity.go4lunch.models.autocomplete.Prediction;
import com.error.grrravity.go4lunch.models.autocomplete.Predictions;
import com.error.grrravity.go4lunch.utils.GPS;
import com.error.grrravity.go4lunch.utils.alarm_and_receiver.NetworkReceiver;
import com.error.grrravity.go4lunch.utils.api.APIStreams;
import com.error.grrravity.go4lunch.utils.auth.AuthenticationActivity;
import com.error.grrravity.go4lunch.utils.helper.NetworkChecker;
import com.error.grrravity.go4lunch.utils.helper.UserHelper;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


//TODO verif condition (recup dans onresume). si activé petit moment de latence

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static NetworkReceiver mNetworkStateReceiver;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.activity_main_botnav)
    BottomNavigationView mBottomNavigationView;
    @BindView(R.id.main_activity_nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.activity_welcome_drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.search_view) androidx.appcompat.widget.SearchView mSearchView;


    private static final int SIGN_OUT_TASK = 10;
    public static final String ID = "ID";
    private static final String GET_RESTAURANT_ID = "restaurantId";
    public static final String RESTAURANT = "restaurant";
    private static final String apiKey = BuildConfig.API_KEY;

    private LatLngBounds latLngBounds;
    private Menu menu;
    private String location;
    private Disposable mDisposable;
    public List<Prediction> mResultAutocomplete = null;
    MenuItem item;

    // TODO Actionbar avec recherche (penser à l'ouvrir ET la refermer)

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
        this.getGPS();
        this.handleIntent(getIntent());

        if (!NetworkChecker.isNetworkAvailable(this)) {

        } else {
            mNetworkStateReceiver = new NetworkReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        }
        ////Set alarm if restaurant booked
        //UserHelper.getBookingRestaurant(UserHelper.getCurrentUser().getUid()).addOnCompleteListener(task -> {
        //    if (task.isSuccessful()) {
        //        String restaurantId = task.getResult().getString(GET_RESTAURANT_ID);
        //        if (restaurantId != null) {
        //            this.setAlarm(true);
        //        } else {
        //            this.setAlarm(false);
        //        }
        //    }
        //});

        showFragment(GoogleMapsFragment.newInstance());
    }

    private void configureToolbar() {
        mToolbarTitle.setText(getResources().getString(R.string.toolbar_title));
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new Toolbar.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
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
                    if(item != null) {
                        item.setVisible(true);
                    }
                    showFragment(RestaurantsFragment.newInstance());
                    break;
                case R.id.action_coworkers:
                    if(item != null) {
                        item.setVisible(false);
                    }
                    showFragment(CoworkerFragment.newInstance());
                    break;
                default:
                    if(item != null) {
                        item.setVisible(true);
                    }
                    showFragment(GoogleMapsFragment.newInstance());
                    break;
            }
            return true;
        });
    }

    private void getGPS() {
        GPS gps = new GPS(this);
        if (gps.canLocalize()) {
            double latitude = gps.getLatitude(); // returns latitude
            double longitude = gps.getLongitude(); // returns longitude
            location = latitude + "," + longitude;
        }
    }

    private void showFragment(Fragment newInstante) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.activity_main_content_frame_layout, newInstante);
        fragmentTransaction.commitAllowingStateLoss();
    }


    // TODO recup coords
    public LatLngBounds getLatLngBounds() {
        return latLngBounds;
    }

    public void setLatLngBounds(LatLngBounds latLngBounds) {
        this.latLngBounds = latLngBounds;
    }


    // Handle Navigation View Click

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.main_activity_drawer_lunch:
                UserHelper.getBookingRestaurant(UserHelper.getCurrentUser().getUid()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String restaurantId = task.getResult().getString(GET_RESTAURANT_ID);
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
                showFragment(SettingsFragment.newInstance());
                //Intent settingsIntent = new Intent(this, SettingsFragment.class);
                //startActivity(settingsIntent);
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_top_menu, menu);
        item = menu.findItem(R.id.search_item);
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        mSearchView = new androidx.appcompat.widget.SearchView(Objects.requireNonNull(this.getSupportActionBar()).getThemedContext());
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(mSearchView);
        mSearchView.setQueryHint(getResources().getString(R.string.search_hint));
        mSearchView.setSearchableInfo(Objects.requireNonNull(searchManager).getSearchableInfo(this.getComponentName()));
        mSearchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    updateUI(true);
                }
                doSearch(newText);
                return false;
            }
        });
        mSearchView.setOnCloseListener(() -> {
            hideSoftKeyboard(MainActivity.this);
            return true;
        });
        return true;
    }

    private void doSearch(String query) {
        if (query.length() > 2){
            mDisposable = APIStreams.getInstance().getPlacesAutoComplete(query, location, 1000, apiKey).subscribeWith(new DisposableObserver<Predictions>() {
                @Override
                public void onNext(Predictions predictions) {
                    mResultAutocomplete = predictions.getPredictions();
                }

                @Override
                public void onError(Throwable e) {
                    e.getMessage();
                }

                @Override
                public void onComplete() {
                    updateUI(false);
                }
            });
        }
    }

    // Update view with results of Place Autocomplete

    private void updateUI(boolean isReset) {
            FragmentManager fm = getSupportFragmentManager();
            List<Fragment> fragments = fm.getFragments();
            Fragment newFragment = fragments.get(fragments.size() - 1);
            if (newFragment instanceof GoogleMapsFragment) {
                if (isReset){
                    ((GoogleMapsFragment) newFragment).resetList();
                } else {
                    ((GoogleMapsFragment) newFragment).updateRestaurantList(mResultAutocomplete);
                }
            }
            if (newFragment instanceof RestaurantsFragment) {
                if (isReset){
                    ((RestaurantsFragment) newFragment).resetList();
                } else {
                    ((RestaurantsFragment) newFragment).updateRestaurantList(mResultAutocomplete);
                }
            }
    }

    @Override
    protected void onPause() {
        mNetworkStateReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mNetworkStateReceiver, filter);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mSearchView.setQuery(query, false);
            doSearch(query);
        }
    }

    // Hide keyboard
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(
                Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
    }

}
