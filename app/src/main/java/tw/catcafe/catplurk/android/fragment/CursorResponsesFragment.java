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
import tw.catcafe.catplurk.android.activity.PlurkIdentifiable;
import tw.catcafe.catplurk.android.adapter.AbsResponseAdapter;
import tw.catcafe.catplurk.android.adapter.ResponseAdapter;
import tw.catcafe.catplurk.android.model.ParcelableResponse;
import tw.catcafe.catplurk.android.provider.CatPlurkDataStore.Responses;
import tw.catcafe.catplurk.android.support.loader.ObjectCursorLoader;
import tw.catcafe.catplurk.android.util.message.GetResponsesTaskEvent;
import tw.catcafe.catplurk.android.util.message.PlurkUserUpdatedEvent;
import tw.catcafe.catplurk.android.util.message.ResponseListChangedEvent;

import static tw.catcafe.catplurk.android.util.DatabaseUtils.getResponseCountInDatabase;

/**
 * @author Davy
 */
public abstract class CursorResponsesFragment extends AbsResponsesFragment<List<ParcelableResponse>> {

    public abstract Uri getContentUri();

    protected abstract void updateRefreshState();

    @Override
    protected long getPlurkId() {
        final Bundle args = getArguments();
        if (args != null && args.getLong(EXTRA_PLURK_ID) > 0)
            return args.getLong(EXTRA_PLURK_ID);
        final FragmentActivity activity = getActivity();
        if (activity instanceof PlurkIdentifiable)
            return ((PlurkIdentifiable) activity).getCurrentPlurkId();
        return 0;
    }

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
    public boolean getResponses(long accountId, long plurkId, long offset, long limit) {
        return false;
    }

    //region Event
    @Override
    public void onStart() {
        super.onStart();
        updateRefreshState();
        reloadResponses();
    }
    //endregion Event

    //region Loader
    @Override
    protected Loader<List<ParcelableResponse>> onCreateResponsesLoader(Context context, Bundle args, boolean fromUser) {
        final Uri uri = getContentUri();
        final String sortOrder = getSortOrder();
        final long accountId = getAccountId();
        final long plurkId = getPlurkId();
        final Expression where = Expression.and(
                Expression.equals(new Column(Responses.ACCOUNT_ID), accountId),
                Expression.equals(new Column(Responses.PLURK_ID), plurkId));
        final String selection = processWhere(where).getSQL();
        return new ObjectCursorLoader<>(context, ParcelableResponse.CursorIndices.class, uri, Responses.COLUMNS,
                selection, null, sortOrder);
    }

    @Override
    protected void onLoadingFinished() {
        final AbsResponseAdapter<List<ParcelableResponse>> adapter = getAdapter();
        if (adapter.getItemCount() > 0) {
            showContent();
        } else {
            showEmpty(R.drawable.icon_info_refresh, getString(R.string.tip_swipe_down_to_refresh));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ParcelableResponse>> loader) {
        getAdapter().setData(null);
    }
    //endregion Loader

    //region Helpers
    protected void reloadResponses() {
        if (getActivity() == null || isDetached()) return;
        final Bundle args = new Bundle(), fragmentArgs = getArguments();
        if (fragmentArgs != null) {
            args.putAll(fragmentArgs);
            args.putBoolean(EXTRA_FROM_USER, true);
        }
        getLoaderManager().restartLoader(0, args, this);
    }

    protected int getResponsesCount(long accountId, long plurkId) {
        return getResponseCountInDatabase(getActivity(), getContentUri(), accountId, plurkId);
    }
    //endregion Helpers

    //region SQL Helper
    protected Expression processWhere(final Expression where) {
        return where;
    }

    protected String getSortOrder() {
        return Responses.POSTED + " ASC";
    }
    //endregion SQL Helper

    @Override
    public boolean isRefreshing() {
        return false;
    }

    @NonNull
    @Override
    protected ResponseAdapter onCreateAdapter(Context context) {
        return new ResponseAdapter(context);
    }

    @Override
    protected boolean hasMoreData(final List<ParcelableResponse> responses) {
        return responses != null && responses.size() != 0;
    }

    @Override
    public void onLoadMoreContents() {
        super.onLoadMoreContents();
        AsyncManager.runBackgroundTask(new TaskRunnable<Object, long[], CursorResponsesFragment>() {
            @Override
            public long[] doLongOperation(Object o) throws InterruptedException {
                final long[] result = new long[4];
                result[0] = getAccountId();
                result[1] = getPlurkId();
                result[2] = getResponsesCount(result[0], result[1]);
                result[3] = -1;
                return result;
            }

            @Override
            public void callback(CursorResponsesFragment fragment, long[] result) {
                fragment.getResponses(result[0], result[1], result[2], result[3]);
            }
        }.setResultHandler(this));
    }

    @Override
    public boolean triggerRefresh() {
        super.triggerRefresh();
        AsyncManager.runBackgroundTask(new TaskRunnable<Object, long[], CursorResponsesFragment>() {
            @Override
            public long[] doLongOperation(Object o) throws InterruptedException {
                final long[] result = new long[4];
                result[0] = getAccountId();
                result[1] = getPlurkId();
                result[2] = result[3] = -1;
                return result;
            }

            @Override
            public void callback(CursorResponsesFragment fragment, long[] result) {
                fragment.getResponses(result[0], result[1], result[2], result[3]);
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
        public void notifyGetPlurksTaskChanged(GetResponsesTaskEvent event) {
            if (!event.uri.equals(getContentUri())) return;
            setRefreshing(event.running);
            if (!event.running)
                setLoadMoreIndicatorVisible(false);
            setRefreshEnabled(!event.running);
        }
//
//        @Subscribe
//        public void notifyStatusDestroyed(StatusDestroyedEvent event) {
//        }

        @Subscribe
        public void notifyResponseListChanged(ResponseListChangedEvent event) {
            getAdapter().notifyDataSetChanged();
        }

        @Subscribe
        public void notifyPlurkUserUpdated(PlurkUserUpdatedEvent event) {
            getAdapter().notifyDataSetChanged();
        }
    }
    //endregion Message Bus
}
