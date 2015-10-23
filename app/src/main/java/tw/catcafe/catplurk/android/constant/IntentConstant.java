package tw.catcafe.catplurk.android.constant;

/**
 * @author Davy
 */
public interface IntentConstant {
    String INTENT_NAMESPACE = "tw.catcafe.catplurk.android.";

    String INTENT_ACTION_TIMELINE = INTENT_NAMESPACE + "ACCOUNT_TIMELINE";
    String INTENT_ACTION_ACCOUNT_LOGIN = INTENT_NAMESPACE + "ACCOUNT_LOGIN";

    String EXTRA_ACCOUNT_ID = "account_id";
    String EXTRA_PLURK = "plurk";
    String EXTRA_PLURKS = "plurks";
    String EXTRA_FROM_USER = "from_user";

    String BROADCAST_NAMESPACE = INTENT_NAMESPACE;
    String BROADCAST_REFRESH_HOME_TIMELINE = BROADCAST_NAMESPACE + "REFRESH_HOME_TIMELINE";
    String BROADCAST_RESCHEDULE_HOME_TIMELINE_REFRESHING = BROADCAST_NAMESPACE +
            "RESCHEDULE_HOME_TIMELINE_REFRESHING";
}
