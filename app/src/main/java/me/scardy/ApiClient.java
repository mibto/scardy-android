package me.scardy;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MB on 25.07.2016.
 * for android
 */
public class ApiClient {
    private HttpURLConnection connection = null;
    private URL url;
    private String response = null;
    private JSONObject bodyAsJSON = new JSONObject();
    private InputStream in;
    private DataOutputStream out;
    private String masterKeyHash;
    private CryptoClient cryptoClient;

    public ApiClient( SecretKey masterKey, String masterKeyHash ) {
        this.masterKeyHash = masterKeyHash;
        this.cryptoClient = new CryptoClient( masterKey );
    }

    public String getKeyStore( String id, Date version ) {
        try {
            url = new URL( "http://192.168.0.66:8080/api/key-stores/" + id + "?version=" + version.getTime() );
            connection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream( connection.getInputStream() );
            response = IOUtils.toString( in, "UTF-8" );

            if ( connection.getResponseCode() != 200 ) {
                connection.disconnect();
                return null;
            } else {
                connection.disconnect();
                return response;
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( connection != null ) {
                connection.disconnect();
            }
        }
        return response;
    }

    public KeyStore getLatestKeyStore() {
        List<Date> versions = getKeyStoreVersions();
        try {
            JSONObject response = new JSONObject( getKeyStore( masterKeyHash, versions.get( versions.size() - 1 ) ) );
            JSONObject keyStoreAsJSON = new JSONObject( cryptoClient.decryptKeyStore( response.getString( "encryptedData" ) ) );
            return new me.scardy.KeyStore( keyStoreAsJSON );
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSharedDataStore( SecretKey sharedKey, Date version ) {
        try {
            url = new URL( "http://192.168.0.66:8080/api/data-stores/" + cryptoClient.getHash( sharedKey.getEncoded() ) + "?version=" + version.getTime() );
            connection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream( connection.getInputStream() );
            response = IOUtils.toString( in, "UTF-8" );

            if ( connection.getResponseCode() != 200 ) {
                connection.disconnect();
                return null;
            } else {
                connection.disconnect();
                return response;
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( connection != null ) {
                connection.disconnect();
            }
        }
        return response;
    }

    public Profile getLatestSharedDataStore( SecretKey sharedKey ) {
        List<Date> versions = getSharedDataStoreVersions( sharedKey );
        try {
            JSONObject response = new JSONObject( getSharedDataStore( sharedKey, versions.get( versions.size() - 1 ) ) );
            JSONObject dataStoreAsJSON = new JSONObject( cryptoClient.decrypt( sharedKey, response.getString( "encryptedData" ) ) );
            Profile profile = new Profile( dataStoreAsJSON.getJSONArray( Constants.PERMISSIONS ) );
            return profile;
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createKeyStore( KeyStore keyStore ) {
        try {
            bodyAsJSON.put( "id", masterKeyHash );
            bodyAsJSON.put( "encryptedData", cryptoClient.encryptKeyStore( keyStore.toJSON().toString() ) );

            url = new URL( "http://192.168.0.66:8080/api/key-stores/" );
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod( "POST" );
            connection.setDoOutput( true );
            connection.setRequestProperty( "Content-Type", "application/json" );

            out = new DataOutputStream( connection.getOutputStream() );
            out.writeBytes( bodyAsJSON.toString() );
            out.flush();
            out.close();

            in = new BufferedInputStream( connection.getInputStream() );

            if ( connection.getResponseCode() != 200 ) {
                connection.disconnect();
                return false;
            } else {
                connection.disconnect();
                return true;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            if ( connection != null ) {
                connection.disconnect();
            }
        }
        return true;
    }

    public boolean updateKeyStore( KeyStore keyStore ) {
        try {
            bodyAsJSON.put( "encryptedData", cryptoClient.encryptKeyStore( keyStore.toJSON().toString() ) );

            url = new URL( "http://192.168.0.66:8080/api/key-stores/" + masterKeyHash );
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod( "PUT" );
            connection.setDoOutput( true );
            connection.setRequestProperty( "Content-Type", "application/json" );

            out = new DataOutputStream( connection.getOutputStream() );
            out.writeBytes( bodyAsJSON.toString() );
            out.flush();
            out.close();

            in = new BufferedInputStream( connection.getInputStream() );

            if ( connection.getResponseCode() != 200 ) {
                connection.disconnect();
                return false;
            } else {
                connection.disconnect();
                return true;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            if ( connection != null ) {
                connection.disconnect();
            }
        }
        return false;
    }

    public boolean createSharedDataStore( SharedKey sharedProfile ) {
        try {
            bodyAsJSON.put( "id", cryptoClient.getHash( sharedProfile.getKey().getEncoded() ) );
            bodyAsJSON.put( "admin", masterKeyHash );
            bodyAsJSON.put( "encryptedData", cryptoClient.encrypt( sharedProfile.getKey(), sharedProfile.toJSON().toString() ) );

            url = new URL( "http://192.168.0.66:8080/api/data-stores/" );
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod( "POST" );
            connection.setDoOutput( true );
            connection.setRequestProperty( "Content-Type", "application/json" );

            out = new DataOutputStream( connection.getOutputStream() );
            out.writeBytes( bodyAsJSON.toString() );
            out.flush();
            out.close();

            in = new BufferedInputStream( connection.getInputStream() );

            if ( connection.getResponseCode() != 200 ) {
                connection.disconnect();
                return false;
            } else {
                connection.disconnect();
                return true;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            if ( connection != null ) {
                connection.disconnect();
            }
        }
        return false;
    }

    public boolean updateSharedDataStore( SharedKey sharedProfile ) {
        try {
            bodyAsJSON.put( "admin", masterKeyHash );
            bodyAsJSON.put( "encryptedData", cryptoClient.encrypt( sharedProfile.getKey(), sharedProfile.toJSON().toString() ) );

            url = new URL( "http://192.168.0.66:8080/api/data-stores/" + cryptoClient.getHash( sharedProfile.getKey().getEncoded() ) );
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod( "PUT" );
            connection.setDoOutput( true );
            connection.setRequestProperty( "Content-Type", "application/json" );

            out = new DataOutputStream( connection.getOutputStream() );
            out.writeBytes( bodyAsJSON.toString() );
            out.flush();
            out.close();

            in = new BufferedInputStream( connection.getInputStream() );

            if ( connection.getResponseCode() != 200 ) {
                connection.disconnect();
                return false;
            } else {
                connection.disconnect();
                return true;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            if ( connection != null ) {
                connection.disconnect();
            }
        }
        return false;
    }

    public List<Date> getKeyStoreVersions() {
        try {
            url = new URL( "http://192.168.0.66:8080/api/key-stores/" + masterKeyHash + "/versions" );
            connection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream( connection.getInputStream() );
            JSONArray versionsAsJSON = new JSONArray( IOUtils.toString( in, "UTF-8" ) );
            List<Date> versions = new ArrayList<>();
            for ( int i = 0; i < versionsAsJSON.length(); i++ ) {
                versions.add( new Date( (Long) versionsAsJSON.get( i ) ) );
            }
            if ( connection.getResponseCode() != 200 ) {
                connection.disconnect();
                return null;
            } else {
                connection.disconnect();
                return versions;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            if ( connection != null ) {
                connection.disconnect();
            }
        }
        return null;
    }

    public List<Date> getSharedDataStoreVersions( SecretKey sharedKey ) {
        try {
            url = new URL( "http://192.168.0.66:8080/api/data-stores/" + cryptoClient.getHash( sharedKey.getEncoded() ) + "/versions" );
            connection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream( connection.getInputStream() );
            JSONArray versionsAsJSON = new JSONArray( IOUtils.toString( in, "UTF-8" ) );
            List<Date> versions = new ArrayList<>();
            for ( int i = 0; i < versionsAsJSON.length(); i++ ) {
                versions.add( new Date( (Long) versionsAsJSON.get( i ) ) );
            }
            if ( connection.getResponseCode() != 200 ) {
                connection.disconnect();
                return null;
            } else {
                connection.disconnect();
                return versions;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            if ( connection != null ) {
                connection.disconnect();
            }
        }
        return null;
    }
}
