package tw.catcafe.catplurk.android.adapter.iface;

import android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by Davy on 2015/7/19.
 */
public interface GapSupportableAdapter {
    int ITEM_VIEW_TYPE_GAP = 1;

    boolean isGapItem(int position);
    void onGapClick(ViewHolder holder, int position);
}
