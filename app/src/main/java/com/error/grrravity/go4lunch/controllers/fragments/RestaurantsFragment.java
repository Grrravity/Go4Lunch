package com.error.grrravity.go4lunch.controllers.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.error.grrravity.go4lunch.BuildConfig;
import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.controllers.base.BaseFragment;
import com.error.grrravity.go4lunch.models.autocomplete.Prediction;
import com.error.grrravity.go4lunch.models.details.ResultDetail;
import com.error.grrravity.go4lunch.models.places.Google;
import com.error.grrravity.go4lunch.models.places.Location;
import com.error.grrravity.go4lunch.models.places.NearbyResult;
import com.error.grrravity.go4lunch.utils.GPS;
import com.error.grrravity.go4lunch.utils.ItemClickHelper;
import com.error.grrravity.go4lunch.utils.api.APIStreams;
import com.error.grrravity.go4lunch.views.RestaurantsAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.observers.DisposableObserver;

public class RestaurantsFragment extends BaseFragment {

    private static final String apiKey = BuildConfig.API_KEY;
    private static final String PREFS = "PREFS" ;

    @BindView(R.id.restaurants_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.open_first_btn)
    Button mSortOpen;
    @BindView(R.id.best_rating_btn) Button mSortRating;
    @BindView(R.id.nearest_first_btn) Button mSortNearest;

    private String mPosition;
    private SharedPreferences prefs;

    private List<NearbyResult> mNearbyResultList, mStoredResultList;
    private RestaurantsAdapter mRestaurantsAdapter;

    private boolean ratingSorted = false;
    private boolean distanceSorted = false;
    private boolean openSorted = false;

    public static RestaurantsFragment newInstance(){
        return new RestaurantsFragment();
    }

    @SuppressWarnings({"unused"})
    public static RestaurantsFragment newInstance(List< ResultDetail > results){
            RestaurantsFragment fragment = new RestaurantsFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("result", (Serializable) results);
            fragment.setArguments(bundle);
            return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurants_view,
                                     container,
                          false);
        ButterKnife.bind(this, view);

        GPS gps = new GPS(getContext());
        mPosition = gps.getLatitude() + "," + gps.getLongitude();

        mStoredResultList = new ArrayList<>();

        prefs = Objects.requireNonNull(getContext()).getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        this.configureRecyclerView();
        this.configureClickOnRecyclerViewItem();
        this.executeHttpRequestWithRetrofit();

        //showRestaurants();
        setHasOptionsMenu(true);

        return view;
    }

    private void configureClickOnRecyclerViewItem() {
        ItemClickHelper.addTo(mRecyclerView)
                .setOnItemClickListener((mRecyclerView, position, v) -> {
                    String placeID = mRestaurantsAdapter.getResultList().get(position).getPlaceId();
                    Log.d("configureClickOnRecyc", placeID);
                    RestaurantDetailFragment detailFragment = new RestaurantDetailFragment();
                    Bundle args = new Bundle();
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("id", placeID);
                    editor.apply();
                    detailFragment.setArguments(args);
                    FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.activity_welcome_drawer_layout, detailFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                    fragmentTransaction.addToBackStack(null);
                });
    }


    private void configureRecyclerView(){
        this.mNearbyResultList = new ArrayList<>();
        this.mRestaurantsAdapter = new RestaurantsAdapter
                (getContext(),mNearbyResultList, mPosition);
        this.mRecyclerView.setAdapter(this.mRestaurantsAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                                                 DividerItemDecoration.VERTICAL));
        }

        public void updateRestaurantList(List<Prediction> newList) {
            ArrayList<NearbyResult> nearbyResults = new ArrayList<>();
            for (NearbyResult nearbyResult : mStoredResultList) {
                for (Prediction prediction: newList) {
                    if(prediction.getPlaceId().equals(nearbyResult.getPlaceId())){
                        nearbyResults.add(nearbyResult);
                        break;
                    }
                }
            }
            mNearbyResultList.clear();
            mNearbyResultList.addAll(nearbyResults);
            mRestaurantsAdapter.notifyDataSetChanged();
        }

    private void executeHttpRequestWithRetrofit() {
        mDisposable = APIStreams.getInstance().streamFetchGooglePlaces(mPosition, 10000, RESTAURANT, apiKey).subscribeWith(new DisposableObserver<Google>() {
            @Override
            public void onNext(Google google) {
                mNearbyResultList.addAll(google.getResults());
                mStoredResultList.addAll(google.getResults());
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                mRestaurantsAdapter.notifyItemRangeChanged(0, mNearbyResultList.size());

                //if (mNearbyResultList.size() > 0) {
                //    Collections.sort(mNearbyResultList, new Comparator<NearbyResult>() {
                //        @Override
                //        public int compare(final NearbyResult object1, final NearbyResult object2) {
                //            if (object1.getRating() > object2.getRating()){
                //                return 1;
                //            } else if (object1.getRating() < object2.getRating()) {
                //                return -1;
                //            } else {
                //                return 0;
                //            }
                //        }
                //    });
                //}
//
                //if (mNearbyResultList.size() > 0) {
                //    Collections.sort(mNearbyResultList, new Comparator<NearbyResult>() {
                //        @Override
                //        public int compare(final NearbyResult object1, final NearbyResult object2) {
                //           return object1.getGeometry().dis
                //        }
                //    });
                //}

                //field rating = mNearbyResultList.rating
                //field type = mNearbyResultList.openNow
                //field distance = mNearbyResultList.geometry.location
                //distanceTo location => location1
            }
        });
    }

    @OnClick(R.id.open_first_btn)
    void onClickSortOpen() {
        if(mNearbyResultList.size() > 0) {
            sortOpened(mNearbyResultList);
            if(!openSorted) {
                Collections.reverse(mNearbyResultList);
            }
            openSorted = !openSorted;
            mRestaurantsAdapter.notifyDataSetChanged();
        }

    }

    public static void sortOpened(List<NearbyResult> nearbyResultList) {
        Collections.sort(nearbyResultList, (nearbyResult, t1) -> {
            int b1 = nearbyResult.getOpeningHours() == null ? -1 : nearbyResult.getOpeningHours().getOpenNow() ? 1 : 0;
            int b2 = t1.getOpeningHours() == null ? -1 : t1.getOpeningHours().getOpenNow() ? 1:0;

            return Integer.compare(b1,b2);
        });
    }

    @OnClick(R.id.nearest_first_btn)
    void onClickSortNearest(){
        if(mNearbyResultList.size() > 0) {
            GPS gps = new GPS(getContext());
            Location origin = new Location();
            origin.setLat(gps.getLatitude());
            origin.setLng(gps.getLongitude());
            sortNearest(mNearbyResultList, origin);
            if(!distanceSorted) {
                Collections.reverse(mNearbyResultList);
            }
            distanceSorted = !distanceSorted;
            mRestaurantsAdapter.notifyDataSetChanged();
        }
    }

    static void sortNearest(List<NearbyResult> list, Location origin) {
        Collections.sort(list, (nearbyResult, t1) -> {
            Location b1 = nearbyResult.getGeometry().getLocation();
            Location b2 = t1.getGeometry().getLocation();

            return Double.compare(distance(b1.getLat(), origin.getLat(), b1.getLng(), origin.getLng()),
                    distance(b2.getLat(), origin.getLat(), b2.getLng(), origin.getLng()));
        });
    }

    @OnClick(R.id.best_rating_btn)
    void onClickSortRating(){
        if (mNearbyResultList.size() > 0) {
            sortRating(mNearbyResultList);
            if(!ratingSorted) {
                    Collections.reverse(mNearbyResultList);
                }
                ratingSorted = !ratingSorted;
                mRestaurantsAdapter.notifyDataSetChanged();
            }
    }

    public static void sortRating(List<NearbyResult> nearbyResultList) {
        Collections.sort(nearbyResultList, (object1, object2) -> Double.compare(object1.getRating(), object2.getRating()));
    }

    static double distance(double lat1, double lat2, double lon1,
                                   double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

    public void resetList(){
        mNearbyResultList.clear();
        mNearbyResultList.addAll(mStoredResultList);
        mRestaurantsAdapter.notifyDataSetChanged();
    }
}
