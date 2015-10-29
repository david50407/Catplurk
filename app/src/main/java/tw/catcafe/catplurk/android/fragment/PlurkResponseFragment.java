package tw.catcafe.catplurk.android.fragment;

import android.net.Uri;

import tw.catcafe.catplurk.android.provider.CatPlurkDataStore.Responses;
import tw.catcafe.catplurk.android.util.AsyncPlurkWrapper;

/**
 * @author Davy
 */
public class PlurkResponseFragment extends CursorResponsesFragment {
    @Override
    public Uri getContentUri() {
        return Responses.CONTENT_URI;
    }

    @Override
    protected void updateRefreshState() {
        final AsyncPlurkWrapper plurkWrapper = getPlurkWrapper();
        if (plurkWrapper == null) return;
        setRefreshing(plurkWrapper.isResponseRefreshing());
    }

    @Override
    public boolean isRefreshing() {
        final AsyncPlurkWrapper plurkWrapper = getPlurkWrapper();
        return plurkWrapper != null && plurkWrapper.isResponseRefreshing();
    }

    @Override
    public boolean getResponses(long accountId, long plurkId, long offset, long limit) {
        final AsyncPlurkWrapper plurkWrapper = getPlurkWrapper();
        if (plurkWrapper == null) return false;
        // if (offset == -1) return plurkWrapper.refreshAll(accountId);
        return plurkWrapper.getResponseAsync(accountId, plurkId, offset, limit);
    }
}
