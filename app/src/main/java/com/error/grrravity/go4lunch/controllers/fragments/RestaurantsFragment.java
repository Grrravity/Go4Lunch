package com.error.grrravity.go4lunch.controllers.fragments;

import android.content.Intent;
import android.os.Bundle;
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
import com.error.grrravity.go4lunch.models.autocomplete.Prediction;
import com.error.grrravity.go4lunch.models.details.ResultDetail;
import com.error.grrravity.go4lunch.models.places.Google;
import com.error.grrravity.go4lunch.models.places.NearbyResult;
import com.error.grrravity.go4lunch.utils.GPS;
import com.error.grrravity.go4lunch.utils.ItemClickHelper;
import com.error.grrravity.go4lunch.utils.api.APIStreams;
import com.error.grrravity.go4lunch.views.RestaurantsAdapter;

import java.io.Serializable;
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

    private List<NearbyResult> mNearbyResultList, mStoredResultList;
    private RestaurantsAdapter mRestaurantsAdapter;

    public static RestaurantsFragment newInstance(){
        return new RestaurantsFragment();
    }

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
        mDisposable = APIStreams.getInstance().streamFetchGooglePlaces(mPosition, 1000, RESTAURANT, apiKey).subscribeWith(new DisposableObserver<Google>() {
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
                //todo changer notifydatasetchanged (clear + addall)
            }
        });
    }

    public void resetList(){
        mNearbyResultList.clear();
        mNearbyResultList.addAll(mStoredResultList);
        mRestaurantsAdapter.notifyDataSetChanged();
    }
}
//TODO ajouter notifyDataSetChange pour reload fragment quand recherche
