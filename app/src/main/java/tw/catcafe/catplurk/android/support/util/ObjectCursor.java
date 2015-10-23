package tw.catcafe.catplurk.android.support.util;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.util.AbstractList;

/**
 * @author Davy
 */
public class ObjectCursor<E> extends AbstractList<E> {

    private final Cursor mCursor;
    private final CursorIndices<E> mIndices;
    private final SparseArray<E> mCache;

    public ObjectCursor(@NonNull Cursor cursor, @NonNull CursorIndices<E> indies) {
        mCursor = cursor;
        mIndices = indies;
        mCache = new SparseArray<>();
    }

    @Override
    public E get(final int location) {
        ensureCursor();
        final int idxOfCache = mCache.indexOfKey(location);
        if (idxOfCache >= 0) return mCache.valueAt(idxOfCache);
        if (mCursor.moveToPosition(location)) {
            final E object = get(mCursor, mIndices);
            mCache.put(location, object);
            return object;
        }
        throw new ArrayIndexOutOfBoundsException("length=" + mCursor.getCount() + "; index=" + location);
    }

    private void ensureCursor() {
        if (mCursor.isClosed()) throw new IllegalStateException("Cursor is closed");
    }

    protected E get(final Cursor cursor, final CursorIndices<E> indices) {
        return indices.newObject(cursor);
    }

    @Override
    public int size() {
        return mCursor.getCount();
    }

    public boolean isClosed() {
        return mCursor.isClosed();
    }

    public void close() {
        mCursor.close();
    }

    public static abstract class CursorIndices<T> {

        public CursorIndices(@NonNull Cursor cursor) {

        }

        public abstract T newObject(Cursor cursor);
    }
}