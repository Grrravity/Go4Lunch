package com.error.grrravity.go4lunch.controllers.base;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.error.grrravity.go4lunch.utils.alarm_and_receiver.DeletParticipationWorker;
import com.error.grrravity.go4lunch.utils.alarm_and_receiver.NotifyWorker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity{
    private static final String REMINDER_WORKER = "REMINDER" ;
    private static final String PARTICIPATION_WORKER = "PARTICIPATION" ;
    private static final String PREFS = "PREFS" ;
    SharedPreferences prefs;
    // --------------------
    // LIFE CYCLE
    // --------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        this.setContentView(this.getFragmentLayout());
        ButterKnife.bind(this);
        prefs = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);
    }
    public abstract int getFragmentLayout();
}
