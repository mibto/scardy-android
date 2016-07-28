package me.scardy;

import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * Created by MB on 25.07.2016.
 * for android
 */
public class ApiClientTest {
    @Test
    public void getKeyStore() throws Exception {
        ApiClient apiClient = new ApiClient();
        apiClient.getKeyStore( "blubb", new Date( 1469474074747L ) );
    }

    @Test
    public void getSharedDataStore() throws Exception {
        ApiClient apiClient = new ApiClient();
        apiClient.getSharedDataStore( "blubb", new Date( 1469474074747L ) );
    }

    @Test
    public void createKeyStore() throws Exception {
        ApiClient apiClient = new ApiClient();
        apiClient.createKeyStore( Long.toString( new Date().getTime() ), "akjdshflkajsdfhkjadfhalkjdfhakljdfhakldfjahdlkfjadhsfkjladh" );
    }

    @Test
    public void updateKeyStore() throws Exception {
        ApiClient apiClient = new ApiClient();
        apiClient.updateKeyStore( "blubb", "aldsfjalkdsjfkasdfkasdfkjadjkfahkjsdfakjsdfas" );
    }

    @Test
    public void createSharedDataStore() throws Exception {
        ApiClient apiClient = new ApiClient();
        if ( apiClient.createSharedDataStore( Long.toString( new Date().getTime() ), "hashOfMasterKey", "asdlfkaskldfjkadfjkajdfklaldhfkjhadjlfkahsdfkljasdf" ) ) {
            System.out.println( "true" );
        } else {
            System.out.println( "false" );
        }
    }

    @Test
    public void updateSharedDataStore() throws Exception {
        ApiClient apiClient = new ApiClient();
        apiClient.updateSharedDataStore( "1469528579519", "hashOfMasterKey", "asdfahdfkasdjfkjasdhfljkasdhfjklashdfkljadshfjklasdhf" );
    }

    @Test
    public void getKeyStoreVersions() throws Exception {
        ApiClient apiClient = new ApiClient();
        List<Date> versions = apiClient.getKeyStoreVersions( "blubb" );
        for ( Date version : versions ) {
            System.out.println( version.getTime() );
        }
    }

    @Test
    public void getSharedDataStoreVersions() throws Exception {
        ApiClient apiClient = new ApiClient();
        List<Date> versions = apiClient.getSharedDataStoreVersions( "1469528579519" );
        for ( Date version : versions ) {
            System.out.println( version.getTime() );
        }
    }

}