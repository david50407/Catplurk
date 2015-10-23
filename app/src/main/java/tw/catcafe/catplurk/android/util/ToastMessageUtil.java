package tw.catcafe.catplurk.android.util;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import javax.net.ssl.SSLException;

import tw.catcafe.catplurk.android.R;
import tw.catcafe.catplurk.android.plurkapi.PlurkException;

import static android.text.TextUtils.isEmpty;

/**
 * @author Davy
 */
public final class ToastMessageUtil {
    // region Plurk Error
    public static String getPlurkErrorMessage(final Context context, final CharSequence action,
                                                final PlurkException e) {
        if (context == null) return null;
        if (e == null) return context.getString(R.string.error_unknown_error);
        if (e.getCause() instanceof SSLException) {
            final String msg = e.getCause().getMessage();
            if (msg != null && msg.contains("!="))
                return getErrorMessage(context, action, context.getString(R.string.error_ssl_error));
            else
                return getErrorMessage(context, action, context.getString(R.string.error_network_error));
        } else if (e.getCause() instanceof IOException)
            return getErrorMessage(context, action, context.getString(R.string.error_network_error));
        else if (e.getCause() instanceof JSONException)
            return getErrorMessage(context, action, context.getString(R.string.error_api_data_corrupted));
        else
            return getErrorMessage(context, action, trimLineBreak(e.getMessage()));
    }

    public static String getPlurkErrorMessage(final Context context, final PlurkException e) {
        if (e == null) return null;
        return e.getMessage();
    }
    public static void showPlurkErrorMessage(final Context context, final CharSequence action,
                                               final PlurkException te, final boolean long_message) {
        if (context == null) return;
        final String message;
        if (te != null) {
            if (action != null) {
                if (te.getCause() instanceof SSLException) {
                    final String msg = te.getCause().getMessage();
                    if (msg != null && msg.contains("!=")) {
                        message = context.getString(R.string.format_error_action_message, action,
                                context.getString(R.string.error_ssl_error));
                    } else {
                        message = context.getString(R.string.format_error_action_message, action,
                                context.getString(R.string.error_network_error));
                    }
                } else if (te.getCause() instanceof IOException) {
                    message = context.getString(R.string.format_error_action_message, action,
                            context.getString(R.string.error_network_error));
                } else {
                    message = context.getString(R.string.format_error_action_message, action,
                            trimLineBreak(te.getMessage()));
                }
            } else {
                message = context.getString(R.string.format_error_message, trimLineBreak(te.getMessage()));
            }
        } else {
            message = context.getString(R.string.error_unknown_error);
        }
        showErrorMessage(context, message, long_message);
    }
    // endregion Plurk Error

    // region Error
    public static String getErrorMessage(final Context context, final CharSequence message) {
        if (context == null) return ParseUtil.parseString(message);
        if (isEmpty(message)) return context.getString(R.string.error_unknown_error);
        return context.getString(R.string.format_error_message, message);
    }

    public static String getErrorMessage(final Context context, final CharSequence action, final CharSequence message) {
        if (context == null || isEmpty(action)) return ParseUtil.parseString(message);
        if (isEmpty(message)) return context.getString(R.string.error_unknown_error);
        return context.getString(R.string.format_error_action_message, action, message);
    }

    public static String getErrorMessage(final Context context, final CharSequence action, final Throwable t) {
        if (context == null) return null;
        if (t instanceof PlurkException)
            return getPlurkErrorMessage(context, action, (PlurkException) t);
        else if (t != null) return getErrorMessage(context, trimLineBreak(t.getMessage()));
        return context.getString(R.string.error_unknown_error);
    }

    public static String getErrorMessage(final Context context, final Throwable t) {
        if (t == null) return null;
        if (context != null && t instanceof PlurkException)
            return getPlurkErrorMessage(context, (PlurkException) t);
        return t.getMessage();
    }

    public static void showErrorMessage(final Context context, final CharSequence message, final boolean longMessage) {
        if (context == null) return;
        final Toast toast = Toast.makeText(context, message, longMessage ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showErrorMessage(final Context context, final CharSequence action,
                                        final CharSequence message, final boolean longMessage) {
        if (context == null) return;
        showErrorMessage(context, getErrorMessage(context, message), longMessage);
    }

    public static void showErrorMessage(final Context context, final CharSequence action,
                                        final Throwable t, final boolean longMessage) {
        if (context == null) return;
        if (t instanceof PlurkException) {
            showPlurkErrorMessage(context, action, (PlurkException) t, longMessage);
            return;
        }
        showErrorMessage(context, getErrorMessage(context, action, t), longMessage);
    }

    public static void showErrorMessage(final Context context, final int actionRes, final String desc,
                                        final boolean longMessage) {
        if (context == null) return;
        showErrorMessage(context, context.getString(actionRes), desc, longMessage);
    }

    public static void showErrorMessage(final Context context, final int action, final Throwable t,
                                        final boolean long_message) {
        if (context == null) return;
        showErrorMessage(context, context.getString(action), t, long_message);
    }
    // endregion Error

    //reigon Info
    public static void showInfoMessage(final Context context, final int resId, final boolean long_message) {
        if (context == null) return;
        showInfoMessage(context, context.getText(resId), long_message);
    }

    public static void showInfoMessage(final Context context, final CharSequence message, final boolean long_message) {
        if (context == null || isEmpty(message)) return;
        final Toast toast = Toast.makeText(context, message, long_message ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.show();
    }
    //endregion Info

    // region Helper
    public static String trim(final String str) {
        return str != null ? str.trim() : null;
    }

    public static String trimLineBreak(final String orig) {
        if (orig == null) return null;
        return orig.replaceAll("\\n+", "\n");
    }
    // endregion Helper
}
