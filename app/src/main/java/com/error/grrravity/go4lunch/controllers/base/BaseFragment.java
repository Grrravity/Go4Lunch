package com.error.grrravity.go4lunch.controllers.base;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.reactivex.disposables.Disposable;


public abstract class BaseFragment extends Fragment {

    protected Disposable mDisposable;
    public static final String RESTAURANT = "restaurant";
    public static final String ID = "ID";



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    protected void disposeWhenDestroy(){
        if (this.mDisposable != null && !this.mDisposable.isDisposed()) this.mDisposable.dispose();
    }
}
