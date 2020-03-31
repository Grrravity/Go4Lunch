package com.error.grrravity.go4lunch.controllers.base;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;

@SuppressWarnings({"unused"})
public abstract class BaseActivity extends AppCompatActivity{
    private static final String REMINDER_WORKER = "REMINDER" ;
    private static final String PARTICIPATION_WORKER = "PARTICIPATION" ;
    private static final String PREFS = "PREFS" ;
    // --------------------
    // LIFE CYCLE
    // --------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        this.setContentView(this.getFragmentLayout());
        ButterKnife.bind(this);
        SharedPreferences prefs = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
    }
    protected abstract int getFragmentLayout();
}
