package tw.catcafe.catplurk.android.adapter.iface;

/**
 * Created by Davy on 2015/7/19.
 */
public interface LoadMoreSupportableAdapter {
    int ITEM_VIEW_TYPE_LOAD_INDICATOR = 0;

    boolean isLoadMoreIndicatorVisible();
    void setLoadMoreIndicatorVisible(boolean enabled);

    boolean isLoadMoreSupported();
    void setLoadMoreSupported(boolean supported);
}
