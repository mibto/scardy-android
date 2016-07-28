package me.scardy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KeyStore {
    private Profile profile = new Profile();
    private List<SharedKey> sharedKeys = new ArrayList<>();
    private List<ContactKey> contactKeys = new ArrayList<>();


    public KeyStore() {

    }

    public KeyStore( JSONObject keyStoreAsJSON ) throws JSONException {
        JSONArray profileAsJSON = keyStoreAsJSON.getJSONArray( Constants.PROFILE );
        JSONArray sharedKeysAsJSON = keyStoreAsJSON.getJSONArray( Constants.SHARED_KEYS );
        JSONArray contactKeys = keyStoreAsJSON.getJSONArray( Constants.CONTACT_KEYS );

        this.profile = new Profile( profileAsJSON );


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
}
