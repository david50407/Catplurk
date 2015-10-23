package tw.catcafe.catplurk.android.service;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import tw.catcafe.catplurk.android.activity.AccountLoginActivity;
import tw.catcafe.catplurk.android.constant.IntentConstant;
import tw.catcafe.catplurk.android.util.AccountManager;

import static android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT;
import static android.accounts.AccountManager.KEY_INTENT;

/**
 * @author Davy
 */
public class AccountAuthenticatorService extends Service {
    private static AccountAuthenticator sAccountAuthenticator = null;

    @Override
    public IBinder onBind(Intent intent) {
        if (intent.getAction().equals(ACTION_AUTHENTICATOR_INTENT))
            return getAuthenticator().getIBinder();
        return null;
    }

    public AbstractAccountAuthenticator getAuthenticator() {
        if (sAccountAuthenticator == null)
            sAccountAuthenticator = new AccountAuthenticator(this);
        return sAccountAuthenticator;
    }

    private static class AccountAuthenticator extends AbstractAccountAuthenticator
            implements IntentConstant{
        private final Context mContext;

        public AccountAuthenticator(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
            return null;
        }

        @Override
        public Bundle addAccount(AccountAuthenticatorResponse response,
                                 String accountType, String authTokenType,
                                 String[] requiredFeatures, Bundle options)
                throws NetworkErrorException {
            final Intent intent = new Intent(mContext, AccountLoginActivity.class);
            intent.setAction(INTENT_ACTION_ACCOUNT_LOGIN);
            intent.putExtra(AccountLoginActivity.EXTRA_LOGIN_FROM_AUTHENTICATOR, true);
            final Bundle bundle = new Bundle();
            bundle.putParcelable(KEY_INTENT, intent);
            return bundle;
        }

        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
            return null;
        }

        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
            return null;
        }

        @Override
        public String getAuthTokenLabel(String s) {
            return null;
        }

        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
            return null;
        }

        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
            return null;
        }
    }
}
