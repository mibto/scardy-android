package me.scardy;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import me.scardy.dummy.DummyContent;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import static me.scardy.Constants.MASTER_KEY;

public class ScardyActivity extends AppCompatActivity implements ContactsFragment.OnListFragmentInteractionListener, SharesFragment.OnListFragmentInteractionListener {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private me.scardy.KeyStore keyStore;
    private ApiClient apiClient;
    private FloatingActionButton fab;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy( policy );
        setContentView( R.layout.activity_scardy );

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById( R.id.container );
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter( mViewPager, getSupportFragmentManager() );
        mViewPager.setAdapter( mSectionsPagerAdapter );
        fab = (FloatingActionButton) findViewById( R.id.fab );


        TabLayout tabLayout = (TabLayout) findViewById( R.id.tabs );
        tabLayout.setupWithViewPager( mViewPager );

        init();
    }


    private void init() {
        try {
            KeyStore androidKeyStore = KeyStore.getInstance( "AndroidKeyStore" );
            androidKeyStore.load( null );
            SecretKey masterKey = (SecretKey) androidKeyStore.getKey( MASTER_KEY, null );
            boolean newInstall = false;
            if ( masterKey == null ) {
                newInstall = true;
                masterKey = initMasterKey( androidKeyStore );
            }
            String masterKeyHash = getMasterKeyHash();
            apiClient = new ApiClient( masterKey, masterKeyHash );
            if ( newInstall ) {
                apiClient.createKeyStore( new me.scardy.KeyStore() );
            }
            keyStore = apiClient.getLatestKeyStore();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private String getMasterKeyHash() {
        try {
            byte[] masterKeyHash = new byte[ 44 ];
            FileInputStream in = openFileInput( "masterKeyHash" );
            in.read( masterKeyHash );
            in.close();
            return new String( masterKeyHash );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    private SecretKey initMasterKey( KeyStore androidKeyStore ) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, FileNotFoundException {
        KeyGenerator keyGen;
        keyGen = KeyGenerator.getInstance( "AES" );
        keyGen.init( 256 ); //256 bit AES Key
        SecretKey masterKeyDoNotUse = keyGen.generateKey();

        //Store the generate key in the Android HSM
        androidKeyStore.setEntry(
                MASTER_KEY,
                new KeyStore.SecretKeyEntry( masterKeyDoNotUse ),
                new KeyProtection.Builder( KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT )
                        .setBlockModes( KeyProperties.BLOCK_MODE_CBC )
                        .setEncryptionPaddings( KeyProperties.ENCRYPTION_PADDING_PKCS7 )
                        .build() );
        //Load the key from the Android HSM - just to be sure that the key was really stored
        SecretKey androidMasterKey = (SecretKey) androidKeyStore.getKey( "masterKey", null );

        //decrypt the master key with itself and store it on the internal file system
        //this file can only be access from the app itself
        new CryptoClient().saveMasterKey( masterKeyDoNotUse, androidMasterKey, openFileOutput( "masterKey", MODE_PRIVATE ) );
        new CryptoClient().saveMasterKeyHash( masterKeyDoNotUse, openFileOutput( "masterKeyHash", MODE_PRIVATE ) );

        return androidMasterKey;
    }

    private void setFABVisibility( final int position ) {
        switch ( position ) {
            case 0:
                fab.hide();
                break;
            case 1:
                fab.show();
                break;
            case 2:
                fab.show();
                break;
        }
    }

    private void setFABAction( final int position, final Activity activity ) {
        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
        if ( position == 1 ) {
            fab.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View view ) {
                    IntentIntegrator intentIntegrator = new IntentIntegrator( activity );
                    intentIntegrator.initiateScan();
                }
            } );
        }
        if ( position == 2 ) {
            fab.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View view ) {
                    createShare();
                }
            } );
        }
    }

    private void setFABStyle( final int position ) {
        if ( position == 1 ) {
            fab.setImageDrawable( getDrawable( R.drawable.add_user ) );
            fab.setBackgroundTintList( ColorStateList.valueOf( Color.GREEN ) );
        }
        if ( position == 2 ) {
            fab.setImageDrawable( getDrawable( R.drawable.add_key ) );
            fab.setBackgroundTintList( ColorStateList.valueOf( Color.RED ) );
        }
    }

    private void createShare() {
        System.out.println( "create Share" );
    }


    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_scardy, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if ( id == R.id.action_settings ) {
            return true;
        }

        if ( id == R.id.save_profile ) {
            saveProfile();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onListFragmentInteraction( DummyContent.DummyItem item ) {
        //do nothing
    }

    public void onActivityResult( int requestCode, int resultCode, Intent intent ) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult( requestCode, resultCode, intent );
        if ( scanResult != null ) {
            System.out.println( new String( scanResult.getRawBytes() ) );
            SecretKey key = new SecretKeySpec( scanResult.getRawBytes(), 0, scanResult.getRawBytes().length, "AES" );
            ContactKey contactKey = new ContactKey( key );
            keyStore.addContactKey( contactKey );

        }
    }

    @Override
    public void onPostCreate( Bundle savedInstanceState ) {
        final Activity activity = this;
        super.onPostCreate( savedInstanceState );
        mViewPager.addOnPageChangeListener( new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected( int position ) {
                setFABAction( position, activity );
                setFABStyle( position );
                setFABVisibility( position );
            }
        } );
    }


    public void saveProfile() {
        ProfileFragment fragment = (ProfileFragment) mSectionsPagerAdapter.getItem( 0 );
        Profile profile = keyStore.getProfile();

        Permission fullName = Permission.FULL_NAME;
        fullName.setValue( fragment.getName() );
        profile.addPermission( fullName );

        Permission address = Permission.ADDRESS;
        address.setValue( fragment.getAddress() );
        profile.addPermission( address );

        Permission phone = Permission.PHONE;
        phone.setValue( fragment.getPhone() );
        profile.addPermission( phone );

        Permission email = Permission.EMAIL;
        email.setValue( fragment.getEmail() );
        profile.addPermission( email );

        keyStore.setProfile( profile );

        //encrypt and update remote
        apiClient.updateKeyStore( keyStore );

        updateAllRelevantShares();
    }

    private boolean updateAllRelevantShares() {
        return false;
    }

}
