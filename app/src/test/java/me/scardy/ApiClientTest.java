package me.scardy;

import org.junit.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

/**
 * Created by MB on 25.07.2016.
 * for android
 */
public class ApiClientTest {
    SecretKey masterKey = KeyGenerator.getInstance( "AES" ).generateKey();
    CryptoClient cryptoClient = new CryptoClient( masterKey );
    String masterKeyHash = cryptoClient.getHash( masterKey.getEncoded() );

    public ApiClientTest() throws NoSuchAlgorithmException {
    }

    @Test
    public void getKeyStore() throws Exception {
        ApiClient apiClient = new ApiClient( masterKey, masterKeyHash );
        apiClient.getKeyStore( "blubb", new Date( 1469474074747L ) );
    }

    @Test
    public void getSharedDataStore() throws Exception {
        ApiClient apiClient = new ApiClient( masterKey, masterKeyHash );
        // apiClient.getSharedDataStore( "blubb", new Date( 1469474074747L ) );
    }

    @Test
    public void createKeyStore() throws Exception {
        ApiClient apiClient = new ApiClient( masterKey, masterKeyHash );
        apiClient.createKeyStore( new KeyStore() );
    }

    @Test
    public void updateKeyStore() throws Exception {
        ApiClient apiClient = new ApiClient( masterKey, masterKeyHash );
        KeyStore keyStore = new KeyStore();
        keyStore.addSharedKey( new SharedKey() );
        apiClient.updateKeyStore( keyStore );
    }

    @Test
    public void createSharedDataStore() throws Exception {
        ApiClient apiClient = new ApiClient( masterKey, masterKeyHash );

    }

    @Test
    public void updateSharedDataStore() throws Exception {
        ApiClient apiClient = new ApiClient( masterKey, masterKeyHash );

    }

    @Test
    public void getKeyStoreVersions() throws Exception {
        ApiClient apiClient = new ApiClient( masterKey, masterKeyHash );
        List<Date> versions = apiClient.getKeyStoreVersions();
        for ( Date version : versions ) {
            System.out.println( version.getTime() );
        }
    }

    @Test
    public void getSharedDataStoreVersions() throws Exception {
        ApiClient apiClient = new ApiClient( masterKey, masterKeyHash );
        // List<Date> versions = apiClient.getSharedDataStoreVersions( "SyeqE5ioRA3JbY4fPd8kXzx0GK3YnKdCBSN5TO0ZkQI=" );
        //for ( Date version : versions ) {
        //   System.out.println( version.getTime() );
        // }
    }

}