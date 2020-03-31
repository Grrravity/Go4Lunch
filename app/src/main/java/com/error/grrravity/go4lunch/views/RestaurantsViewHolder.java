package com.error.grrravity.go4lunch.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.error.grrravity.go4lunch.BuildConfig;
import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.models.places.Location;
import com.error.grrravity.go4lunch.models.places.NearbyResult;
import com.error.grrravity.go4lunch.utils.helper.UserHelper;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_TV_name)
    TextView restaurantName;
    @BindView(R.id.item_TV_address)
    TextView restaurantAdress;
    @BindView(R.id.item_TV_opening)
    TextView restaurantOpenClose;
    @BindView(R.id.item_TV_distance)
    TextView restaurantDistance;
    @BindView(R.id.item_IV_main_pic)
    ImageView restaurantPicture;
    @BindView(R.id.item_ratingBar)
    RatingBar restaurantRatingBar;
    @BindView(R.id.item_IV_mates)
    ImageView imageViewMates;
    @BindView(R.id.item_TV_mates)
    TextView textViewMates;

    private static final String APIKEY2 = BuildConfig.API_KEY2;

    private final float[] distanceResults = new float[3];

    public RestaurantsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithGoogle(NearbyResult result, String userLocation) {

        // ------------- ID ------------
        this.restaurantName.setText(result.getName());
        // ------------- ADDRESS ----------
        this.restaurantAdress.setText(result.getVicinity());
        // ------------ RATING BAR ----------
        displayRating(result);
        // ----------- DISTANCE -----------
        displayDistance(userLocation, result.getGeometry().getLocation());
        int dist = Math.round(distanceResults[0]);
        String distanceString;
        String distance;
        if(dist>1000){
            DecimalFormat dec = new DecimalFormat("#0.00");
            distanceString = dec.format((double) dist/1000);
            distance = itemView.getResources().getString(R.string.list_unit_distance_KM, distanceString);
        } else {
            distanceString = Integer.toString(dist);
            distance = itemView.getResources().getString(R.string.list_unit_distance_M, distanceString);
        }
        this.restaurantDistance.setText(distance);
        // ---------- Opening -------------
        if (result.getOpeningHours() != null) {
            if (result.getOpeningHours().getOpenNow()) {
                restaurantOpenClose.setText(R.string.open);
                restaurantOpenClose.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.validColor));
            } else {
                restaurantOpenClose.setText(R.string.close);
                restaurantOpenClose.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorAccent));
            }
        } else {
            restaurantOpenClose.setText(itemView.getContext().getString(R.string.time_unavailable));
            restaurantOpenClose.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.primaryTextColor));
        }

        // -------------- PICTURE ------------------
        if (!(result.getPhotos() == null)) {
            if (!(result.getPhotos().isEmpty())) {
                Glide.with(itemView)
                        .load("https://maps.googleapis.com/maps/api/place/photo"
                                + "?maxwidth=" + 75 + "&maxheight=" + 75 + "&photoreference="
                                + result.getPhotos().get(0).getPhotoReference() + "&key="+ APIKEY2 )
                        .into(restaurantPicture);
            }
        } else {
            Glide.with(itemView)
                    .load(R.drawable.broken_picture)
                    .apply(RequestOptions.centerCropTransform())
                    .into(restaurantPicture);
        }

        // ----------- MATES -----------
        UserHelper.getRestaurantForList(result.getPlaceId()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (Objects.requireNonNull(task.getResult()).size() > 0) {
                    List<String> resultList = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        String uid = document.getString("uid");
                        if (!Objects.requireNonNull(uid).equals(Objects.requireNonNull(UserHelper.getCurrentUser()).getUid()))
                            resultList.add(uid);
                    }
                    if (resultList.size() > 0) {
                        textViewMates.setText(itemView.getResources().getString(R.string.restaurant_mates_number, task.getResult().size()));
                        imageViewMates.setImageResource(R.drawable.profile_icon);
                        imageViewMates.setVisibility(View.VISIBLE);
                    } else {
                        hideWorkers();
                    }
                } else {
                    hideWorkers();
                }
            }
        });
    }

    private void hideWorkers() {
        textViewMates.setText("");
        imageViewMates.setVisibility(View.GONE);
    }

    private void displayRating(NearbyResult result) {
        if (result.getRating() != 0) {
            double googleRating = result.getRating();
            double rating = googleRating / 5 * 3;
            this.restaurantRatingBar.setRating((float) rating);
            this.restaurantRatingBar.setVisibility(View.VISIBLE);
        } else {
            this.restaurantRatingBar.setVisibility(View.GONE);
        }
    }

    private void displayDistance(String startLocation, Location endLocation) {
        String[] separatedStart = startLocation.split(",");
        double startLatitude = Double.parseDouble(separatedStart[0]);
        double startLongitude = Double.parseDouble(separatedStart[1]);
        double endLatitude = endLocation.getLat();
        double endLongitude = endLocation.getLng();
        android.location.Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, distanceResults);
    }
}
