package com.error.grrravity.go4lunch.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.models.places.NearbyResult;

import java.util.List;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsViewHolder> {

    private final List<NearbyResult> mResultList;
    private final Context mContext;
    private final String location;

    public RestaurantsAdapter(Context context, List<NearbyResult> result, String location) {
        this.mContext = context;
        this.mResultList = result;
        this.location = location;
    }

    @NonNull
    @Override
    public RestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.fragment_restaurants_item, parent, false);
        return new RestaurantsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantsViewHolder listViewViewHolder, int position) {
        listViewViewHolder.updateWithGoogle(this.mResultList.get(position), location);
    }

    @Override
    public int getItemCount() {
        return this.mResultList.size();
    }


    //public void refreshAdapter(List<NearbyResult> nearbyResults) {
    //    mResultList = nearbyResults;
    //    notifyDataSetChanged();
    //}

    public List<NearbyResult> getResultList() {
        return mResultList;
    }
}

