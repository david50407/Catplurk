package tw.catcafe.catplurk.android.support.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PatchedLinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import tw.catcafe.catplurk.android.R;
import tw.catcafe.catplurk.android.adapter.LoadMoreSupportAdapter;
import tw.catcafe.catplurk.android.support.fragment.iface.RefreshScrollTop;
import tw.catcafe.catplurk.android.util.ContentListScrollListener;

/**
 * @author Davy
 */
public abstract class ContentRecyclerViewFragment<Adapter extends LoadMoreSupportAdapter> extends BaseSupportFragment
        implements RefreshScrollTop, SwipeRefreshLayout.OnRefreshListener, ContentListScrollListener.ContentListSupport {
    @Bind(R.id.progress_container)
    View mProgressContainer;
    @Bind(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.error_container)
    View mErrorContainer;
    @Bind(R.id.error_icon)
    ImageView mErrorIconView;
    @Bind(R.id.error_text)
    TextView mErrorTextView;

    private LinearLayoutManager mLayoutManager;
    private Adapter mAdapter;

    // Callbacks and listeners
    private ContentListScrollListener mScrollListener;

    @Override
    public void onRefresh() {
        triggerRefresh();
    }

    @Override
    public boolean scrollToStart() {
        mLayoutManager.scrollToPositionWithOffset(0, 0);
        mRecyclerView.stopScroll();
        setControlVisible(true);
        return true;
    }

    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public abstract boolean isRefreshing();

    public LinearLayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    public void setRefreshing(final boolean refreshing) {
        final boolean currentRefreshing = mSwipeRefreshLayout.isRefreshing();
        if (refreshing == currentRefreshing) return;
        final boolean layoutRefreshing = refreshing && !mAdapter.isLoadMoreIndicatorVisible();
        mSwipeRefreshLayout.setRefreshing(layoutRefreshing);
    }

    @Override
    public void onLoadMoreContents() {
        setLoadMoreIndicatorVisible(true);
        setRefreshEnabled(false);
    }

    public final RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_content_recyclerview, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final View view = getView();
        if (view == null) throw new AssertionError();
        final Context context = view.getContext();
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mAdapter = onCreateAdapter(context);
        mLayoutManager = new PatchedLinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        mScrollListener = new ContentListScrollListener(this);
        mScrollListener.setTouchSlop(ViewConfiguration.get(context).getScaledTouchSlop());
    }

    @Override
    public void onStart() {
        super.onStart();
        mRecyclerView.addOnScrollListener(mScrollListener);
    }

    @Override
    public void onStop() {
        mRecyclerView.removeOnScrollListener(mScrollListener);
        super.onStop();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void setControlVisible(boolean visible) { }

    public void setLoadMoreIndicatorVisible(boolean visible) {
        mAdapter.setLoadMoreIndicatorVisible(visible);
    }

    public void setRefreshEnabled(boolean enabled) {
        mSwipeRefreshLayout.setEnabled(enabled);
    }

    @Override
    public boolean triggerRefresh() {
        return false;
    }

    @NonNull
    protected abstract Adapter onCreateAdapter(Context context);

    protected final void showContent() {
        mErrorContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    protected final void showProgress() {
        mErrorContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setVisibility(View.GONE);
    }

    protected final void showError(int icon, CharSequence text) {
        mErrorContainer.setVisibility(View.VISIBLE);
        mProgressContainer.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mErrorIconView.setImageResource(icon);
        mErrorTextView.setText(text);
    }

    protected final void showEmpty(int icon, CharSequence text) {
        mErrorContainer.setVisibility(View.VISIBLE);
        mProgressContainer.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        mErrorIconView.setImageResource(icon);
        mErrorTextView.setText(text);
    }
}