package tw.catcafe.catplurk.android.adapter.iface;

import tw.catcafe.catplurk.android.model.ParcelablePlurk;
import tw.catcafe.catplurk.android.plurkapi.model.Response;
import tw.catcafe.catplurk.android.view.holder.PlurkViewHolder;

/**
 * Created by Davy on 2015/7/19.
 */
public interface ResponsesAdapter extends GapSupportableAdapter {
    Response getResponse(int position);
    long getResponseId(int position);
    int getResponsesCount();
}
