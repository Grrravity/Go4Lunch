package com.error.grrravity.go4lunch.controllers.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.controllers.base.BaseFragment;
import com.error.grrravity.go4lunch.utils.ItemClickHelper;
import com.error.grrravity.go4lunch.utils.helper.User;
import com.error.grrravity.go4lunch.utils.helper.UserHelper;
import com.error.grrravity.go4lunch.views.CoworkerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CoworkerFragment extends BaseFragment {

    @BindView(R.id.fragment_coworker_recyclerview)
    RecyclerView mRecyclerView;

    private static final String PREFS = "PREFS" ;

    private List<User> mUserList;
    private CoworkerAdapter mCoworkerAdapter;
    private SharedPreferences prefs;

    public static CoworkerFragment newInstance(){
        return new CoworkerFragment();
    }

    @SuppressWarnings({"unused"})
    public static CoworkerFragment newInstance(List<User> userList){
        CoworkerFragment fragment = new CoworkerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userList", (Serializable) userList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coworker,
                container,
                false);
        ButterKnife.bind(this, view);

        mUserList = new ArrayList<>();

        prefs = Objects.requireNonNull(getContext()).getSharedPreferences(PREFS, Context.MODE_PRIVATE);


        UserHelper.getCoworkers(userList -> {
            CoworkerFragment.this.mUserList = userList;
            mCoworkerAdapter.refreshAdapter(mUserList);
            mCoworkerAdapter.notifyDataSetChanged();
        });

        this.configureRecyclerView();
        this.configureClickOnRecyclerViewItem();
        return view;
    }

    private void configureRecyclerView() {
        this.mUserList = new ArrayList<>();
        this.mCoworkerAdapter = new CoworkerAdapter(mUserList, 1);
        this.mRecyclerView.setAdapter(this.mCoworkerAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void configureClickOnRecyclerViewItem() {
        ItemClickHelper.addTo(mRecyclerView)
                .setOnItemClickListener((mRecyclerView, position, v) -> {
                    String placeID = mCoworkerAdapter.getResultList().get(position).getJoinedRestaurantId();
                    RestaurantDetailFragment detailFragment = new RestaurantDetailFragment();
                    Bundle args = new Bundle();
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("id", placeID);
                    editor.apply();
                    detailFragment.setArguments(args);
                    FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.activity_welcome_drawer_layout, detailFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                    fragmentTransaction.addToBackStack(null);
                });
    }


}
