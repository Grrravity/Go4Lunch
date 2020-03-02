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

    protected void setAlarm(boolean forSet) {
        if(prefs.getInt("notif", 1) == 1 ) {

            WorkManager mWorkManager = WorkManager.getInstance(this);
            if (forSet) {
                //Now time
                Calendar nowTime = Calendar.getInstance();

                //dueDate delay
                Calendar dueDate = Calendar.getInstance();

                // Set Execution around 12:00:00 AM
                dueDate.set(Calendar.HOUR_OF_DAY, 12);
                dueDate.set(Calendar.MINUTE, 0);
                dueDate.set(Calendar.SECOND, 0);

                //Check if dueDate is before actual time and add 24hours if true
                if (dueDate.before(nowTime)) {
                    dueDate.add(Calendar.HOUR_OF_DAY, 24);
                }

                //Provide the difference in Milis for Notif Worker
                long timeDiffNotif = dueDate.getTimeInMillis() - nowTime.getTimeInMillis();

                //Provide the difference in Milis for Participation Worker
                dueDate.add(Calendar.HOUR_OF_DAY, 3);
                long timeDiffParticipation = dueDate.getTimeInMillis() - nowTime.getTimeInMillis();
                //Notify Worker

                //PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                //        NotifyWorker.class,
                //        24,
                //        TimeUnit.HOURS,
                //        PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
                //        TimeUnit.MILLISECONDS)
                //        .setInitialDelay(timeDiffNotif, TimeUnit.MILLISECONDS)
                //        .addTag(REMINDER_WORKER)
                //        .build();
                //mWorkManager.enqueueUniquePeriodicWork(REMINDER_WORKER, ExistingPeriodicWorkPolicy.REPLACE, workRequest);

                mWorkManager.cancelAllWorkByTag(REMINDER_WORKER);
                OneTimeWorkRequest mRequestReminder = new OneTimeWorkRequest.Builder(NotifyWorker.class)
                        .setInitialDelay(timeDiffNotif, TimeUnit.MILLISECONDS)
                        .addTag(REMINDER_WORKER)
                        .build();
                mWorkManager.enqueue(mRequestReminder);
                Log.d("Reminder", " setAlarm: set in " + timeDiffNotif);


                //Worker to delet participation

                //PeriodicWorkRequest workRequest2 = new PeriodicWorkRequest.Builder(
                //        DeletParticipationWorker.class,
                //        24,
                //        TimeUnit.HOURS,
                //        PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
                //        TimeUnit.MILLISECONDS)
                //        .setInitialDelay(timeDiffParticipation, TimeUnit.MILLISECONDS)
                //        .addTag(PARTICIPATION_WORKER)
                //        .build();
                //mWorkManager.enqueueUniquePeriodicWork(PARTICIPATION_WORKER, ExistingPeriodicWorkPolicy.REPLACE, workRequest2);

                mWorkManager.cancelAllWorkByTag(PARTICIPATION_WORKER);
                OneTimeWorkRequest mRequestParticipation = new OneTimeWorkRequest.Builder(DeletParticipationWorker.class)
                        .setInitialDelay(timeDiffParticipation, TimeUnit.MILLISECONDS)
                        .addTag(PARTICIPATION_WORKER)
                        .build();
                mWorkManager.enqueue(mRequestParticipation);
                Log.d("Participation", " setAlarm: set in " + timeDiffParticipation);

            } else {
                Log.d("SetAlarm:", " AlarmDeleted");
                mWorkManager.cancelAllWorkByTag(REMINDER_WORKER);
                mWorkManager.cancelAllWorkByTag(PARTICIPATION_WORKER);
            }
        } else {
            Log.d("ALARM", "no alarm set because of preferences");
        }
    }
}
