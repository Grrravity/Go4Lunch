package com.error.grrravity.go4lunch.controllers.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.error.grrravity.go4lunch.utils.alarm_and_receiver.NotifyWorker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity{
    private static final String WORK_TAG = "REMINDER" ;
    private Boolean alreadySet;
    // --------------------
    // LIFE CYCLE
    // --------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        this.setContentView(this.getFragmentLayout());
        ButterKnife.bind(this);
    }
    public abstract int getFragmentLayout();

    protected void setAlarm(boolean forSet) {
        WorkManager mWorkManager = WorkManager.getInstance(this);
        if (forSet) {
            //Calculating if < or > than 12am
            Calendar calendar = Calendar.getInstance();
            long nowMillis = calendar.getTimeInMillis();

            if (calendar.get(Calendar.HOUR_OF_DAY) >= 12 )
            //||(calendar.get(Calendar.HOUR_OF_DAY) == 12 && calendar.get(Calendar.MINUTE) > 0)
            {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            calendar.set(Calendar.HOUR_OF_DAY, 12);
            calendar.set(Calendar.MINUTE, 0);

            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long diff = calendar.getTimeInMillis() - nowMillis;

            //TODO set 24hours delay for next alarm
            //PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
            //        NotifyWorker.class,
            //    24,
            //    TimeUnit.HOURS,
            //    PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            //    TimeUnit.MILLISECONDS)
            //        .setInitialDelay(diff, TimeUnit.MILLISECONDS)
            //        .addTag(WORK_TAG)
            //        .build();
            //mWorkManager.enqueueUniquePeriodicWork(WORK_TAG, ExistingPeriodicWorkPolicy.REPLACE, workRequest);

            mWorkManager.cancelAllWorkByTag(WORK_TAG);
            OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(NotifyWorker.class)
                    .setInitialDelay(diff, TimeUnit.MILLISECONDS)
                    .addTag(WORK_TAG)
                    .build();
            mWorkManager.enqueue(mRequest);
            Log.d("ALARM", "setAlarm: setted at ");
            //TODO add suppress restau at 3pm
        } else {
            mWorkManager.cancelAllWorkByTag(WORK_TAG);
        }
    }
}
