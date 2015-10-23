package tw.catcafe.catplurk.android.adapter;

import android.content.Context;

import java.util.List;

import tw.catcafe.catplurk.android.model.ParcelablePlurk;
import tw.catcafe.catplurk.android.view.holder.PlurkViewHolder;

/**
 * Created by Davy on 2015/9/16.
 */
public class PlurkAdapter extends AbsPlurkAdapter<List<ParcelablePlurk>> {

    private List<ParcelablePlurk> mData;

    public PlurkAdapter(Context context) {
        super(context);
    }

    @Override
    protected void bindStatus(PlurkViewHolder holder, int position) {
        holder.displayPlurk(mContext, getPlurk(position));
    }

    @Override
    public boolean isGapItem(int position) {
        if (position == getPlurksCount()) return true;
        return getPlurk(position).isGap && position != getPlurksCount() - 1;
    }

    @Override
    public ParcelablePlurk getPlurk(int position) {
        if (position == getPlurksCount()) return null;
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position == getPlurksCount()) return position;
        return mData.get(position).hashCode();
    }

    @Override
    public long getPlurkId(int position) {
        if (position == getPlurksCount()) return -1;
        return mData.get(position).plurkId;
    }

    @Override
    public int getPlurksCount() {
        if (mData == null) return 0;
        return mData.size();
    }


    @Override
    public void setData(List<ParcelablePlurk> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public List<ParcelablePlurk> getData() {
        return mData;
    }
}
