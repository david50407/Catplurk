package tw.catcafe.catplurk.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import tw.catcafe.catplurk.android.Constants;
import tw.catcafe.catplurk.android.PlurkDetailActivity;
import tw.catcafe.catplurk.android.activity.AccountLoginActivity;
import tw.catcafe.catplurk.android.model.ParcelablePlurk;

import static android.accounts.AccountManager.KEY_INTENT;

/**
 * @author Davy
 */
public class OpenIntentUtil implements Constants {
    //region Account Login
    /**
     * Open Account Login Activity to let user add account.
     * @see tw.catcafe.catplurk.android.activity.AccountLoginActivity
     */
    public static void openAccountLogin(final Context context) {
        openAccountLogin(context, null);
    }

    /**
     * Open Account Login Activity to let user add account.
     * @see tw.catcafe.catplurk.android.activity.AccountLoginActivity
     */
    public static void openAccountLogin(final Context context, Bundle activityOptions) {
        if (context == null) return;
        final Intent intent = new Intent(context, AccountLoginActivity.class);
        intent.setAction(INTENT_ACTION_ACCOUNT_LOGIN);
        ActivityCompat.startActivity((Activity) context, intent, activityOptions);
    }
    //endregion Account Login

//    public static void openPlurk(final Context context, final long accountId, final long statusId) {
//        if (context == null || accountId <= 0 || statusId <= 0) return;
//        final Uri uri = LinkCreator.getTwidereStatusLink(accountId, statusId);
//        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        context.startActivity(intent);
//    }

    public static void openPlurk(final Context context, final ParcelablePlurk parcelablePlurk, Bundle activityOptions) {
        if (context == null || parcelablePlurk == null) return;
        final long account_id = parcelablePlurk.accountId, plurk_id = parcelablePlurk.plurkId;
        final Bundle extras = new Bundle();
        extras.putParcelable(EXTRA_PLURK, parcelablePlurk);
        // TODO: Maybe use Uri
//        final Uri.Builder builder = new Uri.Builder();
//        builder.scheme(SCHEME_CATPLURK);
//        builder.authority(AUTHORITY_PLURK);
//        builder.appendQueryParameter(QUERY_PARAM_ACCOUNT_ID, String.valueOf(account_id));
//        builder.appendQueryParameter(QUERY_PARAM_PLURK_ID, String.valueOf(plurk_id));
//        final Intent intent = new Intent(Intent.ACTION_VIEW, builder.build());
//        intent.setExtrasClassLoader(context.getClassLoader());
//        intent.putExtras(extras);
//        if (context instanceof Activity) {
//            ActivityCompat.startActivity((Activity) context, intent, activityOptions);
//        } else {
//            context.startActivity(intent);
//        }
        final Intent intent = new Intent(context, PlurkDetailActivity.class);
        intent.putExtras(extras);
        ActivityCompat.startActivity((Activity) context, intent, activityOptions);
    }
}
