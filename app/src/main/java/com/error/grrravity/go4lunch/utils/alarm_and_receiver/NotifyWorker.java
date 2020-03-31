package com.error.grrravity.go4lunch.utils.alarm_and_receiver;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.error.grrravity.go4lunch.R;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class NotifyWorker extends Worker {

    private static final String PREFS = "PREFS" ;

    private static final String TAG = "NotifyWorker" ;

    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        triggerNotification();
        Log.d(TAG, "doWork: Triggered");
        return Result.success();
    }

    private void triggerNotification() {
        SharedPreferences prefs = Objects.requireNonNull(getApplicationContext()).getSharedPreferences(PREFS, MODE_PRIVATE);
        String restaurantName = prefs.getString("restaurantName", "empty");
        String coworkers = prefs.getString("restaurantCoworker", "empty");
        String address = prefs.getString("restaurantAddress", "empty");
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        String text;
        String title;
        assert restaurantName != null;
        if (restaurantName.equals("empty")){
            title = getApplicationContext().getString(R.string.notificationTitle0);
        } else {
            title = getApplicationContext().getString(R.string.notificationTitle, restaurantName);
        }

        //checking cases
        assert coworkers != null;
        assert address != null;
        if (coworkers.equals("empty") && address.equals("empty")){
            text = getApplicationContext().getString(R.string.notificationText00);
        }
        else if (coworkers.equals("empty")){
            text = getApplicationContext().getString(R.string.notificationText01 , address);
        } else if (address.equals("empty")){
            text = getApplicationContext().getString(R.string.notificationText10 ,coworkers);
        } else {
            text = getApplicationContext().getString(R.string.notificationText ,coworkers, address);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(1, notification.build());
    }
}
