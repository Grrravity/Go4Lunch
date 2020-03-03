package com.error.grrravity.go4lunch.controllers.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.error.grrravity.go4lunch.BuildConfig;
import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.controllers.base.BaseFragment;
import com.error.grrravity.go4lunch.models.details.Details;
import com.error.grrravity.go4lunch.models.details.Result;
import com.error.grrravity.go4lunch.utils.api.APIStreams;
import com.error.grrravity.go4lunch.utils.helper.User;
import com.error.grrravity.go4lunch.utils.helper.UserHelper;
import com.error.grrravity.go4lunch.views.CoworkerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.observers.DisposableObserver;

public class RestaurantDetailFragment extends BaseFragment {

    //TODO passer ça en fragment
    private static final String ID = "ID";
    private static final String JOINING = "JOINING";
    private static final String UNJOIN = "UNJOIN";
    private static final String TEL = "tel:";
    private static final String PICTURE_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&maxheight=150&photoreference=";
    private static final String GET_RESTAURANT_ID = "restaurantId";
    private static final String APIKEY = BuildConfig.API_KEY;

    private static final String PREFS = "PREFS" ;


    @BindView(R.id.resdetail_respict) ImageView mResPicture;
    @BindView(R.id.resdetail_resadress) TextView mResAdress;
    @BindView(R.id.resdetail_resname) TextView mResName;
    @BindView(R.id.resdetail_resrecycler) RecyclerView mRecyclerView;
    @BindView(R.id.resdetail_resrate) RatingBar mRatingBar;
    @BindView(R.id.resdetail_reslike) Button mLikeBtn;
    @BindView(R.id.resdetail_rescall) Button mCallBtn;
    @BindView(R.id.resdetail_resweb) Button mWebBtn;
    @BindView(R.id.resdetail_join_FAB) FloatingActionButton mJoinFAB;

    private String placeID;
    private List<User> mUserList;
    private Result mResult;
    private CoworkerAdapter mCoworkerAdapter;
    private SharedPreferences prefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_detail_restaurant,
                container,
                false);
        ButterKnife.bind(this, view);

        Log.d("resdetail activity", "onCreate: " + placeID);

        prefs = Objects.requireNonNull(getContext()).getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        placeID = prefs.getString("id", "");

        this.configureRecyclerView();
        this.executeHttpRequestWithRetrofit();

        return view;
    }

    private void configureRecyclerView() {
        this.mUserList = new ArrayList<>();
        this.mCoworkerAdapter = new CoworkerAdapter(mUserList,2);
        this.mRecyclerView.setAdapter(this.mCoworkerAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public static RestaurantDetailFragment newInstance() {
        // Create new fragment

        return new RestaurantDetailFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    private void executeHttpRequestWithRetrofit() {
        Log.d("resdetail activity", "executeHttpRequestWithRetrofit: " + placeID);
        this.mDisposable = APIStreams.getInstance().streamFetchGoogleDetailsInfo(placeID, APIKEY).subscribeWith(new DisposableObserver<Details>(){
            @Override
            public void onNext(Details details) {
                mResult = details.getResult();

                UserHelper.getRestaurantInfo(mResult, userList ->{
                    RestaurantDetailFragment.this.mUserList = userList;
                    mCoworkerAdapter.refreshAdapter(mUserList);
                    Log.d("resdetail activity", "executeHttpRequestWithRetrofit: " + mUserList);
                });
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                updateUI();
            }
        });
    }

    private void updateUI() {

        Log.d("test debug", "updateUI: " + mResult);
        //restaurant name
        mResName.setText(mResult.getName());

        //restaurant vicinity
        String around = mResult.getTypes().get(0) + " - " + mResult.getVicinity();
        mResAdress.setText(around);

        //restaurant rating
        if(mResult.getRating() != 0){
            double googleRating = mResult.getRating();
            double rating = googleRating / 5 * 3;
            this.mRatingBar.setRating((float) rating);
            this.mRatingBar.setVisibility(View.VISIBLE);
        }
        else { mRatingBar.setVisibility(View.GONE);}

        //restaurant picture
        if(!(mResult.getPhotos() == null)){
            if(!(mResult.getPhotos().isEmpty())){
                Glide.with(this)
                        .load(PICTURE_URL + mResult.getPhotos().get(0).getPhotoReference() + "&key="+ APIKEY )
                        .into(mResPicture);
            }
        } else {
            Glide.with(this)
                    .load(R.drawable.goforfood_front_logo)
                    .apply(RequestOptions.centerCropTransform())
                    .into(mResPicture);
        }
        this.updateLike();
        this.updateJoin();
    }

    private void updateJoin() {
        UserHelper.getBookingRestaurant(UserHelper.getCurrentUser().getUid()).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String restaurantId = task.getResult().getString(GET_RESTAURANT_ID);
                if(restaurantId != null && restaurantId.equals(mResult.getPlaceId())){
                    mJoinFAB.setImageDrawable(getResources().getDrawable(R.drawable.check_circle_black));
                    mJoinFAB.setRippleColor(getResources().getColor(R.color.validColor));
                    mJoinFAB.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    mJoinFAB.setTag(UNJOIN);
                } else {
                    mJoinFAB.setImageDrawable(getResources().getDrawable(R.drawable.goforfood_front_logo));
                    mJoinFAB.setBackgroundColor(getResources().getColor(R.color.themeWhite));
                    mJoinFAB.setTag(JOINING);
                }
            }
        });
    }

    private void updateLike() {
        UserHelper.getLike(UserHelper.getCurrentUser().getUid()).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if(task.getResult().isEmpty()){
                    mLikeBtn.setText("Like");
                } else {
                    for (DocumentSnapshot restaurant : task.getResult()){
                        if(mResult.getPlaceId().equals(restaurant.getId())){
                            mLikeBtn.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.star_rate_yellow), null, null);
                            mLikeBtn.setText("Unlike");
                            break;
                        } else {
                            mLikeBtn.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.star_rate_black), null, null);
                            mLikeBtn.setText("Like");
                        }
                    }
                }
            }
        });
    }

    //OnClicks
    @OnClick(R.id.resdetail_join_FAB)
    void onClickFAB(View v){
        if(JOINING.equals(mJoinFAB.getTag())){
            this.joinRestaurant();
            setAlarm(true);
        } else {
            this.leaveRestaurant();
            setAlarm(false);
        }
    }

    private void joinRestaurant(){
        if (UserHelper.getCurrentUser()!=null) {
            UserHelper.updateUserAtRestaurant(UserHelper.getCurrentUser().getUid(),
                    mResult.getName(),
                    mResult.getPlaceId(),
                    mResult.getVicinity());
            mJoinFAB.setImageDrawable(getResources().getDrawable(R.drawable.check_circle_black));
            mJoinFAB.setRippleColor(getResources().getColor(R.color.validColor));
            mJoinFAB.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            Toast.makeText(getContext(), getResources().getString(R.string.joining), Toast.LENGTH_SHORT).show();
            mJoinFAB.setTag(UNJOIN);
        }
    }

    private void leaveRestaurant(){
        if (UserHelper.getCurrentUser()!=null) {
            UserHelper.deleteUserAtRestaurant(UserHelper.getCurrentUser().getUid());
            mJoinFAB.setImageDrawable(getResources().getDrawable(R.drawable.goforfood_front_logo));
            Toast.makeText(getContext(), getResources().getString(R.string.leaving), Toast.LENGTH_SHORT).show();
            mJoinFAB.setTag(JOINING);
        }
    }

    @OnClick(R.id.resdetail_reslike)
    void onClickLike(View v){
        if (mLikeBtn.getText().equals("Like")){
            this.likeRestaurant();
        } else {this.unlikeRestaurant();}
    }

    private void likeRestaurant(){
        if (UserHelper.getCurrentUser()!=null){
            UserHelper.createLike(mResult.getPlaceId(), UserHelper.getCurrentUser().getUid()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), getResources().getString(R.string.liked), Toast.LENGTH_SHORT).show();mLikeBtn.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.star_rate_yellow), null, null);
                    mLikeBtn.setText("Unlike");
                }
            });
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
        }

    }

    private void unlikeRestaurant(){
        if (UserHelper.getCurrentUser() != null){
            UserHelper.deleteLike(mResult.getPlaceId(),UserHelper.getCurrentUser().getUid());
            Toast.makeText(getContext(), getResources().getString(R.string.unliked), Toast.LENGTH_SHORT).show();
            mLikeBtn.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.star_rate_black), null, null);
            mLikeBtn.setText("Like");
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.resdetail_rescall)
    void callRestaurant(){
        Intent callIntent = new Intent(Intent.ACTION_CALL,
                Uri.parse(TEL + mResult.getFormattedPhoneNumber()));
        startActivity(callIntent);
        //if(ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
        //        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
        //    Intent callIntent = new Intent(Intent.ACTION_CALL,
        //            Uri.fromParts(TEL, mResult.getFormattedPhoneNumber(), null));
        //    startActivity(callIntent);
        //}
        //else {
        //    Toast.makeText(this, getResources().getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
        //}

    }

    @OnClick(R.id.resdetail_resweb)
    void goToWebSite(){
        if(mResult.getWebsite() != null){
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mResult.getWebsite()));
            startActivity(websiteIntent);
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
        }
    }

}
