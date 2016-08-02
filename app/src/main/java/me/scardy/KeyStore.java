package me.scardy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class KeyStore {
    private Profile profile = new Profile();
    private List<SharedKey> sharedKeys = new ArrayList<>();
    private List<ContactKey> contactKeys = new ArrayList<>();


    public KeyStore() {

    }

    public KeyStore( JSONObject keyStoreAsJSON ) throws JSONException {
        JSONArray profileAsJSON = keyStoreAsJSON.getJSONArray( Constants.PROFILE );
        JSONArray sharedKeysAsJSON = keyStoreAsJSON.getJSONArray( Constants.SHARED_KEYS );
        JSONArray contactKeysAsJSON = keyStoreAsJSON.getJSONArray( Constants.CONTACT_KEYS );

        this.profile = new Profile( profileAsJSON );

        for ( int i = 0; i < sharedKeysAsJSON.length(); i++ ) {
            JSONObject sharedKeyAsJSON = sharedKeysAsJSON.getJSONObject( i );
            SecretKey sharedKey = new CryptoClient().getKeyFromString( sharedKeyAsJSON.getString( Constants.KEY ) );
            Set<Permission> permissions = new HashSet<>();
            JSONArray permissionsAsJSON = sharedKeyAsJSON.getJSONArray( Constants.PERMISSIONS );
            for ( int k = 0; k < permissionsAsJSON.length(); k++ ) {
                Permission permission = Permission.valueOf( permissionsAsJSON.getJSONObject( k ).keys().next() );
                permission.setValue( permissionsAsJSON.getJSONObject( k ).getString( permission.name() ) );
                permissions.add( permission );
            }
            sharedKeys.add( new SharedKey( sharedKey, sharedKeyAsJSON.optString( Constants.LABEL ), permissions ) );
        }

        for ( int i = 0; i < contactKeysAsJSON.length(); i++ ) {
            JSONObject contactKeyAsJSON = contactKeysAsJSON.getJSONObject( i );
            SecretKey contactKey = new CryptoClient().getKeyFromString( contactKeyAsJSON.getString( Constants.KEY ) );
            contactKeys.add( new ContactKey( contactKey ) );
        }

    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile( Profile profile ) {
        this.profile = profile;
    }

    public List<SharedKey> getSharedKeys() {
        return sharedKeys;
    }

    public SharedKey getSharedKey( SecretKey key ) {
        for ( SharedKey sharedKey : sharedKeys ) {
            if ( Arrays.equals( sharedKey.getKey().getEncoded(), key.getEncoded() ) ) {
                return sharedKey;
            }
        }
        return null;
    }

    public void replaceSharedKey( SharedKey sharedKeyNew ) {
        Iterator<SharedKey> iterator = sharedKeys.iterator();

        while ( iterator.hasNext() ) {
            SharedKey sharedKeyOld = iterator.next();
            if ( sharedKeyNew.getKey().equals( sharedKeyOld.getKey() ) ) {
                iterator.remove();
            }
        }
        sharedKeys.add( sharedKeyNew );
    }

    public void setSharedKeys( List<SharedKey> sharedKeys ) {
        this.sharedKeys = sharedKeys;
    }

    public void addSharedKey( SharedKey sharedKey ) {
        this.sharedKeys.add( sharedKey );
    }

    public void addContactKey( ContactKey contactKey ) {
        this.contactKeys.add( contactKey );
    }

    public List<ContactKey> getContactKeys() {
        return contactKeys;
    }

    public void setContactKeys( List<ContactKey> contactKeys ) {
        this.contactKeys = contactKeys;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject keyStoreAsJSON = new JSONObject();
        keyStoreAsJSON.put( Constants.PROFILE, profile.toJSON() );
        JSONArray sharedKeysAsJSON = new JSONArray();
        JSONArray contactKeysAsJSON = new JSONArray();
        for ( SharedKey sharedKey : sharedKeys ) {
            sharedKeysAsJSON.put( sharedKey.toJSON() );
        }
        keyStoreAsJSON.put( Constants.SHARED_KEYS, sharedKeysAsJSON );
        for ( ContactKey contactKey : contactKeys ) {
            contactKeysAsJSON.put( contactKey.toJSON() );
        }
        keyStoreAsJSON.put( Constants.CONTACT_KEYS, contactKeysAsJSON );
        return keyStoreAsJSON;
    }

    public List<SharedKey> getChangedSharedKeys() {
        List<SharedKey> changedSharedKeys = new ArrayList<>();
        for ( SharedKey sharedKey : sharedKeys ) {
            if ( sharedKey.hasChanged() ) {
                changedSharedKeys.add( sharedKey );
            }
        }
        return changedSharedKeys;
    }
}
