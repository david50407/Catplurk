package tw.catcafe.catplurk.android.util;

import android.accounts.Account;
import android.support.annotation.Nullable;

import rx.Observable;
import tw.catcafe.catplurk.android.Application;
import tw.catcafe.catplurk.android.constant.AccountConstant;
import tw.catcafe.catplurk.android.plurkapi.auth.OAuthToken;

/**
 * @author Davy
 */
public class AccountManager implements AccountConstant {
    private Application mApplication;
    private android.accounts.AccountManager mAccountManager;

    public AccountManager(Application app) {
        this.mApplication = app;
        this.mAccountManager = android.accounts.AccountManager.get(app);
    }

    public Account createAccount(String name, OAuthToken token) {
        final Account account = new Account(name, ACCOUNT_TYPE);
        mAccountManager.addAccountExplicitly(account, null, null);
        mAccountManager.setAuthToken(account, ACCOUNT_OAUTH_TOKEN, token.getOauthToken());
        mAccountManager.setAuthToken(account, ACCOUNT_OAUTH_TOKEN_SECRET, token.getOauthTokenSecret());
        return account;
    }

    @Nullable
    public Account updateAccount(String name, OAuthToken token) {
        final Account account = getAccount(name);
        if (account == null)
            return null;
        mAccountManager.setAuthToken(account, ACCOUNT_OAUTH_TOKEN, token.getOauthToken());
        mAccountManager.setAuthToken(account, ACCOUNT_OAUTH_TOKEN_SECRET, token.getOauthTokenSecret());
        return account;
    }

    @Nullable
    public Account getAccount(String name) {
        return getAccountObservable(name).toBlocking().singleOrDefault(null);
    }

    public boolean hasAccount() {
        return mAccountManager.getAccountsByType(ACCOUNT_TYPE).length > 0;
    }

    public boolean hasAccount(String name) {
        return getAccountObservable(name).count().toBlocking().single() > 0;
    }

    //region Observable methods
    public Observable<Account> getAccountObservable(String name) {
        return Observable.from(mAccountManager.getAccountsByType(ACCOUNT_TYPE))
                .takeFirst(account -> account.name.equals(name));
    }

    public Observable<Account> getAccountsObservable() {
        return Observable.from(mAccountManager.getAccountsByType(ACCOUNT_TYPE));
    }
    //endregion Observable methods
}
