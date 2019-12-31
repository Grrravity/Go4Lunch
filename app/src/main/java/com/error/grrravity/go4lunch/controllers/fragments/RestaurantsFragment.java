package com.error.grrravity.go4lunch.controllers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.error.grrravity.go4lunch.BuildConfig;
import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.controllers.RestaurantDetailActivity;
import com.error.grrravity.go4lunch.controllers.base.BaseFragment;
import com.error.grrravity.go4lunch.models.places.Google;
import com.error.grrravity.go4lunch.models.places.NearbyResult;
import com.error.grrravity.go4lunch.utils.GPS;
import com.error.grrravity.go4lunch.utils.ItemClickHelper;
import com.error.grrravity.go4lunch.utils.api.APIStreams;
import com.error.grrravity.go4lunch.views.RestaurantsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.observers.DisposableObserver;

public class RestaurantsFragment extends BaseFragment {

    //TODO collection.sort pour sort les restaurants suivant la distance

    private static final String apiKey = BuildConfig.API_KEY;

    @BindView(R.id.restaurants_rv)
    RecyclerView mRecyclerView;
    private String mPosition;

    public List<NearbyResult> mNearbyResultList;
    public RestaurantsAdapter mRestaurantsAdapter;

    public static RestaurantsFragment newInstance(){
        return new RestaurantsFragment();
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
                    Intent restaurantDetailActivity = new Intent(getContext(),
                            RestaurantDetailActivity.class);
                    restaurantDetailActivity.putExtra(ID, placeID);
                    startActivity(restaurantDetailActivity);
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

        public void showRestaurants(){
        mRestaurantsAdapter.refreshAdapter(mNearbyResultList);
        }

        public void refreshRestaurants(List<NearbyResult> nearbyResultList){
        mRestaurantsAdapter.refreshAdapter(nearbyResultList);
        }

    private void executeHttpRequestWithRetrofit() {
        Log.d("mPosition fragments", "executeHttpRequestWithRetrofit: " + mPosition + " " + RESTAURANT );
        mDisposable = APIStreams.getInstance().streamFetchGooglePlaces(mPosition, 1000, RESTAURANT, apiKey).subscribeWith(new DisposableObserver<Google>() {
            @Override
            public void onNext(Google google) {
                mNearbyResultList.addAll(google.getResults());
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                mRestaurantsAdapter.notifyItemRangeChanged(0, mNearbyResultList.size());
            }
        });
    }
}
