package tw.catcafe.catplurk.android.adapter;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;

import tw.catcafe.catplurk.android.adapter.iface.LoadMoreSupportableAdapter;

/**
 * Created by Davy on 2015/7/19.
 */
public abstract class LoadMoreSupportAdapter<VH extends ViewHolder> extends Adapter<VH>
        implements LoadMoreSupportableAdapter {
    private boolean mLoadMoreSupported;
    private boolean mLoadMoreIndicatorVisible;

    @Override
    public final boolean isLoadMoreIndicatorVisible() {
        return mLoadMoreIndicatorVisible;
    }

    @Override
    public final void setLoadMoreIndicatorVisible(boolean enabled) {
        if (mLoadMoreIndicatorVisible == enabled) return;
        mLoadMoreIndicatorVisible = enabled && mLoadMoreSupported;
        notifyDataSetChanged();
    }

    @Override
    public final boolean isLoadMoreSupported() {
        return mLoadMoreSupported;
    }

    @Override
    public final void setLoadMoreSupported(boolean supported) {
        mLoadMoreSupported = supported;
        if (!supported) {
            mLoadMoreIndicatorVisible = false;
        }
        notifyDataSetChanged();
    }
}
