package com.error.grrravity.go4lunch.controllers.fragments;
import android.content.Context;

import com.error.grrravity.go4lunch.models.places.Location;
import com.error.grrravity.go4lunch.models.places.Geometry;
import com.error.grrravity.go4lunch.models.places.NearbyResult;
import com.error.grrravity.go4lunch.models.places.OpeningHours;
import com.error.grrravity.go4lunch.views.RestaurantsAdapter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
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

    @Test
    public void distanceCheck(){
        Location myPlace = new Location();
        myPlace.setLat(44.453249);
        myPlace.setLng(4.713797);

        Location restaurantPlace = new Location();
        restaurantPlace.setLat(44.44825434147048);
        restaurantPlace.setLng(4.724234819477715);

        assertEquals(1000, RestaurantsFragment.distance(myPlace.getLat(),restaurantPlace.getLat(), myPlace.getLng(), restaurantPlace.getLng()),50);
    }

    private NearbyResult getRestaurant1(){
        Location restaurant1km = new Location();
        restaurant1km.setLat(44.44825434147048);
        restaurant1km.setLng(4.724234819477715);
        Geometry rest1kmGeometry = new Geometry();
        rest1kmGeometry.setLocation(restaurant1km);
        OpeningHours hours1km = new OpeningHours();
        hours1km.setOpenNow(true);
        hours1km.setWeekdayText(null);
        NearbyResult rst1Km = new NearbyResult();
        rst1Km.setGeometry(rest1kmGeometry);
        rst1Km.setRating(5);
        rst1Km.setOpeningHours(hours1km);
        rst1Km.setId("1");

        return rst1Km;
    }

    private NearbyResult getRestaurant2(){
        Location restaurant2km = new Location();
        restaurant2km.setLat(44.43304438224648);
        restaurant2km.setLng(4.722747802734375);
        Geometry rest2kmGeometry = new Geometry();
        rest2kmGeometry.setLocation(restaurant2km);
        OpeningHours hours2km = new OpeningHours();
        hours2km.setOpenNow(false);
        hours2km.setWeekdayText(null);
        NearbyResult rst2Km = new NearbyResult();
        rst2Km.setGeometry(rest2kmGeometry);
        rst2Km.setRating(3);
        rst2Km.setOpeningHours(hours2km);
        rst2Km.setId("2");

        return rst2Km;
    }

    private NearbyResult getRestaurant3(){
        Location restaurant4km = new Location();
        restaurant4km.setLat(44.43813115095816);
        restaurant4km.setLng(4.667816162109375);
        Geometry rest4kmGeometry = new Geometry();
        rest4kmGeometry.setLocation(restaurant4km);
        NearbyResult rst4Km = new NearbyResult();
        rst4Km.setGeometry(rest4kmGeometry);
        rst4Km.setRating(0);
        rst4Km.setOpeningHours(null);
        rst4Km.setId("3");

        return rst4Km;
    }

    @Test
    public void orderByDistance(){
        Location myPlace = new Location();
        myPlace.setLat(44.453249);
        myPlace.setLng(4.713797);

       List<NearbyResult> list = new ArrayList<>();
       list.add(getRestaurant3());
       list.add(getRestaurant1());
       list.add(getRestaurant2());

       RestaurantsFragment.sortNearest(list, myPlace);

       assertEquals("1",list.get(0).getId());
       assertEquals("2",list.get(1).getId());
       assertEquals("3",list.get(2).getId());
    }

    @Test
    public void orderByRate() {
        List<NearbyResult> list = new ArrayList<>();
        list.add(getRestaurant3());
        list.add(getRestaurant1());
        list.add(getRestaurant2());

        RestaurantsFragment.sortRating(list);
        Collections.reverse(list);

        assertEquals("1",list.get(0).getId());
        assertEquals("2",list.get(1).getId());
        assertEquals("3",list.get(2).getId());
    }

    @Test
    public void orderByOpen(){
        List<NearbyResult> list = new ArrayList<>();
        list.add(getRestaurant3());
        list.add(getRestaurant1());
        list.add(getRestaurant2());

        RestaurantsFragment.sortOpened(list);
        Collections.reverse(list);

        assertEquals("1",list.get(0).getId());
        assertEquals("2",list.get(1).getId());
        assertEquals("3",list.get(2).getId());
    }
}