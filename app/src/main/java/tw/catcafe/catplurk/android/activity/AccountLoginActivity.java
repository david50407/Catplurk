package tw.catcafe.catplurk.android.activity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.mariotaku.restfu.http.Endpoint;
import org.mariotaku.sqliteqb.library.Expression;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import tw.catcafe.catplurk.android.Application;
import tw.catcafe.catplurk.android.Constants;
import tw.catcafe.catplurk.android.R;
import tw.catcafe.catplurk.android.TimelineActivity;
import tw.catcafe.catplurk.android.model.Account;
import tw.catcafe.catplurk.android.plurkapi.PlurkException;
import tw.catcafe.catplurk.android.plurkapi.PlurkOAuth;
import tw.catcafe.catplurk.android.plurkapi.auth.Authenticator.OAuthPasswordAuthenticator;
import tw.catcafe.catplurk.android.plurkapi.auth.Authenticator.OAuthPasswordAuthenticator.AuthenticationException;
import tw.catcafe.catplurk.android.plurkapi.auth.OAuthAuthorization;
import tw.catcafe.catplurk.android.plurkapi.auth.OAuthToken;
import tw.catcafe.catplurk.android.plurkapi.PlurkApi;
import tw.catcafe.catplurk.android.plurkapi.model.User;
import tw.catcafe.catplurk.android.provider.CatPlurkDataStore;
import tw.catcafe.catplurk.android.support.fragment.SupportProgressDialogFragment;
import tw.catcafe.catplurk.android.util.AccountManager;
import tw.catcafe.catplurk.android.util.AsyncTaskUtils;
import tw.catcafe.catplurk.android.util.InstallationIdUtil;
import tw.catcafe.catplurk.android.util.ParseUtil;
import tw.catcafe.catplurk.android.util.PlurkAPIFactory;

import static tw.catcafe.catplurk.android.util.ToastMessageUtil.showErrorMessage;

/**
 * @author Davy
 */
public class AccountLoginActivity extends AppCompatActivity
        implements Constants {
    public static final String FRAGMENT_TAG_SIGN_IN_PROGRESS = "sign_in_progress";
    public static final String EXTRA_LOGIN_FROM_AUTHENTICATOR = "login_from_authenticator";
    private static final String PLURK_SIGNUP_URL = "https://www.plurk.com/Users/showRegister";
    private BaseSignInTask mTask;
    private ContentResolver mResolver;
    private final Handler mHandler = new Handler();
    @Bind(R.id.actionbar)
    Toolbar mActionbar;
    @Bind(R.id.username)
    EditText mUsername;
    @Bind(R.id.password)
    EditText mPassword;
    @Bind(R.id.sign_in)
    Button mSignin;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResolver = getContentResolver();
        setContentView(R.layout.activity_account_login);
        ButterKnife.bind(this);
        setSupportActionBar(mActionbar);
        onChangedUsernamePassword();
    }

    //region Events
    @OnClick(R.id.sign_up)
    protected void onClickSignUp() {
        final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(PLURK_SIGNUP_URL));
        startActivity(intent);
    }

    @OnClick(R.id.sign_in)
    protected void onClickSignIn() {
        if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
            mTask.cancel(true);
        }
        // TODO: write app key into config
        final OAuthToken appKey = new OAuthToken(PLURK_APP_KEY, PLURK_APP_SECRET);
        final String username = ParseUtil.parseString(mUsername.getText());
        final String password = ParseUtil.parseString(mPassword.getText());
        mTask = new AppSignInTask(this, username, password, appKey);
        AsyncTaskUtils.executeTask(mTask);
    }

    @OnTextChanged({R.id.username, R.id.password})
    protected void onChangedUsernamePassword() {
        mSignin.setEnabled(mUsername.getText().length() > 0 && mPassword.getText().length() > 0);
    }
    //endregion Events

    //region SignInMethodIntroduction
    @OnClick(R.id.sign_in_method_introduction)
    protected void onClickSignInMethodIntroduction() {
        final FragmentManager fm = getSupportFragmentManager();
        new SignInMethodIntroductionDialogFragment().show(fm.beginTransaction(),
                "sign_in_method_introduction");
    }

    public static class SignInMethodIntroductionDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.sign_in_method_introduction);
            builder.setMessage(R.string.sign_in_method_introduction_detail);
            builder.setPositiveButton(android.R.string.ok, null);
            return builder.create();
        }
    }
    //endregion SignInMethodIntroduction

    //region Sign-in actions
    void onSignInStart() {
        mHandler.post(() -> {
            if (isFinishing()) return;
            final FragmentManager fm = getSupportFragmentManager();
            final FragmentTransaction ft = fm.beginTransaction();
            final SupportProgressDialogFragment fragment = new SupportProgressDialogFragment();
            fragment.setCancelable(false);
            fragment.show(ft, FRAGMENT_TAG_SIGN_IN_PROGRESS);
        });
    }

    void onSignInResult(final SignInResponse result) {
        final FragmentManager fm = getSupportFragmentManager();
        final Fragment f = fm.findFragmentByTag(FRAGMENT_TAG_SIGN_IN_PROGRESS);
        if (f instanceof DialogFragment) {
            ((DialogFragment) f).dismiss();
        }
        if (result != null) {
            if (result.alreadyLoggedIn) {
                final AccountManager accountManager = ((Application)getApplicationContext()).getAccountManager();
                accountManager.updateAccount(result.user.getNickname(), result.oauth.getOauthToken());
                final Account account = new Account(result.user, result.oauth.getOauthToken());
                final ContentValues contentValues = account.toContentValues();
                mResolver.update(CatPlurkDataStore.Accounts.CONTENT_URI, contentValues,
                        Expression.equals(CatPlurkDataStore.Accounts.ACCOUNT_ID,
                                result.user.getUserId()).getSQL(),
                        null);
                Toast.makeText(this, R.string.error_already_logged_in, Toast.LENGTH_SHORT).show();
            } else if (result.succeed) {
                final AccountManager accountManager = ((Application)getApplicationContext()).getAccountManager();
                accountManager.createAccount(result.user.getNickname(), result.oauth.getOauthToken());
                final Account account = new Account(result.user, result.oauth.getOauthToken());
                final ContentValues contentValues = account.toContentValues();
                mResolver.insert(CatPlurkDataStore.Accounts.CONTENT_URI, contentValues);
                if (!isSignInFromAuthenticatorService()) { // Goto Timeline
//                final long loggedId = result.user.getUserId();
                    final Intent intent = new Intent(this, TimelineActivity.class);
//                intent.putExtra(EXTRA_REFRESH_IDS, new long[]{loggedId});
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
                finish();
            } else {
                if (result.exception instanceof OAuthPasswordAuthenticator.WrongUserPassException) {
                    Toast.makeText(this, R.string.error_wrong_username_password, Toast.LENGTH_SHORT).show();
                } else if (result.exception instanceof AuthenticationException) {
                    showErrorMessage(this, getString(R.string.action_signing_in), result.exception.getCause(), true);
                } else {
                    showErrorMessage(this, getString(R.string.action_signing_in), result.exception, true);
                }
            }
        }
    }

    boolean isSignInFromAuthenticatorService() {
        final Bundle extras = getIntent().getExtras();
        return extras != null && extras.getBoolean(EXTRA_LOGIN_FROM_AUTHENTICATOR, false);
    }
    //endregion Sign-in actions

    //region SignInTask
    public static class AppSignInTask extends BaseSignInTask {
        private final String username, password;
        private final Context context;
        private final OAuthToken consumerKey;

        public AppSignInTask(final AccountLoginActivity context,
                             final String username, final String password,
                             final OAuthToken consumerKey) {
            super(context);
            this.context = context;
            this.username = username;
            this.password = password;
            this.consumerKey = consumerKey;
        }

        @Override
        protected SignInResponse doInBackground(final Object... params) {
            try {
                return authOAuth();
            } catch (final PlurkException e) {
                e.printStackTrace();
                return new SignInResponse(false, false, e);
            } catch (final AuthenticationException e) {
                e.printStackTrace();
                return new SignInResponse(false, false, e);
            }
        }

        private SignInResponse authOAuth() throws AuthenticationException, PlurkException {
            Endpoint endpoint = PlurkAPIFactory.getOAuthEndpoint();
            OAuthAuthorization auth = new OAuthAuthorization(consumerKey.getOauthToken(), consumerKey.getOauthTokenSecret());
            final PlurkOAuth oauth = PlurkAPIFactory.getInstance(context, endpoint, auth, PlurkOAuth.class);
            final OAuthPasswordAuthenticator authenticator = new OAuthPasswordAuthenticator(oauth);
            final String modelName = Build.MODEL;
            final String deviceId = InstallationIdUtil.getId(context).replaceAll("-", "");
            final OAuthToken accessToken = authenticator.getOAuthAccessToken(username, password,
                    modelName, deviceId);
            // Test for oauthToken
            endpoint = PlurkAPIFactory.getOAuthRestEndpoint();
            auth = new OAuthAuthorization(consumerKey.getOauthToken(), consumerKey.getOauthTokenSecret(), accessToken);
            final PlurkApi plurkApi = PlurkAPIFactory.getInstance(context, endpoint, auth, PlurkApi.class);
            final User loginedUser = plurkApi.getMyInfomation();
            if (loginedUser.getUserId() <= 0) return new SignInResponse(false, false, null);
            final AccountManager accountManager = ((Application)context.getApplicationContext()).getAccountManager();
            if (accountManager.hasAccount(loginedUser.getNickname()))
                return new SignInResponse(true, auth, loginedUser);
            return new SignInResponse(false, auth, loginedUser);
        }
    }

    public static abstract class BaseSignInTask extends AsyncTask<Object, Object, SignInResponse> {
        protected final AccountLoginActivity callback;
        public BaseSignInTask(final AccountLoginActivity callback) {
            this.callback = callback;
        }
        @Override
        protected void onPostExecute(final SignInResponse result) {
            if (callback != null) {
                callback.onSignInResult(result);
            }
        }
        @Override
        protected void onPreExecute() {
            if (callback != null) {
                callback.onSignInStart();
            }
        }
    }

    static class SignInResponse {
        public final boolean alreadyLoggedIn, succeed;
        public final Exception exception;
        public final OAuthAuthorization oauth;
        public final User user;

        public SignInResponse(final boolean alreadyLoggedIn, final boolean succeed, final Exception exception) {
            this(alreadyLoggedIn, succeed, exception, null, null);
        }

        public SignInResponse(final boolean alreadyLoggedIn, final boolean succeed, final Exception exception,
                              final OAuthAuthorization oauth, final User user) {
            this.alreadyLoggedIn = alreadyLoggedIn;
            this.succeed = succeed;
            this.exception = exception;
            this.oauth = oauth;
            this.user = user;
        }

        public SignInResponse(final boolean alreadyLoggedIn, final OAuthAuthorization oauth,
                              final User user) {
            this(alreadyLoggedIn, true, null, oauth, user);
        }

        private User getUser() {
            return user;
        }
    }
    //endregion SingInTask
}
