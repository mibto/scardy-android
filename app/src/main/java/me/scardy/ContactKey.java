package me.scardy;

import android.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.util.Date;

import static me.scardy.Constants.KEY;
import static me.scardy.Constants.LAST_UPDATE_DATE;

public class ContactKey {
    private SecretKey key;
    private Date lastUpdateDate;

    public ContactKey() {

    }

    public ContactKey( SecretKey key ) {
        this.key = key;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject contactKeyAsJSON = new JSONObject();
        contactKeyAsJSON.put( LAST_UPDATE_DATE, lastUpdateDate.getTime() );
        contactKeyAsJSON.put( KEY, Base64.encodeToString( key.getEncoded(), 0 ) );
        return contactKeyAsJSON;
    }

    public SecretKey getKey() {
        return key;
    }

    public void setKey( SecretKey key ) {
        this.key = key;
    }

    public Date getLastUpdateDate() {
        return new Date( lastUpdateDate.getTime() );
    }

    public void setLastUpdateDate( Date date ) {
        this.lastUpdateDate = new Date( date.getTime() );
    }
}