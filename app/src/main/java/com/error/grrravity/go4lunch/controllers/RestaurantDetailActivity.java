package com.error.grrravity.go4lunch.controllers;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.error.grrravity.go4lunch.BuildConfig;
import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.controllers.base.BaseActivity;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RestaurantDetailActivity extends BaseActivity {

    private static final String ID = "ID";
    private static final String NAME = "NAME";
    private static final String JOINING = "JOINING";
    private static final String UNJOIN = "UNJOIN";
    private static final String TEL = "tel:";
    private static final String PICTURE_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&maxheight=150&photoreference=";
    private static final String RESTAURANT_ID = "RESID";
    private static final String GET_RESTAURANT_ID = "restaurantId";
    private static final String APIKEY = BuildConfig.API_KEY;

    @BindView(R.id.resdetail_respict) ImageView mResPicture;
    @BindView(R.id.resdetail_resadress) TextView mResAdress;
    @BindView(R.id.resdetail_resname) TextView mResName;
    @BindView(R.id.resdetail_resrecycler) RecyclerView mRecyclerView;
    @BindView(R.id.resdetail_resrate) RatingBar mRatingBar;
    @BindView(R.id.resdetail_reslike) Button mLikeBtn;
    @BindView(R.id.resdetail_rescall) Button mCallBtn;
    @BindView(R.id.resdetail_resweb) Button mWebBtn;
    @BindView(R.id.resdetail_join_FAB) FloatingActionButton mJoinFAB;

    private Disposable mDisposable;
    private String placeID;
    private List<User> mUserList;
    private Result mResult;
    private CoworkerAdapter mCoworkerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        placeID = getIntent().getStringExtra(ID);

        Log.d("resdetail activity", "onCreate: " + placeID);

        this.configureRecyclerView();
        this.executeHttpRequestWithRetrofit();

    }

    private void configureRecyclerView() {
        this.mUserList = new ArrayList<>();
        this.mCoworkerAdapter = new CoworkerAdapter(mUserList,2);
        this.mRecyclerView.setAdapter(this.mCoworkerAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onDestroy() {
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
                    RestaurantDetailActivity.this.mUserList = userList;
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
                    mJoinFAB.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    mJoinFAB.setTag(UNJOIN);
                } else {
                    mJoinFAB.setImageDrawable(getResources().getDrawable(R.drawable.goforfood_front_logo));
                    mJoinFAB.setBackgroundColor(getResources().getColor(R.color.white));
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
    public void onClickFAB(View v){
        if(JOINING.equals(mJoinFAB.getTag())){
            this.joinRestaurant();
        } else {this.leaveRestaurant();}
    }

    public void joinRestaurant(){
        if (UserHelper.getCurrentUser()!=null) {
            UserHelper.updateUserAtRestaurant(UserHelper.getCurrentUser().getUid(),
                    mResult.getName(),
                    mResult.getPlaceId(),
                    mResult.getVicinity());
            mJoinFAB.setImageDrawable(getResources().getDrawable(R.drawable.check_circle_black));
            mJoinFAB.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            Toast.makeText(this, getResources().getString(R.string.joining), Toast.LENGTH_SHORT).show();
            mJoinFAB.setTag(UNJOIN);
        }
    }

    public void leaveRestaurant(){
        if (UserHelper.getCurrentUser()!=null) {
            UserHelper.deleteUserAtRestaurant(UserHelper.getCurrentUser().getUid());
            mJoinFAB.setImageDrawable(getResources().getDrawable(R.drawable.goforfood_front_logo));
            Toast.makeText(this, getResources().getString(R.string.leaving), Toast.LENGTH_SHORT).show();
            mJoinFAB.setTag(JOINING);
        }
    }

    @OnClick(R.id.resdetail_reslike)
    public void onClickLike(View v){
        if (mLikeBtn.getText().equals("Like")){
            this.likeRestaurant();
        } else {this.unlikeRestaurant();}
    }

    public void likeRestaurant(){
        if (UserHelper.getCurrentUser()!=null){
            UserHelper.createLike(mResult.getPlaceId(), UserHelper.getCurrentUser().getUid()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, getResources().getString(R.string.liked), Toast.LENGTH_SHORT).show();mLikeBtn.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.star_rate_yellow), null, null);
                    mLikeBtn.setText("Unlike");
                }
            });
        } else {
            Toast.makeText(this, getResources().getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
        }

    }

    public void unlikeRestaurant(){
        if (UserHelper.getCurrentUser() != null){
            UserHelper.deleteLike(mResult.getPlaceId(),UserHelper.getCurrentUser().getUid());
            Toast.makeText(this, getResources().getString(R.string.unliked), Toast.LENGTH_SHORT).show();
            mLikeBtn.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.star_rate_black), null, null);
            mLikeBtn.setText("Like");
        } else {
            Toast.makeText(this, getResources().getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.resdetail_rescall)
    public void callRestaurant(){
        Intent callIntent = new Intent(Intent.ACTION_CALL,
                Uri.parse(TEL + mResult.getFormattedPhoneNumber()));
        startActivity(callIntent);
        //if(ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
        //        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
        //    //TODO : fix call
        //    Intent callIntent = new Intent(Intent.ACTION_CALL,
        //            Uri.fromParts(TEL, mResult.getFormattedPhoneNumber(), null));
        //    startActivity(callIntent);
        //}
        //else {
        //    Toast.makeText(this, getResources().getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
        //}

    }

    @OnClick(R.id.resdetail_resweb)
    public void goToWebSite(){
        if(mResult.getWebsite() != null){
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mResult.getWebsite()));
            startActivity(websiteIntent);
        } else {
            Toast.makeText(this, getResources().getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_detail_restaurant;
    }

    private void disposeWhenDestroy(){
        if(this.mDisposable != null && !this.mDisposable.isDisposed()) this.mDisposable.dispose();
    }
}
