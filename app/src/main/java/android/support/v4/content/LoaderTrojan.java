package android.support.v4.content;

/**
 * @author Davy
 */
public class LoaderTrojan {
    public static <T> boolean isContentChanged(final Loader<T> loader) {
        return loader.mContentChanged;
    }
}
