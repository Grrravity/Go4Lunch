package com.error.grrravity.go4lunch.utils.helper;

import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class TimeHelperTest {

    @Test
    public void testGetDelays() {
        long targetTime;

        targetTime = TimeUnit.HOURS.toMillis(9);

        ArrayList<Long> testDelays = TimeHelper.getDelays();
        assertEquals(testDelays.get(1) - testDelays.get(0),
                TimeUnit.HOURS.toMillis(3));
        assertTrue(testDelays.get(0) > targetTime);
    }
}

