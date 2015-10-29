package tw.catcafe.catplurk.android.constant;

/**
 * @author Davy
 */
public interface CatPlurkConstant extends IntentConstant, AccountConstant, DatabaseConstant {
    String CATPLURK_APP_NAME = "CatPlurk";
    String CATPLURK_PACKAGE_NAME = "tw.catcafe.catplurk.android";
    String CATPLURK_PROJECT_URL = "http://catplurk.catcafe.tw";

    String PLURK_APP_KEY = "ML5eA4XkJy7t";
    String PLURK_APP_SECRET = "v1mVscqJws3YWTiEBHMOUIzhEu2r8PeC";

    String LOGTAG = CATPLURK_APP_NAME;

    String SCHEME_HTTP = "http";
    String SCHEME_HTTPS = "https";
    String SCHEME_CONTENT = "content";
    String SCHEME_CATPLURK = "catplurk";

    String AUTHORITY_PLURK = "plurk";

    String QUERY_PARAM_ACCOUNT_ID = "account_id";
    String QUERY_PARAM_PLURK_ID = "plurk_id";

    String SHARED_PREFERENCES_NAME = "preferences";

    String QUERY_PARAM_NOTIFY = "notify";

    String TASK_TAG_GET_TIMELINE = "get_timeline";
    String TASK_TAG_GET_RESPONSE = "get_response";
}
