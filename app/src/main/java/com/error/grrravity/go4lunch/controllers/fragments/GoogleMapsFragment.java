package com.error.grrravity.go4lunch.controllers.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.error.grrravity.go4lunch.BuildConfig;
import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.controllers.MainActivity;
import com.error.grrravity.go4lunch.controllers.RestaurantDetailActivity;
import com.error.grrravity.go4lunch.controllers.base.BaseFragment;
import com.error.grrravity.go4lunch.models.autocomplete.Prediction;
import com.error.grrravity.go4lunch.models.details.ResultDetail;
import com.error.grrravity.go4lunch.models.places.Google;
import com.error.grrravity.go4lunch.models.places.NearbyResult;
import com.error.grrravity.go4lunch.utils.GPS;
import com.error.grrravity.go4lunch.utils.api.APIStreams;
import com.error.grrravity.go4lunch.utils.helper.UserHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import io.reactivex.observers.DisposableObserver;

public class GoogleMapsFragment extends BaseFragment implements
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener,
        Filterable, OnMapReadyCallback {

    private static final String TAG = "MapsFragment";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String apiKey = BuildConfig.API_KEY;

    //vars
    private boolean mLocationPermissionsGranted = false;

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;

    private List<NearbyResult> mNearbyResultList, mStoredResultList;

    private GPS mGPS;
    private MarkerOptions mMarkerOptions;
    private int height = 90;
    private int width = 90;

    private LatLng mMyLatLng;
    private Location mMyLocation;
    private String mPosition;

    private String restaurantIDForMarker, restaurantName;

    private boolean firstLC = true;

    // TODO : créer un marqueurs + verrif crash eventuel
    //TODO : fragment + recicler view pour requete google places (API)

    public static GoogleMapsFragment newInstance(){
        return new GoogleMapsFragment();
    }

    public static GoogleMapsFragment newInstance(List<ResultDetail> results) {
        GoogleMapsFragment fragment = new GoogleMapsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("result", (Serializable) results);
        fragment.setArguments(bundle);
        return fragment; }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocationPermission();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_googlemaps, container, false);

        ButterKnife.bind(this, view);

        mMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);

        mGPS = new GPS(getContext());
        mNearbyResultList = new ArrayList<>();
        mStoredResultList = new ArrayList<>();
        mMarkerOptions = new MarkerOptions();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
            updateUI(mNearbyResultList);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapFragment.getMapAsync(this);
        mMapFragment.getMapAsync(googleMap -> {
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    //TODO lui passer le placeID
                    if (restaurantIDForMarker != null){
                        Intent restaurantDetailActivity = new Intent(getContext(),
                                RestaurantDetailActivity.class);
                    restaurantDetailActivity.putExtra(ID, restaurantIDForMarker);
                    startActivity(restaurantDetailActivity);
                    }
                }
            });
        });
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
            } else {
                ActivityCompat.requestPermissions(this.getActivity(), permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this.getActivity(), permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                }
            }
        }
    }

    @Override
    public Filter getFilter() {
        return null;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        restaurantName = marker.getTitle();
        int size = mStoredResultList.size()-1;
        int index = -3;
        for (int i=0; i<size; i++){
            if (mStoredResultList.get(i).getName().equals(restaurantName)){
                index = i;
                break;
            }
        }
        if (index != -3) {
            restaurantIDForMarker = mStoredResultList.get(index).getPlaceId();
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        mMap.setOnMarkerClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        final FusedLocationProviderClient mFusedLocationClient = LocationServices
                .getFusedLocationProviderClient(Objects.requireNonNull(getContext()));

        if (mLocationPermissionsGranted){
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            mMap.setOnCameraIdleListener(this);
        }

        mMyLatLng = new LatLng(mGPS.getLatitude(), mGPS.getLongitude());
        mPosition = mGPS.getLatitude() + "," + mGPS.getLongitude();

        mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

                    //CameraPosition cameraPosition = new CameraPosition.Builder().target(mMyLatLng).zoom(12).build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMyLatLng, 15));
                });

        View myLocationButton = ((View) mMapFragment.getView()
                .findViewById(Integer.parseInt("1")).getParent())
                .findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams)
                myLocationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 170, 190);


            this.executeHttpRequestWithRetrofit();

    }

    public void updateUI(List<NearbyResult> nearbyResultList){
        if(mMap != null) {
            mMap.clear();
        }
        // display all restaurants
        for(NearbyResult mResult : nearbyResultList){
            LatLng restaurant = new LatLng(mResult.getGeometry().getLocation().getLat(),
                                           mResult.getGeometry().getLocation().getLng());

            UserHelper.getRestaurant(mResult.getPlaceId()).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    mMarkerOptions.position(restaurant);
                    mMarkerOptions.title(mResult.getName());
                    mMarkerOptions.snippet(mResult.getVicinity());
                    if (getContext() != null){
                        if(task.getResult().isEmpty()){
                            BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.restaurant_pin_orange);
                            Bitmap bitmap = bitmapDrawable.getBitmap();
                            Bitmap iconSize = Bitmap.createScaledBitmap(bitmap, width, height, false);
                            mMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconSize));
                        } else {
                            BitmapDrawable bitmapDrawable1 = (BitmapDrawable) getResources().getDrawable(R.drawable.restaurant_pin_green);
                            Bitmap bitmap1 = bitmapDrawable1.getBitmap();
                            Bitmap iconSize1 = Bitmap.createScaledBitmap(bitmap1, width, height, false);
                            mMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconSize1));
                        }
                        Marker marker = mMap.addMarker(mMarkerOptions);
                        marker.setTag(mResult);
                    }
                }
            });
        }
    }



    // RETROFIT

    private void executeHttpRequestWithRetrofit() {
            mDisposable = APIStreams.getInstance().streamFetchGooglePlaces(mPosition, 1000, RESTAURANT, apiKey).subscribeWith(new DisposableObserver<Google>() {
        @Override
        public void onNext(Google google) {
            mNearbyResultList.addAll(google.getResults());
            mStoredResultList.addAll(google.getResults());
            updateUI(mNearbyResultList);
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onComplete() {
        }
        });
    }

    @Override
    public boolean onMyLocationButtonClick() {

        final FusedLocationProviderClient mFusedLocationClient = LocationServices
                .getFusedLocationProviderClient(Objects.requireNonNull(getContext()));

        mMyLatLng = new LatLng(mGPS.getLatitude(), mGPS.getLongitude());
        mPosition = mGPS.getLatitude() + "," + mGPS.getLongitude();

        mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

            //CameraPosition cameraPosition = new CameraPosition.Builder().target(mMyLatLng).zoom(12).build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMyLatLng, 15));
        });

        this.executeHttpRequestWithRetrofit();

        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onCameraIdle() {
        ((MainActivity) getActivity())
                .setLatLngBounds(mMap.getProjection().getVisibleRegion().latLngBounds);
    }

    public void updateRestaurantList (List<Prediction> newList) {
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
        updateUI(mNearbyResultList);
    }

    public void resetList(){
        mNearbyResultList.clear();
        mNearbyResultList.addAll(mStoredResultList);
        updateUI(mNearbyResultList);
    }

}
