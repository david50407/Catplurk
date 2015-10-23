package tw.catcafe.catplurk.android.adapter.iface;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

/**
 * Created by Davy on 2015/7/19.
 */
public interface ContentCardClickListener {
    void onCardClick(ViewHolder holder, int position);
    void onCardLongClick(ViewHolder holder, int position);
    void onCardActionClick(ViewHolder holder, int id, int position);
    void onCardMenuClick(ViewHolder holder, View menuView, int position);
}
