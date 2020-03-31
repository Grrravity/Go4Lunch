package com.error.grrravity.go4lunch.utils.api;

import com.error.grrravity.go4lunch.models.autocomplete.Predictions;
import com.error.grrravity.go4lunch.models.details.Details;
import com.error.grrravity.go4lunch.models.places.Google;

import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIStreams {
    private static APIStreams sAPIStreams;
    private static APIService sAPIService;

    private APIStreams() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        sAPIService = retrofit.create(APIService.class);
    }

    public Observable<Google>  streamFetchGooglePlaces (String location, int radius, String type, String apiKey) {
        return sAPIService.getGoogleRestaurant(location, radius, type, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(500, TimeUnit.SECONDS);
    }

    public Observable<Details> streamFetchGoogleDetailsInfo (String placeId, String apiKey) {
        return sAPIService.getGoogleDetailsInfo(placeId, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(500, TimeUnit.SECONDS);
    }

    public Observable<Predictions> getPlacesAutoComplete (String query, String location, int radius, String apiKey){
        return sAPIService.getPlacesAutoComplete(query, location, radius, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(500, TimeUnit.SECONDS);
    }

    public static APIStreams getInstance(){
        if(sAPIStreams == null){
            synchronized (APIStreams.class){
                if(sAPIStreams == null)
                    sAPIStreams = new APIStreams();
            }
        }
        return sAPIStreams;
    }
}
