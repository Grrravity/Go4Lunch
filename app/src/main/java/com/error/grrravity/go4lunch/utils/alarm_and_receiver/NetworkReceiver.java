package com.error.grrravity.go4lunch.utils.alarm_and_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.error.grrravity.go4lunch.utils.helper.NetworkChecker;

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!NetworkChecker.isNetworkAvailable(context)){
            Toast.makeText(context, "no internet", Toast.LENGTH_SHORT).show();
        }
    }
}
