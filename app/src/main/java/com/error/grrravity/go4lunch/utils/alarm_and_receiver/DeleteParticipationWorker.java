package com.error.grrravity.go4lunch.utils.alarm_and_receiver;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.error.grrravity.go4lunch.utils.helper.UserHelper;

public class DeleteParticipationWorker extends Worker {

    private static final String TAG = "DeleteParticipationW" ;


    public DeleteParticipationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Method to trigger an instant notification
        triggerDeleteParticipation();
        Log.d(TAG, "doWork: Triggered");

        return Result.success();
        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }

    private void triggerDeleteParticipation() {
        if (UserHelper.getCurrentUser()!=null) {
            UserHelper.deleteUserAtRestaurant(UserHelper.getCurrentUser().getUid());
        }
    }
}
