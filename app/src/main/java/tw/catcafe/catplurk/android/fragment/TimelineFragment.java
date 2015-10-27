package tw.catcafe.catplurk.android.fragment;

import android.net.Uri;

import tw.catcafe.catplurk.android.provider.CatPlurkDataStore.Plurks;
import tw.catcafe.catplurk.android.util.AsyncPlurkWrapper;

/**
 * @author Davy
 */
public class TimelineFragment extends CursorPlurksFragment {

    @Override
    public Uri getContentUri() {
        return Plurks.CONTENT_URI;
    }

    @Override
    protected void updateRefreshState() {
        final AsyncPlurkWrapper plurkWrapper = getPlurkWrapper();
        if (plurkWrapper == null) return;
        setRefreshing(plurkWrapper.isTimelineRefreshing());
    }

    @Override
    public boolean isRefreshing() {
        final AsyncPlurkWrapper plurkWrapper = getPlurkWrapper();
        return plurkWrapper != null && plurkWrapper.isTimelineRefreshing();
    }

    @Override
    public boolean getPlurks(long accountId, long offset, long limit, long latestId) {
        final AsyncPlurkWrapper plurkWrapper = getPlurkWrapper();
        if (plurkWrapper == null) return false;
        // if (offset == -1) return plurkWrapper.refreshAll(accountId);
        return plurkWrapper.getTimelineAsync(accountId, offset, limit, latestId);
    }
}
