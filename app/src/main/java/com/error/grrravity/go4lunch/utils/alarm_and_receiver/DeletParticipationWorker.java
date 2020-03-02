package com.error.grrravity.go4lunch.utils.alarm_and_receiver;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.utils.helper.UserHelper;

public class DeletParticipationWorker extends Worker {

    public DeletParticipationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Method to trigger an instant notification
        triggerDeletParticipation();
        Log.d("ParticipationWorker", "doWork: Triggered");

        return Result.success();
        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }

    private void triggerDeletParticipation() {
        if (UserHelper.getCurrentUser()!=null) {
            UserHelper.deleteUserAtRestaurant(UserHelper.getCurrentUser().getUid());
        }
    }
}
