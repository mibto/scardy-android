package me.scardy;

public class Permission {
    private String value;
    private PermissionType permissionType;

    public Permission( PermissionType permissionType ) {
        this.permissionType = permissionType;
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public PermissionType getPermission() {
        return permissionType;
    }

    public static Permission valueOf( String permissionAsString ) {
        return new Permission( PermissionType.valueOf( permissionAsString ) );
    }

    public String name() {
        return permissionType.name();
    }

    public enum PermissionType {
        FULL_NAME, ADDRESS, PHONE, EMAIL
    }

    public String toString() {
        return permissionType.name() + " " + value;
    }
}
