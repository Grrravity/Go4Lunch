package com.error.grrravity.go4lunch.utils.helper;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    private final User user = new User("999","OpenClassrooms","www.openclassrooms.fr");

    @Test
    public void testGetUser() {
        String userName = user.getUsername();
        String uid = user.getUid();
        String urlPicture = user.getUrlPicture();

        assertEquals("OpenClassrooms", userName);
        assertEquals("999", uid);
        assertEquals("www.openclassrooms.fr", urlPicture);

    }

    @Test
    public void testSetUser() {
        String newUserName = "LeSiteDuZéro";
        user.setUsername(newUserName);
        String userName = user.getUsername();

        assertNotEquals("OpenClassrooms", userName);
    }
}