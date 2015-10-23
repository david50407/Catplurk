package tw.catcafe.catplurk.android.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import org.mariotaku.sqliteqb.library.Expression;

import tw.catcafe.catplurk.android.Application;
import tw.catcafe.catplurk.android.Constants;
import tw.catcafe.catplurk.android.util.SQLiteDatabaseWrapper;
import tw.catcafe.catplurk.android.provider.CatPlurkDataStore.*;
import tw.catcafe.catplurk.android.util.imageloader.ImagePreloader;
import tw.catcafe.catplurk.android.util.imageloader.UserProfileImageHelper;

import static tw.catcafe.catplurk.android.util.provider.ContentProviderHelper.getTableId;
import static tw.catcafe.catplurk.android.util.provider.ContentProviderHelper.getTableNameById;

/**
 * @author Davy
 */
public final class CatPlurkDataProvider extends ContentProvider
        implements Constants, SQLiteDatabaseWrapper.LazyLoadCallback {
    private Handler mHandler;
    private ContentResolver mContentResolver;
    private SQLiteDatabaseWrapper mDatabaseWrapper;

    private ImagePreloader mImagePreloader;

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        final Application app = Application.getInstance(context);
        mHandler = new Handler(Looper.getMainLooper());
        mDatabaseWrapper = new SQLiteDatabaseWrapper(this);
        mImagePreloader = new ImagePreloader(context, app.getImageLoader());
        return true;
    }

    @Override
    public SQLiteDatabase onCreateSQLiteDatabase() {
        return Application.getInstance(getContext()).getSQLiteOpenHelper().getWritableDatabase();
    }

    //region Data Queries
    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection,
                        final String[] selectionArgs, final String sortOrder) {
        try {
            final int tableId = getTableId(uri);
            final String table = getTableNameById(tableId);
            // TODO: check permission if DataProvider exported in the future.
            switch (tableId) {
                case VIRTUAL_TABLE_ID_DATABASE_READY: {
                    if (mDatabaseWrapper.isReady())
                        return new MatrixCursor(projection != null ? projection : new String[0]);
                    return null;
                }
            }
            if (table == null) return null;
            final Cursor c = mDatabaseWrapper.query(table, projection, selection, selectionArgs,
                    null, null, sortOrder);
            setNotificationUri(c, getNotificationUri(tableId, uri));
            return c;
        } catch (final SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        try {
            final int tableId = getTableId(uri);
            final String table = getTableNameById(tableId);
            // TODO: check permission if DataProvider exported in the future.
            final long rowId;
            switch (tableId) {
                case TABLE_ID_USERS: {
                    final Expression where = Expression.equals(Users.ID, values.getAsLong(Users.ID));
                    mDatabaseWrapper.update(table, values, where.getSQL(), null);
                    rowId = mDatabaseWrapper.insertWithOnConflict(table, null, values,
                            SQLiteDatabase.CONFLICT_IGNORE);
                    break;
                }
                default: {
                    if (shouldReplaceOnConflict(tableId)) {
                        rowId = mDatabaseWrapper.insertWithOnConflict(table, null, values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                    } else if (table != null) {
                        rowId = mDatabaseWrapper.insert(table, null, values);
                    } else {
                        return null;
                    }
                }
            }
            onDatabaseUpdated(tableId, uri);
            onNewItemsInserted(uri, tableId, values, rowId);
            return Uri.withAppendedPath(uri, String.valueOf(rowId));
        } catch (final SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        try {
            final int tableId = getTableId(uri);
            final String table = getTableNameById(tableId);
            // TODO: check permission if DataProvider exported in the future.
            if (table == null) return 0;
            final int result = mDatabaseWrapper.delete(table, selection, selectionArgs);
            if (result > 0) {
                onDatabaseUpdated(tableId, uri);
            }
            return result;
        } catch (final SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        try {
            final int tableId = getTableId(uri);
            final String table = getTableNameById(tableId);
            // TODO: check permission if DataProvider exported in the future.
            int result = 0;
            if (table != null) {
                result = mDatabaseWrapper.update(table, values, selection, selectionArgs);
            }
            if (result > 0) {
                onDatabaseUpdated(tableId, uri);
            }
            return result;
        } catch (final SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    //endregion Data Queries

    @Override
    public String getType(Uri uri) {
        return null;
    }

    //region ContentObserver
    private void notifyContentObserver(final Uri uri) {
        mHandler.post(() -> {
            final ContentResolver cr = getContentResolver();
            if (uri == null || cr == null) return;
            cr.notifyChange(uri, null);
        });
    }
    //endregion ContentObserver

    //region Database Events
    private void onDatabaseUpdated(final int tableId, final Uri uri) {
        if (uri == null) return;
        switch (tableId) {
            case TABLE_ID_ACCOUNTS: {
                // TODO: Clear accounts
                // clearAccountColor();
                // clearAccountName();
                break;
            }
        }
        notifyContentObserver(getNotificationUri(tableId, uri));
    }

    private void onNewItemsInserted(final Uri uri, final int tableId, final ContentValues values, final long newId) {
        onNewItemsInserted(uri, tableId, new ContentValues[]{values}, new long[]{newId});
    }

    private void onNewItemsInserted(final Uri uri, final int tableId, final ContentValues[] valuesArray, final long[] newIds) {
        final Context context = getContext();
        if (uri == null || valuesArray == null || valuesArray.length == 0 || context == null)
            return;
        preloadImages(valuesArray);
        if (!uri.getBooleanQueryParameter(QUERY_PARAM_NOTIFY, true)) return;
        switch (tableId) {
            case TABLE_ID_PLURKS: {
                // TODO: Notify
//                final AccountPreferences[] prefs = AccountPreferences.getNotificationEnabledPreferences(context,
//                        getAccountIds(context));
//                assert prefs != null;
//                for (final AccountPreferences pref : prefs) {
//                    if (!pref.isHomeTimelineNotificationEnabled()) continue;
//                    showTimelineNotification(pref, getPositionTag(TAB_TYPE_HOME_TIMELINE, pref.getAccountId()));
//                }
//                notifyUnreadCountChanged(NOTIFICATION_ID_HOME_TIMELINE);
                break;
            }
        }
    }
    //endregion Database Events

    //region Helpers
    private ContentResolver getContentResolver() {
        if (mContentResolver != null) return mContentResolver;
        final Context context = getContext();
        return mContentResolver = context.getContentResolver();
    }

    private void setNotificationUri(final Cursor c, final Uri uri) {
        final ContentResolver cr = getContentResolver();
        if (cr == null || c == null || uri == null) return;
        c.setNotificationUri(cr, uri);
    }

    public static Uri getNotificationUri(final int tableId, final Uri def) {
        return def;
    }

    private static boolean shouldReplaceOnConflict(final int table_id) {
        switch (table_id) {
            case TABLE_ID_USERS:
                return true;
        }
        return false;
    }

    private void preloadImages(final ContentValues... values) {
        if (values == null) return;
        for (final ContentValues v : values) {
            // TODO: Can be disabled in Preferences
//            if (mPreferences.getBoolean(KEY_PRELOAD_PROFILE_IMAGES, false)) {
            mImagePreloader.preloadImage(UserProfileImageHelper.getUrl(v));
//            }
            // TODO: Can be enabled in Preferences
//            if (mPreferences.getBoolean(KEY_PRELOAD_PREVIEW_IMAGES, false)) {
//                final String textHtml = v.getAsString(Statuses.TEXT_HTML);
//                for (final String link : MediaPreviewUtils.getSupportedLinksInStatus(textHtml)) {
//                    mImagePreloader.preloadImage(link);
//                }
//            }
        }
    }
    //endregion Helpers
}
