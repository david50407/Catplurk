package tw.catcafe.catplurk.android.util;

import android.net.Uri;

/**
 * @author Davy
 */
public class UriUtils {
    public static Uri appendQueryParameters(final Uri uri, final String key, long value) {
        return appendQueryParameters(uri, key, ParseUtil.parseString(value));
    }

    public static Uri appendQueryParameters(final Uri uri, final String key, final boolean value) {
        return appendQueryParameters(uri, key, ParseUtil.parseString(value));
    }

    public static Uri appendQueryParameters(final Uri uri, final String key, String value) {
        final Uri.Builder builder = uri.buildUpon();
        builder.appendQueryParameter(key, value);
        return builder.build();
    }
}
