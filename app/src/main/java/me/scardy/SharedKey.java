package me.scardy;

import android.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.List;

import static me.scardy.Constants.KEY;
import static me.scardy.Constants.LABEL;
import static me.scardy.Constants.PERMISSIONS;

public class SharedKey {
    private String label;
    private List<Permission> permissions;
    private SecretKey key;

    public SharedKey() {
        this.permissions = new ArrayList<>();
    }

    public SharedKey( SecretKey key, String label, List<Permission> permissions ) {
        this.key = key;
        this.label = label;
        this.permissions = permissions;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel( String label ) {
        this.label = label;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void addPermission( Permission permission ) {
        this.permissions.add( permission );
    }

    public void setPermissions( List<Permission> permissions ) {
        this.permissions = permissions;
    }

    public SecretKey getKey() {
        return key;
    }

    public void setKey( SecretKey key ) {
        this.key = key;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject sharedKeyAsJSON = new JSONObject();
        sharedKeyAsJSON.put( LABEL, label );
        JSONArray permissionsAsJSON = new JSONArray();
        for ( Permission permission : permissions ) {
            permissionsAsJSON.put( permission );
        }
        sharedKeyAsJSON.put( PERMISSIONS, permissionsAsJSON );
        sharedKeyAsJSON.put( KEY, Base64.encodeToString( key.getEncoded(), 0 ) );
        return sharedKeyAsJSON;
    }
}
