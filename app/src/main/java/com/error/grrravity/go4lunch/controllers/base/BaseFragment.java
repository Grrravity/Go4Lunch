package com.error.grrravity.go4lunch.controllers.base;

import android.app.Fragment;
import android.content.Context;


public abstract class BaseFragment extends Fragment {

    public static final int GOOGLE_MAPS_FRAGMENT = 0;
    public static final int RESTAURANTS_FRAGMENT = 1;
    public static final int LIKED_RESTAURANTS_FRAGMENT = 2;
    public static final int USER_FRAGMENT = 3;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //TODO if internet ok
      //  if ( OK ) {
      //      getActivity().finish();
      //      getActivity().recreate();
      //  }
    }
}
