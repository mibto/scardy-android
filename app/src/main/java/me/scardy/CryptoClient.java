package me.scardy;

import android.util.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static me.scardy.Constants.ENCRYPTION_MODE;

/**
 * Created by MB on 28.07.2016.
 * for android
 */
public class CryptoClient {

    private SecretKey masterKey;
    private Cipher cipher;

    public CryptoClient( SecretKey masterKey ) {
        this.masterKey = masterKey;
    }

    public CryptoClient() {

    }

    public String encrypt( SecretKey key, String raw ) {
        try {
            cipher = Cipher.getInstance( ENCRYPTION_MODE );
            cipher.init( Cipher.ENCRYPT_MODE, key );
            byte[] iv = cipher.getIV();
            byte[] encrypted = cipher.doFinal( raw.getBytes() );
            return Base64.encodeToString( ArrayUtils.addAll( iv, encrypted ), Base64.URL_SAFE );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt( SecretKey key, String encodedAndEncrypted ) {
        try {
            byte[] ivAndEncrypted = Base64.decode( encodedAndEncrypted, Base64.URL_SAFE );
            byte[] iv = Arrays.copyOfRange( ivAndEncrypted, 0, 16 );
            byte[] encrypted = Arrays.copyOfRange( ivAndEncrypted, 16, ivAndEncrypted.length );

            cipher = Cipher.getInstance( ENCRYPTION_MODE );
            cipher.init( Cipher.DECRYPT_MODE, key, new IvParameterSpec( iv ) );
            return new String( cipher.doFinal( encrypted ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public String decryptKeyStore( String encryptedKeyStoreAsString ) throws JSONException {
        return decrypt( masterKey, encryptedKeyStoreAsString );
    }

    public String encryptKeyStore( String keyStoreAsString ) {
        return encrypt( masterKey, keyStoreAsString );
    }

    public String getHash( byte[] key ) {
        try {
            MessageDigest md = MessageDigest.getInstance( "SHA-256" );
            md.update( key );
            byte[] digest = md.digest();
            return Base64.encodeToString( digest, Base64.URL_SAFE ).replace( "/", "S" );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveMasterKey( SecretKey masterKey, SecretKey androidMasterKey, FileOutputStream out ) {
        try {
            out.write( encrypt( androidMasterKey, new String( masterKey.getEncoded() ) ).getBytes() );
            out.close();

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void saveMasterKeyHash( SecretKey masterKey, FileOutputStream out ) {
        try {
            String masterKeyHash = getHash( masterKey.getEncoded() );
            out.write( masterKeyHash.getBytes() );
            out.close();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public SecretKey getMasterKey( FileInputStream in ) {
        try {
            byte[] ivAndEncryptedKey = new byte[ 64 ];
            in.read( ivAndEncryptedKey );
            in.close();

            if ( masterKey != null ) {
                String decryptedKey = decrypt( masterKey, new String( ivAndEncryptedKey ) );
                return getKeyFromString( decryptedKey );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }


    public SecretKey generateAESKey() {
        try {
            KeyGenerator keyGen;
            keyGen = KeyGenerator.getInstance( "AES" );
            keyGen.init( 256 ); //256 bit AES Key
            return keyGen.generateKey();
        } catch ( NoSuchAlgorithmException e ) {
            e.printStackTrace();
        }
        return null;
    }

    public SecretKey getKeyFromString( String keyAsString ) {
        return new SecretKeySpec( Base64.decode( keyAsString, Base64.URL_SAFE ), 0, 32, "AES" );
    }

    public String getStringFromKey( SecretKey key ) {
        return Base64.encodeToString( key.getEncoded(), Base64.URL_SAFE );
    }
}
