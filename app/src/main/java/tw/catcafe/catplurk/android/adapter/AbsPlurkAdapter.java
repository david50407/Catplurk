package tw.catcafe.catplurk.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.Calendar;
import java.util.TimeZone;

import tw.catcafe.catplurk.android.R;
import tw.catcafe.catplurk.android.adapter.iface.PlurksAdapter;
import tw.catcafe.catplurk.android.model.ParcelablePlurk;
import tw.catcafe.catplurk.android.support.view.holder.LoadIndicatorViewHolder;
import tw.catcafe.catplurk.android.view.holder.GapViewHolder;
import tw.catcafe.catplurk.android.view.holder.PlurkViewHolder;
import tw.catcafe.catplurk.android.view.holder.DateViewHolder;

/**
 * Created by Davy on 2015/7/19.
 */
public abstract class AbsPlurkAdapter<D> extends LoadMoreSupportAdapter<ViewHolder>
        implements PlurksAdapter, StickyRecyclerHeadersAdapter<DateViewHolder> {

    public static final int ITEM_VIEW_TYPE_PLURK = 2;

    final Context mContext;
    final LayoutInflater mInflater;

    PlurkAdapterListener mPlurkAdapterListener;

    public AbsPlurkAdapter(Context context) {
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
            case ITEM_VIEW_TYPE_PLURK: {
                final View view = mInflater.inflate(R.layout.card_item_plurk, parent, false);
                return new PlurkViewHolder(this, view);
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
            case ITEM_VIEW_TYPE_PLURK: {
                bindStatus(((PlurkViewHolder) holder), position);
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getPlurksCount()) {
            return ITEM_VIEW_TYPE_LOAD_INDICATOR;
        } else if (isGapItem(position)) {
            return ITEM_VIEW_TYPE_GAP;
        }
        return ITEM_VIEW_TYPE_PLURK;
    }

    @Override
    public final int getItemCount() {
        return getPlurksCount() + (isLoadMoreIndicatorVisible() ? 1 : 0);
    }

    //region StickyRecyclerHeadersAdapter
    public long getHeaderId(int position) {
        if (isGapItem(position) || getPlurk(position) == null || getPlurk(position).posted == null) return 0;
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(getPlurk(position).posted);
        calendar.setTimeZone(TimeZone.getDefault());
        return calendar.get(Calendar.DAY_OF_YEAR) + calendar.get(Calendar.YEAR) * 366;
    }

    public DateViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.list_header, parent, false);
        return new DateViewHolder(view);
    }

    public void onBindHeaderViewHolder(DateViewHolder holder, int position) {
        holder.bind(isGapItem(position) ? null : getPlurk(position));
    }
    //endregion StickyRecyclerHeadersAdapter

    @Override
    public final void onGapClick(ViewHolder holder, int position) {
        if (mPlurkAdapterListener == null) return;
        mPlurkAdapterListener.onGapClick((GapViewHolder) holder, position);
    }

    //region ContentCardClickListener
    @Override
    public void onCardClick(ViewHolder holder, int position) {
        if (mPlurkAdapterListener == null) return;
        mPlurkAdapterListener.onPlurkClick((PlurkViewHolder) holder, position);
    }

    @Override
    public void onCardLongClick(ViewHolder holder, int position) {
        if (mPlurkAdapterListener == null) return;
        mPlurkAdapterListener.onPlurkLongClick((PlurkViewHolder) holder, position);
    }

    @Override
    public void onCardActionClick(ViewHolder holder, int id, int position) {
        if (mPlurkAdapterListener == null) return;
        mPlurkAdapterListener.onPlurkActionClick((PlurkViewHolder) holder, id, position);
    }

    @Override
    public void onCardMenuClick(ViewHolder holder, View menuView, int position) {
        if (mPlurkAdapterListener == null) return;
        mPlurkAdapterListener.onPlurkMenuClick((PlurkViewHolder) holder, menuView, position);
    }

    @Override
    public void onUserProfileClick(PlurkViewHolder holder, int position) {
        if (mPlurkAdapterListener == null) return;
        final ParcelablePlurk plurk = getPlurk(position);
        if (plurk == null) return;
        mPlurkAdapterListener.onUserProfileClick(holder, plurk, position);
    }
    //endregion ContentCardClickListener

    public void setListener(PlurkAdapterListener listener) {
        mPlurkAdapterListener = listener;
    }

    public boolean isPlurk(int position) {
        return position < getPlurksCount();
    }

    protected abstract void bindStatus(PlurkViewHolder holder, int position);

    public interface PlurkAdapterListener {
        void onGapClick(GapViewHolder holder, int position);

        void onPlurkClick(PlurkViewHolder holder, int position);
        boolean onPlurkLongClick(PlurkViewHolder holder, int position);

        void onPlurkActionClick(PlurkViewHolder holder, int id, int position);
        void onPlurkMenuClick(PlurkViewHolder holder, View menuView, int position);

        void onUserProfileClick(PlurkViewHolder holder, ParcelablePlurk plurk, int position);
    }
}
