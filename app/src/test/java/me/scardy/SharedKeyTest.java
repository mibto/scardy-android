package me.scardy;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by MB on 24.07.2016.
 */
public class SharedKeyTest {
    @Test
    public void getPermissions() throws Exception {

    }

    @Test
    public void addPermission() throws Exception {
        SharedKey sharedKey = new SharedKey();
        sharedKey.addPermission( Permission.ADDRESS );
        sharedKey.addPermission( Permission.FULL_NAME );

        List<Permission> permissions = sharedKey.getPermissions();
        Assert.assertEquals( Permission.ADDRESS, permissions.get( 0 ) );
        Assert.assertEquals( Permission.FULL_NAME, permissions.get( 1 ) );
    }

    @Test
    public void setPermissions() throws Exception {

    }

    @Test
    public void toJSON() throws Exception {

    }

}