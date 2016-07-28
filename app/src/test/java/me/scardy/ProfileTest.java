package me.scardy;

import org.junit.Test;

/**
 * Created by MB on 24.07.2016.
 * for android
 */
public class ProfileTest {
    @Test
    public void toJSON() throws Exception {
        Profile profile = new Profile();
        Permission address = Permission.ADDRESS;
        address.setValue( "Gattikonerstrasse 117" );
        profile.setAddress( address );
        System.out.println( profile.toJSON() );
    }
}