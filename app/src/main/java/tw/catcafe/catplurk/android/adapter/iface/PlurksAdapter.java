package tw.catcafe.catplurk.android.adapter.iface;

import tw.catcafe.catplurk.android.model.ParcelablePlurk;
import tw.catcafe.catplurk.android.view.holder.PlurkViewHolder;

/**
 * Created by Davy on 2015/7/19.
 */
public interface PlurksAdapter extends PlurkViewHolder.PlurkClickListener,
        GapSupportableAdapter, ContentCardClickListener {
    ParcelablePlurk getPlurk(int position);
    long getPlurkId(int position);
    int getPlurksCount();
}
