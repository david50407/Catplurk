package tw.catcafe.catplurk.android.util;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import tw.catcafe.catplurk.android.constant.CatPlurkConstant;

import static android.text.TextUtils.isEmpty;

/**
 * @author Davy
 */
public class ParseUtil {
    //region String
    @Deprecated
    public static String parseString(final String object) {
        return object;
    }

    public static String parseString(final Object object) {
        return parseString(object, null);
    }

    public static String parseString(final Object object, final String def) {
        if (object == null) return def;
        return String.valueOf(object);
    }
    //endregion String

    //region JSON and Bundle
    public static String bundleToJSON(final Bundle args) {
        final Set<String> keys = args.keySet();
        final JSONObject json = new JSONObject();
        for (final String key : keys) {
            final Object value = args.get(key);
            if (value == null) {
                continue;
            }
            try {
                if (value instanceof Boolean) {
                    json.put(key, args.getBoolean(key));
                } else if (value instanceof Integer) {
                    json.put(key, args.getInt(key));
                } else if (value instanceof Long) {
                    json.put(key, args.getLong(key));
                } else if (value instanceof String) {
                    json.put(key, args.getString(key));
                } else {
                    Log.w(CatPlurkConstant.LOGTAG, "Unknown type " + value.getClass().getSimpleName() + " in arguments key " + key);
                }
            } catch (final JSONException e) {
                e.printStackTrace();
            }
        }
        return json.toString();
    }

    public static Bundle jsonToBundle(final String string) {
        final Bundle bundle = new Bundle();
        if (string == null) return bundle;
        try {
            final JSONObject json = new JSONObject(string);
            final Iterator<?> it = json.keys();
            while (it.hasNext()) {
                final Object key_obj = it.next();
                if (key_obj == null) {
                    continue;
                }
                final String key = key_obj.toString();
                final Object value = json.get(key);
                if (value instanceof Boolean) {
                    bundle.putBoolean(key, json.optBoolean(key));
                } else if (value instanceof Integer) {
                    // Simple workaround for account_id
//                    if (shouldPutLong(key)) {
//                        bundle.putLong(key, json.optLong(key));
//                    } else {
                        bundle.putInt(key, json.optInt(key));
//                    }
                } else if (value instanceof Long) {
                    bundle.putLong(key, json.optLong(key));
                } else if (value instanceof String) {
                    bundle.putString(key, json.optString(key));
                } else {
                    Log.w(CatPlurkConstant.LOGTAG, "Unknown type " + value.getClass().getSimpleName() + " in arguments key " + key);
                }
            }
        } catch (final JSONException e) {
            e.printStackTrace();
        } catch (final ClassCastException e) {
            e.printStackTrace();
        }
        return bundle;
    }
    //endregion region JSON and Bundle

    //region Numbers
    public static double parseDouble(final String source) {
        return parseDouble(source, -1);
    }

    public static double parseDouble(final String source, final double def) {
        if (source == null) return def;
        try {
            return Double.parseDouble(source);
        } catch (final NumberFormatException e) {
            // Wrong number format? Ignore them.
        }
        return def;
    }

    public static float parseFloat(final String source) {
        return parseFloat(source, -1);
    }

    public static float parseFloat(final String source, final float def) {
        if (source == null) return def;
        try {
            return Float.parseFloat(source);
        } catch (final NumberFormatException e) {
            // Wrong number format? Ignore them.
        }
        return def;
    }

    public static int parseInt(final String source) {
        return parseInt(source, -1);
    }

    public static int parseInt(final String source, final int def) {
        if (source == null) return def;
        try {
            return Integer.valueOf(source);
        } catch (final NumberFormatException e) {
            // Wrong number format? Ignore them.
        }
        return def;
    }

    public static long parseLong(final String source) {
        return parseLong(source, -1);
    }

    public static long parseLong(final String source, final long def) {
        if (source == null) return def;
        try {
            return Long.parseLong(source);
        } catch (final NumberFormatException e) {
            // Wrong number format? Ignore them.
        }
        return def;
    }
    //endregion Numbers

    //region URI
    public static URI parseURI(final String uriString) {
        if (uriString == null) return null;
        try {
            return new URI(uriString);
        } catch (final URISyntaxException e) {
            // This should not happen.
        }
        return null;
    }

    public static URL parseURL(final String urlString) {
        if (urlString == null) return null;
        try {
            return new URL(urlString);
        } catch (final MalformedURLException e) {
            // This should not happen.
        }
        return null;
    }
    //endregion URI
}
