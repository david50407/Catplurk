package tw.catcafe.catplurk.android.loader;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import rx.Observable;

import tw.catcafe.catplurk.android.model.ParcelablePlurk;
import tw.catcafe.catplurk.android.util.NoDuplicatesArrayList;

/**
 * @author Davy
 */
public abstract class BasePlurksLoader extends AsyncTaskLoader<List<ParcelablePlurk>>
        implements IExtendedLoader {
    private final List<ParcelablePlurk> mData = new NoDuplicatesArrayList<>();
    private final boolean mFirstLoad;
    private boolean mFromUser;

    public BasePlurksLoader(final Context context, final List<ParcelablePlurk> data, final boolean fromUser) {
        super(context);
        mFirstLoad = (data == null);
        if (!mFirstLoad)
            mData.addAll(data);
        mFromUser = fromUser;
    }

    @Override
    public boolean isFromUser() {
        return mFromUser;
    }

    @Override
    public void setFromUser(boolean fromUser) {
        mFromUser = fromUser;
    }

    protected boolean containsPlurk(final long plurkId) {
        return Observable.from(mData).exists((plurk) -> plurk.plurkId == plurkId).toBlocking().single();
    }

    protected boolean deletePlurk(final List<ParcelablePlurk> plurks, final long plurkId) {
        if (plurks == null || plurks.isEmpty()) return false;
        boolean result = false;
        for (int i = plurks.size() - 1; i >= 0; --i) {
            if (plurks.get(i).plurkId == plurkId) {
                plurks.remove(i);
                result = true;
            }
        }
        return result;
    }

    @Nullable
    protected List<ParcelablePlurk> getData() {
        return mData;
    }

    protected boolean isFirstLoad() {
        return mFirstLoad;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
