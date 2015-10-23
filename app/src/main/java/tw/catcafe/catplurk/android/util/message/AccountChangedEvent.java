package tw.catcafe.catplurk.android.util.message;

/**
 * @author Davy
 */
public class AccountChangedEvent {
    public final long account_id;
    public AccountChangedEvent(final long account_id) {
        this.account_id = account_id;
    }
}
