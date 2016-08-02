package me.scardy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Objects;

public class PermissionActivity extends PreferenceActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String key;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        PreferenceManager preferenceManager = getPreferenceManager();
        sharedPreferences = preferenceManager.getSharedPreferences();
        editor = sharedPreferences.edit();
        editor.clear().commit();
        Button button = new Button( this );
        Intent intent = getIntent();
        ArrayList<String> permissionsAsStringArray = intent.getStringArrayListExtra( Constants.PERMISSIONS );
        String label = intent.getStringExtra( Constants.LABEL );
        key = intent.getStringExtra( Constants.KEY );
        if ( permissionsAsStringArray != null ) {
            for ( String permissionAsString : permissionsAsStringArray ) {
                editor.putBoolean( permissionAsString, true ).apply();
            }
        }
        if ( label != null || permissionsAsStringArray != null ) {
            button.setText( "Save" );
        } else {
            button.setText( "Share now" );
        }
        button.setOnClickListener( new Button.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent resultIntent = new Intent();
                ArrayList<String> enabledPermissionsAsString = new ArrayList<>();
                if ( sharedPreferences.getBoolean( Permission.PermissionType.FULL_NAME.name(), false ) ) {
                    enabledPermissionsAsString.add( Permission.PermissionType.FULL_NAME.name() );
                }
                if ( sharedPreferences.getBoolean( Permission.PermissionType.ADDRESS.name(), false ) ) {
                    enabledPermissionsAsString.add( Permission.PermissionType.ADDRESS.name() );
                }
                if ( sharedPreferences.getBoolean( Permission.PermissionType.PHONE.name(), false ) ) {
                    enabledPermissionsAsString.add( Permission.PermissionType.PHONE.name() );
                }
                if ( sharedPreferences.getBoolean( Permission.PermissionType.EMAIL.name(), false ) ) {
                    enabledPermissionsAsString.add( Permission.PermissionType.EMAIL.name() );
                }

                resultIntent.putStringArrayListExtra( "permissions", enabledPermissionsAsString );
                resultIntent.putExtra( Constants.LABEL, sharedPreferences.getString( Constants.LABEL, "" ) );
                if ( key != null && !Objects.equals( key, "" ) ) {
                    resultIntent.putExtra( Constants.KEY, key );
                }
                setResult( Activity.RESULT_OK, resultIntent );
                editor.clear().commit();
                finish();
            }
        } );
        setListFooter( button );
        addPreferencesFromResource( R.xml.permissions_preferences );
        if ( label != null ) {
            EditTextPreference labelPreference = (EditTextPreference) findPreference( Constants.LABEL );
            labelPreference.setText( label );
            editor.putString( Constants.LABEL, label ).apply();
        }
    }

    @Override
    protected boolean isValidFragment( String fragmentName ) {
        return true;
    }

}
