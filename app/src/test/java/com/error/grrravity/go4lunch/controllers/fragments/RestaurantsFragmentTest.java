package com.error.grrravity.go4lunch.controllers.fragments;
import android.content.Context;

import com.error.grrravity.go4lunch.models.places.NearbyResult;
import com.error.grrravity.go4lunch.views.RestaurantsAdapter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class RestaurantsFragmentTest {
    private final List<NearbyResult> mList = new ArrayList<>();
    //private String mLocation;
    private final Context context = mock(Context.class);

    private final RestaurantsAdapter mRestaurantsAdapter =
            new RestaurantsAdapter(context, mList, null);

    @Test
    public void countFragmentReturn(){
        assertEquals(0, mRestaurantsAdapter.getItemCount());
    }
}