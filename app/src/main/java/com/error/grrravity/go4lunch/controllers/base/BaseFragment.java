package com.error.grrravity.go4lunch.controllers.base;

import android.content.Context;

import androidx.fragment.app.Fragment;

import io.reactivex.disposables.Disposable;


public abstract class BaseFragment extends Fragment {

    public static final int GOOGLE_MAPS_FRAGMENT = 0;
    public static final int RESTAURANTS_FRAGMENT = 1;
    public static final int LIKED_RESTAURANTS_FRAGMENT = 2;
    public static final int USER_FRAGMENT = 3;

    protected Disposable mDisposable;
    public static final String RESTAURANT = "restaurant";
    public static final String ID = "ID";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //TODO if internet ok
      //  if ( OK ) {
      //      getActivity().finish();
      //      getActivity().recreate();
      //  }
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
