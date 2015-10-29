package tw.catcafe.catplurk.android.adapter;

import android.content.Context;

import java.util.List;

import tw.catcafe.catplurk.android.model.ParcelablePlurk;
import tw.catcafe.catplurk.android.model.ParcelableResponse;
import tw.catcafe.catplurk.android.view.holder.ResponseViewHolder;

/**
 * Created by Davy on 2015/9/16.
 */
public class ResponseAdapter extends AbsResponseAdapter<List<ParcelableResponse>> {

    private List<ParcelableResponse> mData;

    public ResponseAdapter(Context context) {
        super(context);
    }

    @Override
    protected void bindStatus(ResponseViewHolder holder, int position) {
        holder.displayResponse(mContext, getResponse(position));
    }

    @Override
    public boolean isGapItem(int position) {
        if (position == getResponsesCount()) return true;
        return getResponse(position).isGap && position != getResponsesCount() - 1;
    }

    @Override
    public ParcelableResponse getResponse(int position) {
        if (position == getResponsesCount()) return null;
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position == getResponsesCount()) return position;
        return mData.get(position).hashCode();
    }

    @Override
    public long getResponseId(int position) {
        if (position == getResponsesCount()) return -1;
        return mData.get(position).plurkId;
    }

    @Override
    public int getResponsesCount() {
        if (mData == null) return 0;
        return mData.size();
    }


    @Override
    public void setData(List<ParcelableResponse> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public List<ParcelableResponse> getData() {
        return mData;
    }
}
