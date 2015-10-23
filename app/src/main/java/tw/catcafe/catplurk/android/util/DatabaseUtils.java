package tw.catcafe.catplurk.android.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import org.mariotaku.sqliteqb.library.Expression;
import org.mariotaku.sqliteqb.library.SQLFunctions;

import tw.catcafe.catplurk.android.Constants;
import tw.catcafe.catplurk.android.model.ParcelableUser;
import tw.catcafe.catplurk.android.plurkapi.model.User;
import tw.catcafe.catplurk.android.provider.CatPlurkDataStore.*;

/**
 * @author Davy
 */
public class DatabaseUtils implements Constants {
    public static int getPlurkCountInDatabase(final Context context, final Uri uri, final long accountId) {
        if (context == null) return -1;
        final ContentResolver resolver = context.getContentResolver();
        final String where = Expression.equals(Plurks.ACCOUNT_ID, accountId).getSQL();
        final String[] projection = new String[]{SQLFunctions.COUNT()};
        final Cursor cur = ContentResolverUtils.query(resolver, uri, projection, where, null, null);
        if (cur == null) return -1;
        try {
            if (cur.moveToFirst()) {
                return cur.getInt(0);
            }
            return -1;
        } finally {
            cur.close();
        }
    }

    @Nullable
    public static User findUserInDatabase(final Context context, final long accountId,
                                          final long userId) {
        if (context == null) return null;
        final ContentResolver contentResolver = context.getContentResolver();
        User user = null;
        final String where = Expression.and(
                Expression.equals(Users.ACCOUNT_ID, accountId),
                Expression.equals(Users.ID, userId)
        ).getSQL();
        final Cursor cursor = ContentResolverUtils.query(contentResolver, Users.CONETNT_URI,
                Users.COLUMNS, where, null, null);
        if (cursor == null) return null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            user = new ParcelableUser.CursorIndices(cursor).newObject(cursor);
        }
        cursor.close();
        return user;
    }

    public static long[] getNewestPlurkPostIdAndTimeFromDatabase(final Context context, final Uri uri, final long accountId) {
        if (context == null || uri == null || accountId == -1) return new long[] {-1, -1};
        final String[] cols = new String[] {Plurks.PLURK_ID, Plurks.POSTED};
        final ContentResolver resolver = context.getContentResolver();
        final long[] result = new long[] {-1, -1};
        final String where = Expression.equals(Plurks.ACCOUNT_ID, accountId).getSQL();
        final Cursor cursor = ContentResolverUtils.query(resolver, uri, cols, where, null, Plurks.PLURK_ID + " DESC");
        if (cursor == null) {
            return result;
        } else {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                result[0] = cursor.getLong(cursor.getColumnIndexOrThrow(Plurks.PLURK_ID));
                result[1] = cursor.getLong(cursor.getColumnIndexOrThrow(Plurks.POSTED));
            }
            cursor.close();
            return result;
        }
    }

    public static long[] getOldestPlurkPostIdAndTimeFromDatabase(final Context context, final Uri uri, final long accountId) {
        if (context == null || uri == null || accountId == -1) return new long[] {-1, -1};
        final String[] cols = new String[] {Plurks.PLURK_ID, Plurks.POSTED};
        final ContentResolver resolver = context.getContentResolver();
        final long[] result = new long[] {-1, -1};
        final String where = Expression.equals(Plurks.ACCOUNT_ID, accountId).getSQL();
        final Cursor cursor = ContentResolverUtils.query(resolver, uri, cols, where, null, Plurks.PLURK_ID);
        if (cursor == null) {
            return result;
        } else {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                result[0] = cursor.getLong(cursor.getColumnIndexOrThrow(Plurks.PLURK_ID));
                result[1] = cursor.getLong(cursor.getColumnIndexOrThrow(Plurks.POSTED));
            }
            cursor.close();
            return result;
        }
    }
}
