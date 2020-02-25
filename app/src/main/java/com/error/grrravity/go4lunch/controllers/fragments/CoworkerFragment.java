package com.error.grrravity.go4lunch.controllers.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.controllers.base.BaseFragment;
import com.error.grrravity.go4lunch.utils.helper.User;
import com.error.grrravity.go4lunch.utils.helper.UserHelper;
import com.error.grrravity.go4lunch.views.CoworkerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CoworkerFragment extends BaseFragment {

    @BindView(R.id.fragment_coworker_recyclerview)
    RecyclerView mRecyclerView;

    private List<User> mUserList;
    private CoworkerAdapter mCoworkerAdapter;

    public static CoworkerFragment newInstance(){
        return new CoworkerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coworker, container, false);
        ButterKnife.bind(this, view);
        this.configureRecyclerView();
        UserHelper.getCoworkers(userList -> {
            CoworkerFragment.this.mUserList = userList;
            mCoworkerAdapter.refreshAdapter(mUserList);
        });

        return view;
    }

    private void configureRecyclerView() {
        this.mUserList = new ArrayList<>();
        this.mCoworkerAdapter = new CoworkerAdapter(mUserList, 1);
        this.mRecyclerView.setAdapter(this.mCoworkerAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }
}
