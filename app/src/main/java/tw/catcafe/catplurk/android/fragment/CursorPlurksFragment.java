package tw.catcafe.catplurk.android.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.util.Log;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;
import com.squareup.otto.Subscribe;

import org.mariotaku.sqliteqb.library.Columns.Column;
import org.mariotaku.sqliteqb.library.Expression;

import java.util.List;

import tw.catcafe.catplurk.android.R;
import tw.catcafe.catplurk.android.activity.AccountIdentifiable;
import tw.catcafe.catplurk.android.adapter.AbsPlurkAdapter;
import tw.catcafe.catplurk.android.adapter.PlurkAdapter;
import tw.catcafe.catplurk.android.model.ParcelablePlurk;
import tw.catcafe.catplurk.android.provider.CatPlurkDataStore.Plurks;
import tw.catcafe.catplurk.android.support.loader.ObjectCursorLoader;
import tw.catcafe.catplurk.android.util.message.AccountChangedEvent;
import tw.catcafe.catplurk.android.util.message.GetPlurksTaskEvent;
import tw.catcafe.catplurk.android.util.message.PlurkListChangedEvent;

import static tw.catcafe.catplurk.android.util.DatabaseUtils.getOldestPlurkPostIdAndTimeFromDatabase;
import static tw.catcafe.catplurk.android.util.DatabaseUtils.getNewestPlurkPostIdAndTimeFromDatabase;

/**
 * @author Davy
 */
public abstract class CursorPlurksFragment extends AbsPlurksFragment<List<ParcelablePlurk>> {

    public abstract Uri getContentUri();

    protected abstract void updateRefreshState();

    @Override
    protected long getAccountId() {
        final Bundle args = getArguments();
        if (args != null && args.getLong(EXTRA_ACCOUNT_ID) > 0)
            return args.getLong(EXTRA_ACCOUNT_ID);
        final FragmentActivity activity = getActivity();
        if (activity instanceof AccountIdentifiable)
            return ((AccountIdentifiable) activity).getActivatedAccountId();
        return 0;
    }

    @Override
    public boolean getPlurks(long accountId, long offset, long limit, long latestId) {
        return false;
    }

    //region Event
    @Override
    public void onStart() {
        super.onStart();
        updateRefreshState();
        reloadPlurks();
    }
    //endregion Event

    //region Loader
    @Override
    protected Loader<List<ParcelablePlurk>> onCreatePlurksLoader(Context context, Bundle args, boolean fromUser) {
        final Uri uri = getContentUri();
        final String sortOrder = getSortOrder();
        final long accountId = getAccountId();
        final Expression where = Expression.equals(new Column(Plurks.ACCOUNT_ID), accountId);
        final String selection = processWhere(where).getSQL();
        return new ObjectCursorLoader<>(context, ParcelablePlurk.CursorIndices.class, uri, Plurks.COLUMNS,
                selection, null, sortOrder);
    }

    @Override
    protected void onLoadingFinished() {
        final AbsPlurkAdapter<List<ParcelablePlurk>> adapter = getAdapter();
        if (adapter.getItemCount() > 0) {
            showContent();
        } else {
            showEmpty(R.drawable.icon_info_refresh, getString(R.string.tip_swipe_down_to_refresh));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ParcelablePlurk>> loader) {
        getAdapter().setData(null);
    }
    //endregion Loader

    //region Helpers
    protected void reloadPlurks() {
        if (getActivity() == null || isDetached()) return;
        final Bundle args = new Bundle(), fragmentArgs = getArguments();
        if (fragmentArgs != null) {
            args.putAll(fragmentArgs);
            args.putBoolean(EXTRA_FROM_USER, true);
        }
        getLoaderManager().restartLoader(0, args, this);
    }

    protected long[] getOldestPlurkIdAndTime(long accountId) {
        return getOldestPlurkPostIdAndTimeFromDatabase(getActivity(), getContentUri(), accountId);
    }

    protected long[] getNewestPlurkIdAndTime(long accountId) {
        return getNewestPlurkPostIdAndTimeFromDatabase(getActivity(), getContentUri(), accountId);
    }
    //endregion Helpers

    //region SQL Helper
    protected Expression processWhere(final Expression where) {
        return where;
    }

    protected String getSortOrder() {
        return Plurks.POSTED + " DESC";
    }
    //endregion SQL Helper

    @Override
    public boolean isRefreshing() {
        return false;
    }

    @NonNull
    @Override
    protected PlurkAdapter onCreateAdapter(Context context) {
        return new PlurkAdapter(context);
    }

    @Override
    protected boolean hasMoreData(final List<ParcelablePlurk> plurks) {
        return plurks != null && plurks.size() != 0;
    }

    @Override
    public void onLoadMoreContents() {
        super.onLoadMoreContents();
        AsyncManager.runBackgroundTask(new TaskRunnable<Object, long[], CursorPlurksFragment>() {
            @Override
            public long[] doLongOperation(Object o) throws InterruptedException {
                final long[] result = new long[4];
                result[0] = getAccountId();
                final long[] oldestPlurkIdAndTime = getOldestPlurkIdAndTime(result[0]);
                result[1] = oldestPlurkIdAndTime[1];
                result[3] = oldestPlurkIdAndTime[0];
                result[2] = -1;
                return result;
            }

            @Override
            public void callback(CursorPlurksFragment fragment, long[] result) {
                fragment.getPlurks(result[0], result[1], result[2], result[3]);
            }
        }.setResultHandler(this));
    }

    @Override
    public boolean triggerRefresh() {
        super.triggerRefresh();
        AsyncManager.runBackgroundTask(new TaskRunnable<Object, long[], CursorPlurksFragment>() {
            @Override
            public long[] doLongOperation(Object o) throws InterruptedException {
                final long[] result = new long[4];
                result[0] = getAccountId();
                result[1] = result[2] = result[3] = -1;
                return result;
            }

            @Override
            public void callback(CursorPlurksFragment fragment, long[] result) {
                fragment.getPlurks(result[0], result[1], result[2], result[3]);
            }
        }.setResultHandler(this));
        return true;
    }

    @Override
    protected Object createMessageBusCallback() {
        return new CursorStatusesBusCallback();
    }

    //region Message Bus
    protected class CursorStatusesBusCallback {

        @Subscribe
        public void notifyGetPlurksTaskChanged(GetPlurksTaskEvent event) {
            if (!event.uri.equals(getContentUri())) return;
            setRefreshing(event.running);
            if (!event.running) {
                setLoadMoreIndicatorVisible(false);
                setRefreshEnabled(true);
            }
        }

//        @Subscribe
//        public void notifyFavoriteCreated(FavoriteCreatedEvent event) {
//        }
//
//        @Subscribe
//        public void notifyFavoriteDestroyed(FavoriteDestroyedEvent event) {
//        }
//
//        @Subscribe
//        public void notifyStatusDestroyed(StatusDestroyedEvent event) {
//        }

        @Subscribe
        public void notifyStatusListChanged(PlurkListChangedEvent event) {
            getAdapter().notifyDataSetChanged();
        }

        @Subscribe
        public void notifyAccountChanged(AccountChangedEvent event) {
            reloadPlurks();
        }
    }
    //endregion Message Bus
}
