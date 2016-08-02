package me.scardy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Profile {
    private Map<Permission.PermissionType, Permission> permissions = new HashMap<>();

    public Profile() {
    }

    public Profile( JSONArray profileAsJSON ) throws JSONException {
        for ( int i = 0; i < profileAsJSON.length(); i++ ) {
            JSONObject permissionAsJSON = profileAsJSON.getJSONObject( i );
            String key = permissionAsJSON.keys().next();
            Permission permission = new Permission( Permission.PermissionType.valueOf( key ) );
            permission.setValue( permissionAsJSON.getString( key ) );
            permissions.put( permission.getPermission(), permission );
        }
    }

    public Map<Permission.PermissionType, Permission> getPermissions() {
        return permissions;
    }

    public void addPermission( Permission permission ) {
        this.permissions.put( permission.getPermission(), permission );
    }

    public void setPermissions( Map<Permission.PermissionType, Permission> permissions ) {
        this.permissions = permissions;
    }

    public Permission getPermission( Permission.PermissionType permissionType ) {
        return permissions.get( permissionType );
    }

    public JSONArray toJSON() throws JSONException {
        JSONArray profileAsJSON = new JSONArray();
        for ( Permission permission : permissions.values() ) {
            JSONObject permissionAsJSON = new JSONObject();
            profileAsJSON.put( permissionAsJSON.put( permission.name(), permission.getValue() ) );
        }
        return profileAsJSON;
    }
}
