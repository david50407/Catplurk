package tw.catcafe.catplurk.android.view.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import tw.catcafe.catplurk.android.adapter.iface.GapSupportableAdapter;

/**
 * Created by Davy on 2015/7/20.
 */
public class GapViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final GapSupportableAdapter mAdapter;

    public GapViewHolder(GapSupportableAdapter adapter, View view) {
        super(view);
        mAdapter = adapter;
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mAdapter.onGapClick(this, getLayoutPosition());
    }
}
