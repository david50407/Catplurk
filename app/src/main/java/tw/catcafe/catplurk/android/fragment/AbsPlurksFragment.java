package tw.catcafe.catplurk.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import tw.catcafe.catplurk.android.Application;
import tw.catcafe.catplurk.android.adapter.AbsPlurkAdapter;
import tw.catcafe.catplurk.android.adapter.AbsPlurkAdapter.PlurkAdapterListener;
import tw.catcafe.catplurk.android.constant.IntentConstant;
import tw.catcafe.catplurk.android.loader.IExtendedLoader;
import tw.catcafe.catplurk.android.model.ParcelablePlurk;
import tw.catcafe.catplurk.android.support.fragment.ContentRecyclerViewFragment;
import tw.catcafe.catplurk.android.util.OpenIntentUtil;
import tw.catcafe.catplurk.android.util.message.PlurkListChangedEvent;
import tw.catcafe.catplurk.android.view.holder.GapViewHolder;
import tw.catcafe.catplurk.android.view.holder.PlurkViewHolder;

/**
 * @author Davy
 */
public abstract class AbsPlurksFragment<Data> extends ContentRecyclerViewFragment<AbsPlurkAdapter<Data>>
        implements IntentConstant, LoaderCallbacks<Data>, PlurkAdapterListener {
    private final Object mPlurksBusCallback;
    private PopupMenu mPopupMenu;
    private ParcelablePlurk mSelectedPlurk;
    private PopupMenu.OnMenuItemClickListener mOnPlurkMenuItemClickListener = item -> {
        // TODO: Handle menu items
        return true;
    };

    protected AbsPlurksFragment() {
        mPlurksBusCallback = createMessageBusCallback();
    }

    @Override
    public void onStart() {
        super.onStart();
        final Bus bus = Application.getInstance(getActivity()).getMessageBus();
        assert bus != null;
        bus.register(mPlurksBusCallback);
    }

    @Override
    public void onStop() {
        final Bus bus = Application.getInstance(getActivity()).getMessageBus();
        assert bus != null;
        bus.unregister(mPlurksBusCallback);
        super.onStop();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final AbsPlurkAdapter<Data> adapter = getAdapter();
        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(adapter);
        getRecyclerView().addItemDecoration(decoration);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        });
        adapter.setListener(this);
        final Bundle args = getArguments();
        final Bundle loaderArgs;
        if (args == null)
            loaderArgs = new Bundle();
        else
            loaderArgs = new Bundle(args);
        loaderArgs.putBoolean(EXTRA_FROM_USER, true);
        getLoaderManager().initLoader(0, loaderArgs, this);
        showProgress();
    }

    protected abstract long getAccountId();
    protected abstract boolean hasMoreData(Data data);

    public abstract boolean getPlurks(long accountId, long offset, long limit, long latestId);

    protected abstract Loader<Data> onCreatePlurksLoader(final Context context, final Bundle args,
                                                           final boolean fromUser);
    protected abstract void onLoadingFinished();

    //region Adapter Data
    protected Data getAdapterData() {
        final AbsPlurkAdapter<Data> adapter = getAdapter();
        return adapter.getData();
    }

    protected void setAdapterData(Data data) {
        final AbsPlurkAdapter<Data> adapter = getAdapter();
        adapter.setData(data);
    }
    //endregion Adapter Data

    //region Loader
    @Override
    public final Loader<Data> onCreateLoader(int id, Bundle args) {
        final boolean fromUser = args.getBoolean(EXTRA_FROM_USER);
        args.remove(EXTRA_FROM_USER);
        return onCreatePlurksLoader(getActivity(), args, fromUser);
    }

    @Override
    public final void onLoadFinished(Loader<Data> loader, Data data) {
        final AbsPlurkAdapter<Data> adapter = getAdapter();
        final String tag = getCurrentReadPositionTag();
        final long lastReadId;
        final int lastVisibleTop;
        final LinearLayoutManager layoutManager = getLayoutManager();
        final int lastVisiablePos = layoutManager.findFirstVisibleItemPosition();
        if (lastVisiablePos != RecyclerView.NO_POSITION && lastVisiablePos < adapter.getItemCount()) {
            lastReadId = adapter.getPlurkId(lastVisiablePos);
            final View positionView = layoutManager.findViewByPosition(lastVisiablePos);
            lastVisibleTop = positionView != null ? positionView.getTop() : 0;
        } else {
            lastReadId = -1;
            lastVisibleTop = 0;
        }
        adapter.setData(data);
        setRefreshEnabled(true);
        if (!(loader instanceof IExtendedLoader) || ((IExtendedLoader) loader).isFromUser()) {
            adapter.setLoadMoreSupported(hasMoreData(data));
            if (lastReadId != -1 && lastVisiablePos != 0) {
                int pos = -1;
                for (int i = 0, j = adapter.getItemCount(); i < j; ++i) {
                    if (lastReadId == adapter.getPlurkId(i)) {
                        pos = i;
                        break;
                    }
                }
                if (pos != -1 && adapter.isPlurk(pos)) {
                    if (layoutManager.getHeight() == 0) {
                        // RecyclerView has not currently laid out, ignore padding.
                        layoutManager.scrollToPositionWithOffset(pos, lastVisibleTop);
                    } else {
                        layoutManager.scrollToPositionWithOffset(pos, lastVisibleTop - layoutManager.getPaddingTop());
                    }
                }
            }
        }
        if (loader instanceof IExtendedLoader)
            ((IExtendedLoader) loader).setFromUser(false);
        onLoadingFinished();
    }

    @Override
    public void onLoaderReset(Loader<Data> loader) {
        if (loader instanceof IExtendedLoader) {
            ((IExtendedLoader) loader).setFromUser(false);
        }
    }
    //endregion Loader

    //region Read Position Tag
    protected String getReadPositionTag() {
        return null;
    }

    private String getCurrentReadPositionTag() {
        final String tag = getReadPositionTagWithAccount();
        if (tag == null) return null;
        return tag + "_current";
    }

    private String getReadPositionTagWithAccount() {
        return getReadPositionTag() + "_" + getAccountId();
    }
    //endregion Read Position Tag

    //region PlurkAdapterListener
    @Override
    public void onGapClick(GapViewHolder holder, int position) {
        final AbsPlurkAdapter<Data> adapter = getAdapter();
        final ParcelablePlurk plurk = adapter.getPlurk(position - 1);
        final long accountId = plurk.accountId;
        final long offset = plurk.posted.getTime();
        final long id = plurk.plurkId;
        getPlurks(accountId, offset, -1, id);
    }

    @Override
    public void onPlurkClick(PlurkViewHolder holder, int position) {
        final AbsPlurkAdapter<Data> adapter = getAdapter();
        OpenIntentUtil.openPlurk(getActivity(), adapter.getPlurk(position), null);
    }

    @Override
    public boolean onPlurkLongClick(PlurkViewHolder holder, int position) {
        // TODO: handle long click event
        return true;
    }

    @Override
    public void onPlurkActionClick(PlurkViewHolder holder, int id, int position) {
        // TODO
    }

    @Override
    public void onPlurkMenuClick(PlurkViewHolder holder, View menuView, int position) {
        // TODO
    }

    @Override
    public void onUserProfileClick(PlurkViewHolder holder, ParcelablePlurk plurk, int position) {
        // TODO
    }
    //endregion PlurkAdapterListener

    //region Message Bus
    protected Object createMessageBusCallback() {
        return new PlurksBusCallback();
    }

    protected final class PlurksBusCallback {

        protected PlurksBusCallback() {
        }

        @Subscribe
        public void notifyStatusListChanged(PlurkListChangedEvent event) {
            final AbsPlurkAdapter<Data> adapter = getAdapter();
            adapter.notifyDataSetChanged();
        }

    }
    //endregion Message Bus
}
