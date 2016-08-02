package me.scardy;

import android.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.util.HashSet;
import java.util.Set;

import static me.scardy.Constants.KEY;
import static me.scardy.Constants.LABEL;
import static me.scardy.Constants.PERMISSIONS;

public class SharedKey {
    private String label;
    private Set<Permission> permissions;
    private SecretKey key;
    private boolean hasChanged = false;

    public SharedKey() {
        this.permissions = new HashSet<>();
    }

    public SharedKey( SecretKey key, String label, Set<Permission> permissions ) {
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

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public boolean hasPermission( Permission permission ) {
        for ( Permission permission1 : permissions ) {
            if ( permission1.getPermission().equals( permission.getPermission() ) ) {
                return true;
            }
        }
        return false;
    }

    public void addPermission( Permission permission ) {
        this.permissions.add( permission );
    }

    public void setPermissions( Set<Permission> permissions ) {
        this.permissions = permissions;
    }

    public SecretKey getKey() {
        return key;
    }

    public void setKey( SecretKey key ) {
        this.key = key;
    }

    public void setHasChanged() {
        hasChanged = true;
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject sharedKeyAsJSON = new JSONObject();
        sharedKeyAsJSON.put( LABEL, label );
        JSONArray permissionsAsJSON = new JSONArray();
        for ( Permission permission : permissions ) {
            JSONObject permissionAsJSON = new JSONObject();
            permissionsAsJSON.put( permissionAsJSON.put( permission.name(), permission.getValue() ) );
        }
        sharedKeyAsJSON.put( PERMISSIONS, permissionsAsJSON );
        sharedKeyAsJSON.put( KEY, Base64.encodeToString( key.getEncoded(), Base64.NO_WRAP ) );
        return sharedKeyAsJSON;
    }
}
