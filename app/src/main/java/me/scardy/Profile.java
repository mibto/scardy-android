package me.scardy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Profile {
    private Map<Permission, String> permissions = new HashMap<>();

    public Profile() {
    }

    public Profile( JSONArray profileAsJSON ) throws JSONException {
        for ( int i = 0; i < profileAsJSON.length(); i++ ) {
            JSONObject permissionAsJSON = profileAsJSON.getJSONObject( i );
            String key = permissionAsJSON.keys().next();
            Permission permission = Permission.valueOf( key );
            permission.setValue( permissionAsJSON.getString( key ) );
            permissions.put( permission, permission.getValue() );
        }
    }

    public Map<Permission, String> getPermissions() {
        return permissions;
    }

    public void addPermission( Permission permission ) {
        this.permissions.put( permission, permission.getValue() );
    }

    public void setPermissions( Map<Permission, String> permissions ) {
        this.permissions = permissions;
    }

    public JSONArray toJSON() throws JSONException {
        JSONArray profileAsJSON = new JSONArray();
        for ( Permission permission : permissions.keySet() ) {
            JSONObject permissionAsJSON = new JSONObject();
            profileAsJSON.put( permissionAsJSON.put( permission.name(), permission.getValue() ) );
        }
        return profileAsJSON;
    }
}
