package com.error.grrravity.go4lunch.utils.helper;

import java.util.ArrayList;
import java.util.Calendar;

public class TimeHelper {

    public static ArrayList<Long> getDelays(){
        Calendar dueTime = Calendar.getInstance();
        Calendar currentTime = Calendar.getInstance();
        ArrayList<Long> delays = new ArrayList<>() ;
        long notifyDelay;
        long deleteDelay;

        //Setting base notif time today at 12:00:00
        dueTime.set(Calendar.HOUR_OF_DAY, 12);
        dueTime.set(Calendar.MINUTE, 0);
        dueTime.set(Calendar.SECOND, 0);

        //Checking if 12:00 is already past
        if (dueTime.before(currentTime)) {
            dueTime.add(Calendar.HOUR_OF_DAY,24);
        }

        //Setting delay as the difference of target time and current time
        notifyDelay = dueTime.getTimeInMillis() - currentTime.getTimeInMillis();
        //Setting delete participation delay at 15pm
        dueTime.add(Calendar.HOUR_OF_DAY, 3);
        deleteDelay = dueTime.getTimeInMillis() - currentTime.getTimeInMillis();

        delays.clear();
        delays.add(notifyDelay);
        delays.add(deleteDelay);

        return delays;
    }
}
