package tw.catcafe.catplurk.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tw.catcafe.catplurk.android.R;
import tw.catcafe.catplurk.android.adapter.iface.ResponsesAdapter;
import tw.catcafe.catplurk.android.support.view.holder.LoadIndicatorViewHolder;
import tw.catcafe.catplurk.android.view.holder.GapViewHolder;
import tw.catcafe.catplurk.android.view.holder.ResponseViewHolder;

/**
 * Created by Davy on 2015/7/19.
 */
public abstract class AbsResponseAdapter<D> extends LoadMoreSupportAdapter<ViewHolder>
        implements ResponsesAdapter {

    public static final int ITEM_VIEW_TYPE_RESPONSE = 2;

    final Context mContext;
    final LayoutInflater mInflater;

    ResponseAdapterListener mResponseAdapterListener;

    public AbsResponseAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public abstract D getData();
    public abstract void setData(D data);

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_VIEW_TYPE_LOAD_INDICATOR: {
                final View view = mInflater.inflate(R.layout.card_item_load_indicator, parent, false);
                return new LoadIndicatorViewHolder(view);
            }
            case ITEM_VIEW_TYPE_RESPONSE: {
                final View view = mInflater.inflate(R.layout.list_item_response, parent, false);
                return new ResponseViewHolder(this, view);
            }
            case ITEM_VIEW_TYPE_GAP: {
                final View view = mInflater.inflate(R.layout.card_item_gap, parent, false);
                return new GapViewHolder(this, view);
            }
        }
        throw new IllegalStateException("Unknown view type " + viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ITEM_VIEW_TYPE_RESPONSE: {
                bindStatus(((ResponseViewHolder) holder), position);
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getResponsesCount()) {
            return ITEM_VIEW_TYPE_LOAD_INDICATOR;
        } else if (isGapItem(position)) {
            return ITEM_VIEW_TYPE_GAP;
        }
        return ITEM_VIEW_TYPE_RESPONSE;
    }

    @Override
    public final int getItemCount() {
        return getResponsesCount() + (isLoadMoreIndicatorVisible() ? 1 : 0);
    }

    @Override
    public final void onGapClick(ViewHolder holder, int position) {
        if (mResponseAdapterListener == null) return;
        mResponseAdapterListener.onGapClick((GapViewHolder) holder, position);
    }

    public void setListener(ResponseAdapterListener listener) {
        mResponseAdapterListener = listener;
    }

    public boolean isResponse(int position) {
        return position < getResponsesCount();
    }

    protected abstract void bindStatus(ResponseViewHolder holder, int position);

    public interface ResponseAdapterListener {
        void onGapClick(GapViewHolder holder, int position);
    }
}
