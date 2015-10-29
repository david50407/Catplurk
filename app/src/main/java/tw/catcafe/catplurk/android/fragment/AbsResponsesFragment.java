package tw.catcafe.catplurk.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import tw.catcafe.catplurk.android.Application;
import tw.catcafe.catplurk.android.adapter.AbsResponseAdapter;
import tw.catcafe.catplurk.android.adapter.AbsResponseAdapter.ResponseAdapterListener;
import tw.catcafe.catplurk.android.constant.IntentConstant;
import tw.catcafe.catplurk.android.loader.IExtendedLoader;
import tw.catcafe.catplurk.android.model.ParcelablePlurk;
import tw.catcafe.catplurk.android.plurkapi.model.Response;
import tw.catcafe.catplurk.android.support.fragment.ContentObservableRecyclerViewFragment;
import tw.catcafe.catplurk.android.util.message.ResponseListChangedEvent;
import tw.catcafe.catplurk.android.view.holder.GapViewHolder;

/**
 * @author Davy
 */
public abstract class AbsResponsesFragment<Data> extends ContentObservableRecyclerViewFragment<AbsResponseAdapter<Data>>
        implements IntentConstant, LoaderCallbacks<Data>, ResponseAdapterListener {
    private final Object mResponsesBusCallback;

    protected AbsResponsesFragment() {
        mResponsesBusCallback = createMessageBusCallback();
    }

    @Override
    public void onStart() {
        super.onStart();
        final Bus bus = Application.getInstance(getActivity()).getMessageBus();
        assert bus != null;
        bus.register(mResponsesBusCallback);
    }

    @Override
    public void onStop() {
        final Bus bus = Application.getInstance(getActivity()).getMessageBus();
        assert bus != null;
        bus.unregister(mResponsesBusCallback);
        super.onStop();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final AbsResponseAdapter<Data> adapter = getAdapter();
//        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(adapter);
//        getRecyclerView().addItemDecoration(decoration);
//        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onChanged() {
//                decoration.invalidateHeaders();
//            }
//        });
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
    protected abstract long getPlurkId();
    protected abstract boolean hasMoreData(Data data);

    public abstract boolean getResponses(long accountId, long plurkId, long offset, long limit);

    protected abstract Loader<Data> onCreateResponsesLoader(final Context context, final Bundle args,
                                                            final boolean fromUser);
    protected abstract void onLoadingFinished();

    //region Adapter Data
    protected Data getAdapterData() {
        final AbsResponseAdapter<Data> adapter = getAdapter();
        return adapter.getData();
    }

    protected void setAdapterData(Data data) {
        final AbsResponseAdapter<Data> adapter = getAdapter();
        adapter.setData(data);
    }
    //endregion Adapter Data

    //region Loader
    @Override
    public final Loader<Data> onCreateLoader(int id, Bundle args) {
        final boolean fromUser = args.getBoolean(EXTRA_FROM_USER);
        args.remove(EXTRA_FROM_USER);
        return onCreateResponsesLoader(getActivity(), args, fromUser);
    }

    @Override
    public final void onLoadFinished(Loader<Data> loader, Data data) {
        final AbsResponseAdapter<Data> adapter = getAdapter();
        adapter.setData(data);
        setRefreshEnabled(true);
        if (!(loader instanceof IExtendedLoader) || ((IExtendedLoader) loader).isFromUser())
            adapter.setLoadMoreSupported(hasMoreData(data));
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

    //region PlurkAdapterListener
    @Override
    public void onGapClick(GapViewHolder holder, int position) {
        final AbsResponseAdapter<Data> adapter = getAdapter();
        final Response response = adapter.getResponse(position - 1);
        final long accountId = response.getAccountId();
        final long plurkId = response.getPlurkId();
        getResponses(accountId, plurkId, position, -1);
    }
    //endregion PlurkAdapterListener

    //region Message Bus
    protected Object createMessageBusCallback() {
        return new ResponsesBusCallback();
    }

    protected final class ResponsesBusCallback {

        protected ResponsesBusCallback() {
        }

        @Subscribe
        public void notifyResponsesListChanged(ResponseListChangedEvent event) {
            final AbsResponseAdapter<Data> adapter = getAdapter();
            adapter.notifyDataSetChanged();
        }

    }
    //endregion Message Bus
}
