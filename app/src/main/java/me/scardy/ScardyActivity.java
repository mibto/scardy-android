package me.scardy;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static me.scardy.Constants.KEY;
import static me.scardy.Constants.MASTER_KEY;
import static me.scardy.Constants.PERMISSIONS;

public class ScardyActivity extends AppCompatActivity implements ContactsFragment.OnListFragmentInteractionListener, SharesFragment.OnListFragmentInteractionListener, OnCompleteListener {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public me.scardy.KeyStore keyStore;
    private ApiClient apiClient;
    private FloatingActionButton fab;
    private Activity activity;
    public static final int PERMISSIONS_NEW = 101;
    public static final int PERMISSIONS_CHANGE = 102;

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

    private SecretKey initMasterKey( KeyStore androidKeyStore ) throws KeyStoreException, UnrecoverableKeyException, FileNotFoundException, NoSuchAlgorithmException {
        SecretKey masterKeyDoNotUse = new CryptoClient().generateAESKey();

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
        Intent permissionsIntent = new Intent( activity, PermissionActivity.class );
        startActivityForResult( permissionsIntent, PERMISSIONS_NEW );
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
    public void onListFragmentInteraction( Profile item ) {
        //do nothing
    }

    @Override
    public void onListFragmentInteraction( SharedKey item ) {
        Intent permissionsIntent = new Intent( activity, PermissionActivity.class );
        ArrayList<String> permissions = new ArrayList<>();
        for ( Permission permission : item.getPermissions() ) {
            permissions.add( permission.name() );
        }
        permissionsIntent.putExtra( Constants.KEY, new CryptoClient().getStringFromKey( item.getKey() ) );
        permissionsIntent.putExtra( Constants.LABEL, item.getLabel() );
        permissionsIntent.putStringArrayListExtra( PERMISSIONS, permissions );
        startActivityForResult( permissionsIntent, PERMISSIONS_CHANGE );
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent intent ) {
        try {
            if ( requestCode == IntentIntegrator.REQUEST_CODE ) {
                IntentResult scanResult = IntentIntegrator.parseActivityResult( requestCode, resultCode, intent );
                if ( scanResult != null ) {
                    SecretKey key = new CryptoClient().getKeyFromString( Uri.decode( scanResult.getContents() ) );
                    ContactKey contactKey = new ContactKey( key );
                    keyStore.addContactKey( contactKey );
                    apiClient.updateKeyStore( keyStore );

                    ContactsFragment fragment = (ContactsFragment) mSectionsPagerAdapter.getItem( 1 );
                    fragment.addContact( apiClient.getLatestSharedDataStore( key ) );
                }
            }
            if ( requestCode == PERMISSIONS_NEW ) {
                if ( resultCode == RESULT_OK ) {
                    SharedKey sharedProfile = new SharedKey();
                    Profile profile = keyStore.getProfile();
                    String label = intent.getStringExtra( Constants.LABEL );
                    for ( String permissionAsString : intent.getStringArrayListExtra( "permissions" ) ) {
                        Permission permission = Permission.valueOf( permissionAsString );
                        permission.setValue( profile.getPermission( permission.getPermission() ).getValue() );
                        sharedProfile.addPermission( permission );
                    }
                    SecretKey sharedKey = new CryptoClient().generateAESKey();
                    sharedProfile.setKey( sharedKey );
                    if ( label != null && !Objects.equals( label, "" ) ) {
                        sharedProfile.setLabel( label );
                    }
                    keyStore.addSharedKey( sharedProfile );
                    apiClient.updateKeyStore( keyStore );

                    apiClient.createSharedDataStore( sharedProfile );
                    displayQRCode( sharedKey );
                    SharesFragment fragment = (SharesFragment) mSectionsPagerAdapter.getItem( 2 );
                    fragment.addShare( sharedProfile );
                }
            }

            if ( requestCode == PERMISSIONS_CHANGE ) {
                if ( resultCode == RESULT_OK ) {
                    SecretKey key = new CryptoClient().getKeyFromString( intent.getStringExtra( KEY ) );
                    SharedKey sharedProfile = keyStore.getSharedKey( key );

                    Profile profile = keyStore.getProfile();


                    String label = intent.getStringExtra( Constants.LABEL );
                    if ( label != null && !Objects.equals( label, "" ) ) {
                        sharedProfile.setLabel( label );
                    }

                    Set<Permission> permissions = new HashSet<>();
                    for ( String permissionAsString : intent.getStringArrayListExtra( "permissions" ) ) {
                        Permission permission = Permission.valueOf( permissionAsString );
                        permission.setValue( profile.getPermission( permission.getPermission() ).getValue() );
                        permissions.add( permission );
                    }
                    sharedProfile.setPermissions( permissions );

                    keyStore.replaceSharedKey( sharedProfile );
                    apiClient.updateKeyStore( keyStore );
                    apiClient.updateSharedDataStore( sharedProfile );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostCreate( Bundle savedInstanceState ) {
        activity = this;
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

        Permission fullName = new Permission( Permission.PermissionType.FULL_NAME );
        if ( profile.getPermission( Permission.PermissionType.FULL_NAME ) == null || (profile.getPermission( Permission.PermissionType.FULL_NAME ) != null && !Objects.equals( profile.getPermission( Permission.PermissionType.FULL_NAME ).getValue(), fragment.getName() )) ) {
            fullName.setValue( fragment.getName() );
            profile.addPermission( fullName );
            updateRelevantShares( fullName );
        }

        Permission address = new Permission( Permission.PermissionType.ADDRESS );
        if ( profile.getPermission( Permission.PermissionType.ADDRESS ) == null || (profile.getPermission( Permission.PermissionType.ADDRESS ) != null && !Objects.equals( profile.getPermission( Permission.PermissionType.ADDRESS ).getValue(), fragment.getAddress() )) ) {
            address.setValue( fragment.getAddress() );
            profile.addPermission( address );
            updateRelevantShares( address );
        }

        Permission phone = new Permission( Permission.PermissionType.PHONE );
        if ( profile.getPermission( Permission.PermissionType.PHONE ) == null || (profile.getPermission( Permission.PermissionType.PHONE ) != null && !Objects.equals( profile.getPermission( Permission.PermissionType.PHONE ).getValue(), fragment.getPhone() )) ) {
            phone.setValue( fragment.getPhone() );
            profile.addPermission( phone );
            updateRelevantShares( phone );
        }

        Permission email = new Permission( Permission.PermissionType.EMAIL );
        if ( profile.getPermission( Permission.PermissionType.EMAIL ) == null || (profile.getPermission( Permission.PermissionType.EMAIL ) != null && !Objects.equals( profile.getPermission( Permission.PermissionType.EMAIL ).getValue(), fragment.getEmail() )) ) {
            email.setValue( fragment.getEmail() );
            profile.addPermission( email );
            updateRelevantShares( email );
        }


        if ( inputValidation( profile ) ) {
            //update keyStore, encrypt and update remote
            keyStore.setProfile( profile );
            if ( apiClient.updateKeyStore( keyStore ) ) {
                Toast.makeText( activity, "profile saved", Toast.LENGTH_SHORT ).show();
            }

            for ( SharedKey sharedKey : keyStore.getChangedSharedKeys() ) {
                apiClient.updateSharedDataStore( sharedKey );
            }
        } else {
            Toast.makeText( activity, "invalid input", Toast.LENGTH_SHORT ).show();
        }
    }

    private boolean inputValidation( Profile profile ) {
        boolean valid = true;
        for ( Permission permission : profile.getPermissions().values() ) {
            if ( permission.getValue().contains( "'" ) ) {
                valid = false;
            }
            if ( permission.getValue().contains( "\\" ) ) {
                valid = false;
            }
            if ( permission.getValue().contains( "\"" ) ) {
                valid = false;
            }
            if ( permission.getValue().contains( ";" ) ) {
                valid = false;
            }
            if ( permission.getValue().contains( "{" ) ) {
                valid = false;
            }
            if ( permission.getValue().contains( "}" ) ) {
                valid = false;
            }
        }
        return valid;
    }

    private void updateRelevantShares( Permission permission ) {
        List<SharedKey> shares = keyStore.getSharedKeys();
        for ( SharedKey sharedKey : shares ) {
            if ( sharedKey.hasPermission( permission ) ) {
                sharedKey.addPermission( permission );
                sharedKey.setHasChanged();
            }
        }
        keyStore.setSharedKeys( shares );
    }


    private void displayQRCode( SecretKey sharedKey ) {
        View dialogLayout = View.inflate( activity, R.layout.fragment_qrcode, null );
        AlertDialog.Builder builder = new AlertDialog.Builder( activity );
        builder.setView( dialogLayout )
                .setPositiveButton( R.string.action_done, new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int id ) {
                        dialog.cancel();
                    }
                } );

        ImageView image = (ImageView) dialogLayout.findViewById( R.id.qr_code_image );
        if ( image != null ) {
            Bitmap bitmap = generateQRCode( Base64.encodeToString( sharedKey.getEncoded(), Base64.URL_SAFE ) );
            if ( bitmap != null ) {
                image.setImageBitmap( bitmap );
            }
        }
        builder.create();
        builder.show();
    }


    private Bitmap generateQRCode( String key ) {
        final int SIZE = 600;
        try {
            com.google.zxing.Writer writer = new QRCodeWriter();

            BitMatrix bm = writer.encode( Uri.encode( key ), BarcodeFormat.QR_CODE, SIZE, SIZE );
            Bitmap imageBitmap = Bitmap.createBitmap( SIZE, SIZE, Bitmap.Config.ARGB_8888 );

            for ( int i = 0; i < SIZE; i++ ) {//width
                for ( int j = 0; j < SIZE; j++ ) {//height
                    imageBitmap.setPixel( i, j, bm.get( i, j ) ? Color.BLACK : Color.WHITE );
                }
            }
            return imageBitmap;
        } catch ( WriterException e ) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onComplete( int index ) {
        if ( index == 0 ) {
            ProfileFragment fragment = (ProfileFragment) mSectionsPagerAdapter.getItem( index );
            Profile profile = keyStore.getProfile();
            if ( profile.getPermission( Permission.PermissionType.FULL_NAME ) != null ) {
                fragment.setFullName( profile.getPermission( Permission.PermissionType.FULL_NAME ).getValue() );
            }
            if ( profile.getPermission( Permission.PermissionType.PHONE ) != null ) {
                fragment.setPhone( profile.getPermission( Permission.PermissionType.PHONE ).getValue() );
            }
            if ( profile.getPermission( Permission.PermissionType.ADDRESS ) != null ) {
                fragment.setAddress( profile.getPermission( Permission.PermissionType.ADDRESS ).getValue() );
            }
            if ( profile.getPermission( Permission.PermissionType.EMAIL ) != null ) {
                fragment.setEmail( profile.getPermission( Permission.PermissionType.EMAIL ).getValue() );
            }
        }
        if ( index == 1 ) {
            ContactsFragment fragment = (ContactsFragment) mSectionsPagerAdapter.getItem( index );
            List<ContactKey> contactKeys = keyStore.getContactKeys();
            for ( ContactKey contactKey : contactKeys ) {
                fragment.addContact( apiClient.getLatestSharedDataStore( contactKey.getKey() ) );
            }
        }

        if ( index == 2 ) {
            SharesFragment fragment = (SharesFragment) mSectionsPagerAdapter.getItem( index );
            List<SharedKey> sharedKeys = keyStore.getSharedKeys();
            fragment.addShares( sharedKeys );
        }
    }
}
