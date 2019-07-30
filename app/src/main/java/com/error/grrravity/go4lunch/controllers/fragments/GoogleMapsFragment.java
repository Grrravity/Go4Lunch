package com.error.grrravity.go4lunch.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Filter;
import android.widget.Filterable;

import com.error.grrravity.go4lunch.controllers.base.BaseFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class GoogleMapsFragment extends BaseFragment implements GoogleMap.OnMarkerClickListener,
        Filterable {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Maps");
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
