package me.scardy;

import org.junit.Test;

import javax.crypto.KeyGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by MB on 25.07.2016.
 * for android
 */
public class KeyStoreTest {
    @Test
    public void getProfile() throws Exception {

    }

    @Test
    public void getSharedKeys() throws Exception {

    }

    @Test
    public void getContactKeys() throws Exception {

    }

    @Test
    public void toJSON() throws Exception {
        KeyGenerator aesKeyGenerator = KeyGenerator.getInstance( "AES" );
        aesKeyGenerator.init( 256 );

        KeyStore keyStore = new KeyStore();

        Profile profile = new Profile();
        Permission fullName = Permission.FULL_NAME;
        fullName.setValue( "Michael Brodmann" );
        profile.setFullName( fullName );

        keyStore.setProfile( profile );


        List<SharedKey> sharedKeys = new ArrayList<>();
        SharedKey sharedKey1 = new SharedKey();
        sharedKey1.addPermission( Permission.ADDRESS );
        sharedKey1.setLabel( "Manuela Berger" );
        sharedKey1.setKey( aesKeyGenerator.generateKey() );
        sharedKeys.add( sharedKey1 );

        SharedKey sharedKey2 = new SharedKey( aesKeyGenerator.generateKey(), "Dave Avanthay", Arrays.asList( Permission.PHONE, Permission.ADDRESS ) );
        sharedKeys.add( sharedKey2 );

        SharedKey sharedKey3 = new SharedKey();
        sharedKey3.addPermission( Permission.EMAIL );
        sharedKey3.setLabel( "Stephan Zumbühl" );
        sharedKey3.setKey( aesKeyGenerator.generateKey() );

        keyStore.setSharedKeys( sharedKeys );
        keyStore.addSharedKey( sharedKey3 );

        List<ContactKey> contactKeys = new ArrayList<>();

        ContactKey contactKey1 = new ContactKey();
        contactKey1.setKey( aesKeyGenerator.generateKey() );
        contactKey1.setDate( new Date() );

        ContactKey contactKey2 = new ContactKey( aesKeyGenerator.generateKey(), new Date() );

        contactKeys.add( contactKey1 );
        contactKeys.add( contactKey2 );

        keyStore.setContactKeys( contactKeys );
        System.out.println( keyStore.toJSON() );
    }

}