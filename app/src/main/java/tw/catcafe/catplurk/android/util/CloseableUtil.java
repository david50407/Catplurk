package tw.catcafe.catplurk.android.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Davy
 */
public class CloseableUtil {
    public static boolean closeSilently(final Closeable c) {
        if (c == null) return false;
        try {
            c.close();
        } catch (final IOException e) {
            return false;
        }
        return true;
    }
}
