package com.error.grrravity.go4lunch.utils.api;

import com.error.grrravity.go4lunch.models.details.Details;
import com.error.grrravity.go4lunch.models.places.Google;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {

    @GET("maps/api/place/nearbysearch/json?")
    Observable<Google> getGoogleRestaurant(@Query("location") String location,
                                           @Query("radius") int radius,
                                           @Query("type") String type,
                                           @Query("key") String key);

    @GET("maps/api/place/details/json?")
    Observable<Details> getGoogleDetailsInfo (@Query("placeid") String placeId,
                                              @Query("key") String key);

}
