package tw.catcafe.catplurk.android.util;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;

import java.util.Collection;

import static android.text.TextUtils.isEmpty;

/**
 * @author Davy
 */
public class ContentResolverUtils {
    public static final int MAX_BULK_COUNT = 128;

    public static <T> int bulkDelete(final ContentResolver resolver, final Uri uri,
                                     final String inColumn, final Collection<T> colValues,
                                     final String extraWhere, final boolean valuesIsString) {
        if (colValues == null) return 0;
        return bulkDelete(resolver, uri, inColumn, colValues.toArray(), extraWhere, valuesIsString);
    }

    public static <T> int bulkDelete(final ContentResolver resolver, final Uri uri,
                                     final String inColumn, final T[] colValues,
                                     final String extraWhere, final boolean valuesIsString) {
        if (resolver == null || uri == null || isEmpty(inColumn) || colValues == null || colValues.length == 0)
            return 0;
        final int col_values_length = colValues.length, blocks_count = col_values_length / MAX_BULK_COUNT + 1;
        int rows_deleted = 0;
        for (int i = 0; i < blocks_count; i++) {
            final int start = i * MAX_BULK_COUNT, end = Math.min(start + MAX_BULK_COUNT, col_values_length);
            final String[] block = ArrayUtils.toStringArray(ArrayUtils.subArray(colValues, start, end));
            if (valuesIsString) {
                final StringBuilder where = new StringBuilder(inColumn + " IN(" + ArrayUtils.toStringForSQL(block)
                        + ")");
                if (!isEmpty(extraWhere)) {
                    where.append("AND ").append(extraWhere);
                }
                rows_deleted += resolver.delete(uri, where.toString(), block);
            } else {
                final StringBuilder where = new StringBuilder(inColumn + " IN("
                        + ArrayUtils.toString(block, ',', true) + ")");
                if (!isEmpty(extraWhere)) {
                    where.append("AND ").append(extraWhere);
                }
                rows_deleted += resolver.delete(uri, where.toString(), null);
            }
        }
        return rows_deleted;
    }

    public static int bulkInsert(final ContentResolver resolver, final Uri uri,
                                 final Collection<ContentValues> values) {
        if (values == null) return 0;
        return bulkInsert(resolver, uri, values.toArray(new ContentValues[values.size()]));
    }

    public static int bulkInsert(final ContentResolver resolver, final Uri uri,
                                 final ContentValues[] values) {
        if (resolver == null || uri == null || values == null || values.length == 0) return 0;
        final int colValuesLength = values.length, blocksCount = colValuesLength / MAX_BULK_COUNT + 1;
        int rowsInserted = 0;
        for (int i = 0; i < blocksCount; i++) {
            final int start = i * MAX_BULK_COUNT, end = Math.min(start + MAX_BULK_COUNT, colValuesLength);
            final ContentValues[] block = new ContentValues[end - start];
            System.arraycopy(values, start, block, 0, end - start);
            rowsInserted += resolver.bulkInsert(uri, block);
        }
        return rowsInserted;
    }

    public static Cursor query(final ContentResolver resolver, final Uri uri,
                               final String[] projection, final String selection,
                               final String[] selectionArgs, final String sortOrder) {
        return resolver.query(uri, projection, selection, selectionArgs, sortOrder, null);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static Cursor query(final ContentResolver resolver, final Uri uri,
                               final String[] projection, final String selection,
                               final String[] selectionArgs, final String sortOrder,
                               final CancellationSignal cancellationSignal) {
        return resolver.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
    }
}
