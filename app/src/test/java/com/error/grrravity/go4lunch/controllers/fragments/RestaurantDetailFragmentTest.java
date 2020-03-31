package com.error.grrravity.go4lunch.controllers.fragments;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RestaurantDetailFragmentTest {

    @Test
    public void testCalculateStars(){
        double rate = 5;
        double targetStars = 3;
        double calculatedRate = RestaurantDetailFragment.calculateStars(rate);

        assertEquals(targetStars,calculatedRate,0);
    }

}