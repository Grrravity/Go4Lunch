package com.error.grrravity.go4lunch.controllers.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.error.grrravity.go4lunch.models.places.NearbyResult;
import com.error.grrravity.go4lunch.utils.GPS;
import com.error.grrravity.go4lunch.utils.ItemClickHelper;
import com.error.grrravity.go4lunch.utils.api.APIStreams;
import com.error.grrravity.go4lunch.views.RestaurantsAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.observers.DisposableObserver;

public class RestaurantsFragment extends BaseFragment {

    private static final String apiKey = BuildConfig.API_KEY;
    private static final String PREFS = "PREFS" ;

    @BindView(R.id.restaurants_rv)
    RecyclerView mRecyclerView;
    private String mPosition;
    private SharedPreferences prefs;

    private List<NearbyResult> mNearbyResultList, mStoredResultList;
    private RestaurantsAdapter mRestaurantsAdapter;

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
            }
        });
    }

    public void resetList(){
        mNearbyResultList.clear();
        mNearbyResultList.addAll(mStoredResultList);
        mRestaurantsAdapter.notifyDataSetChanged();
    }
}
