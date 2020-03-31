package com.error.grrravity.go4lunch.controllers.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.utils.alarm_and_receiver.DeleteParticipationWorker;
import com.error.grrravity.go4lunch.utils.alarm_and_receiver.NotifyWorker;
import com.error.grrravity.go4lunch.utils.helper.TimeHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;

import static android.content.Context.MODE_PRIVATE;


public abstract class BaseFragment extends Fragment {

    protected Disposable mDisposable;
    protected static final String RESTAURANT = "restaurant";
    private static final String REMINDER_WORKER = "REMINDER" ;
    private static final String PARTICIPATION_WORKER = "PARTICIPATION" ;
    private static final String TAG = "BaseFragment" ;
    private static final String PREFS = "PREFS" ;
    private SharedPreferences prefs;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = Objects.requireNonNull(getContext()).getSharedPreferences(PREFS, MODE_PRIVATE);
    }

    @Override
    public void onAttach(@NotNull Context context) {
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

    protected void setAlarm(boolean b) {
        if(prefs.getBoolean(getString(R.string.notificationPref), true)) {

            WorkManager mWorkManager = WorkManager.getInstance(Objects.requireNonNull(getContext()));
            if (b) {
                ArrayList<Long> delays = TimeHelper.getDelays();
                if (delays.size() != 2) {
                    Log.d(TAG, getString(R.string.invalidDelay));
                } else {
                    long notifyDelay = delays.get(0);
                    long deleteDelay = delays.get(1);

                    //Notify Worker
                    mWorkManager.cancelAllWorkByTag(REMINDER_WORKER);
                    OneTimeWorkRequest mRequestReminder = new OneTimeWorkRequest.Builder(NotifyWorker.class)
                            .setInitialDelay(notifyDelay, TimeUnit.MILLISECONDS)
                            .addTag(REMINDER_WORKER)
                            .build();
                    mWorkManager.enqueue(mRequestReminder);
                    Log.d("Reminder: ", " setAlarm: set in " + notifyDelay);


                    //Worker to delete participation
                    mWorkManager.cancelAllWorkByTag(PARTICIPATION_WORKER);
                    OneTimeWorkRequest mRequestParticipation = new OneTimeWorkRequest.Builder(DeleteParticipationWorker.class)
                            .setInitialDelay(deleteDelay, TimeUnit.MILLISECONDS)
                            .addTag(PARTICIPATION_WORKER)
                            .build();
                    mWorkManager.enqueue(mRequestParticipation);
                    Log.d("Participation: ", " setAlarm: set in " + deleteDelay);
                }
            } else {
                Log.d("SetAlarm:", " AlarmDeleted");
                mWorkManager.cancelAllWorkByTag(REMINDER_WORKER);
                mWorkManager.cancelAllWorkByTag(PARTICIPATION_WORKER);
            }
        } else {
            Log.d("ALARM", " will not set because of preferences");
        }
    }
}
