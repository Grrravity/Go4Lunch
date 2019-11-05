package com.error.grrravity.go4lunch.controllers.fragments;

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

import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.controllers.base.BaseFragment;
import com.error.grrravity.go4lunch.models.places.Google;
import com.error.grrravity.go4lunch.models.places.NearbyResult;
import com.error.grrravity.go4lunch.utils.GPS;
import com.error.grrravity.go4lunch.utils.api.APIStreams;
import com.error.grrravity.go4lunch.views.RestaurantsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.observers.DisposableObserver;

public class RestaurantsFragment extends BaseFragment {

    private static final String apiKey = "AIzaSyDVaDt05euLhIr1cO1A88iUb4lj8vdA5J0";

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

            this.executeHttpRequestWithRetrofit();

        showRestaurants();
        setHasOptionsMenu(true);

        return view;
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
