package com.error.grrravity.go4lunch.utils.alarm_and_receiver;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.utils.helper.UserHelper;

public class NotifyWorker extends Worker {

    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Method to trigger an instant notification
        triggerNotification();
        Log.d("WORKER", "doWork: Triggered");

        //TODO : delet participation after 3pm
        //if (UserHelper.getCurrentUser()!=null) {
        //    UserHelper.deleteUserAtRestaurant(UserHelper.getCurrentUser().getUid());
        //}

        return Result.success();
        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }

    private void triggerNotification() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setContentTitle("Go4Food: N'oubliez pas votre repas chez " + "TODO")
                .setContentText("TODO List" + " vous attend(ent) au " + "TODO adresse")
                .setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(1, notification.build());
    }
}
