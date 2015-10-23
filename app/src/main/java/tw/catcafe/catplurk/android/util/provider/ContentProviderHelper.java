package tw.catcafe.catplurk.android.util.provider;

import android.content.UriMatcher;
import android.net.Uri;

import tw.catcafe.catplurk.android.constant.DatabaseConstant;
import tw.catcafe.catplurk.android.provider.CatPlurkDataStore.*;

import static tw.catcafe.catplurk.android.provider.CatPlurkDataStore.AUTHORITY;

/**
 * @author Davy
 */
public class ContentProviderHelper implements DatabaseConstant {
    private static final UriMatcher CONTENT_PROVIDER_URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        CONTENT_PROVIDER_URI_MATCHER.addURI(AUTHORITY, Accounts.CONTENT_PATH, TABLE_ID_ACCOUNTS);
        CONTENT_PROVIDER_URI_MATCHER.addURI(AUTHORITY, Plurks.CONTENT_PATH, TABLE_ID_PLURKS);
        CONTENT_PROVIDER_URI_MATCHER.addURI(AUTHORITY, Users.CONTENT_PATH, TABLE_ID_USERS);
    }
    public static int getTableId(final Uri uri) {
        if (uri == null) return -1;
        return CONTENT_PROVIDER_URI_MATCHER.match(uri);
    }

    public static String getTableNameById(final int id) {
        switch (id) {
            case TABLE_ID_ACCOUNTS:
                return Accounts.TABLE_NAME;
            case TABLE_ID_PLURKS:
                return Plurks.TABLE_NAME;
            case TABLE_ID_USERS:
                return Users.TABLE_NAME;
            default:
                return null;
        }
    }

    public static String getTableNameByUri(final Uri uri) {
        if (uri == null) return null;
        return getTableNameById(getTableId(uri));
    }
}
