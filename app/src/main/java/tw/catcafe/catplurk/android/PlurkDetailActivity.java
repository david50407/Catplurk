package tw.catcafe.catplurk.android;

import android.graphics.Rect;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import org.mariotaku.sqliteqb.library.Columns;
import org.mariotaku.sqliteqb.library.Expression;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import tw.catcafe.catplurk.android.activity.AccountIdentifiable;
import tw.catcafe.catplurk.android.activity.PlurkIdentifiable;
import tw.catcafe.catplurk.android.constant.IntentConstant;
import tw.catcafe.catplurk.android.fragment.PlurkResponseFragment;
import tw.catcafe.catplurk.android.plurkapi.model.Plurk;
import tw.catcafe.catplurk.android.plurkapi.model.User;
import tw.catcafe.catplurk.android.provider.CatPlurkDataStore;
import tw.catcafe.catplurk.android.util.QualifierUtil;
import tw.catcafe.catplurk.android.util.imageloader.UserProfileImageHelper;
import tw.catcafe.catplurk.android.view.holder.ResponsePlurkViewHolder;

import static tw.catcafe.catplurk.android.util.DatabaseUtils.findUserInDatabase;

/**
 * An activity representing a single Plurk detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PlurkListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link PlurkDetailFragment}.
 */
public class PlurkDetailActivity extends AppCompatActivity
        implements IntentConstant, AccountIdentifiable, PlurkIdentifiable,
        ObservableScrollViewCallbacks {

    @Bind(R.id.actionbar)
    Toolbar actionbar;
    @Bind(R.id.flexible_space)
    View mFlexibleSpaceView;
    @Bind(R.id.flexible_space_layout)
    RelativeLayout mFlexibleSpaceLayout;
    @Bind(R.id.profile)
    LinearLayout mProfile;
    @Bind(R.id.profile_image)
    CircleImageView mProfileImage;
    @Bind(R.id.title)
    TextView mTitleView;
    @Bind(R.id.qualifier)
    TextView mQualifierView;

    @Bind(R.id.cardview)
    CardView mCardview;

    @BindDimen(R.dimen.basic_margin_start)
    float mBasicMarginStart;
    @BindDimen(R.dimen.toolbar_margin_start)
    float mToolbarMarginStart;

    @BindDimen(R.dimen.elevation_app_bar)
    float mAppBarElevation;
    @BindDimen(R.dimen.flexible_space_height)
    int mFlexibleSpaceHeight;
    @BindDimen(R.dimen.flexible_card_top)
    int mFlexibleSpaceTouchCardHeight;

    @BindDimen(R.dimen.detail_plurk_user_size)
    float mUserSize;
    @BindDimen(R.dimen.detail_plurk_user_size_actionbar)
    float mUserSizeAb;

    PlurkDetailFragment plurkDetailFragment;
    Plurk currentPlurk = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getExtras() != null) {
            currentPlurk = getIntent().getExtras().getParcelable(EXTRA_PLURK);
        }
        if (currentPlurk != null) {
            final int qualifierTheme = QualifierUtil.getQualifierThemeResource(currentPlurk.getQualifier());
            setTheme(qualifierTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plurk_detail);
        ButterKnife.bind(this);
        setSupportActionBar(actionbar);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ResponsePlurkViewHolder plurkHolder = new ResponsePlurkViewHolder(mCardview);
        mCardview.setTag(plurkHolder);

        if (currentPlurk != null) {
            final User owner = findUserInDatabase(this,
                    currentPlurk.getAccountId(), currentPlurk.getOwnerId());
            if (owner != null) {
                mTitleView.setText(owner.getDisplayName());
                Application.getInstance(this).getImageLoaderWrapper().displayImage(mProfileImage,
                        UserProfileImageHelper.getUrl(owner));
            }
            mQualifierView.setText(currentPlurk.getQualifierTranslated());
            plurkHolder.displayPlurk(this, currentPlurk);
        }

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final PlurkResponseFragment responseFragment = (PlurkResponseFragment)fragmentManager.findFragmentById(R.id.response_list);
        final ObservableRecyclerView recyclerView = responseFragment.getRecyclerView();
        recyclerView.setScrollViewCallbacks(this);
        mCardview.addOnLayoutChangeListener((view, left, top, right, bottom,
                                             oldLeft, oldTop, oldRight, oldBottom) ->
                recyclerView.invalidateItemDecorations());
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) == 0)
                    outRect.top = (int)getListPaddingTop();
            }
        });

        ScrollUtils.addOnGlobalLayoutListener(recyclerView, () ->
                updateFlexibleSpace(recyclerView.getCurrentScrollY()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        scrollY += getListPaddingTop();
        updateFlexibleSpace(scrollY);
        updateCardPosition(scrollY);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private float getListPaddingTop() {
        return mCardview.getMeasuredHeight() + mFlexibleSpaceTouchCardHeight + mBasicMarginStart;
    }

    private void updateFlexibleSpace(final int scrollY) {
        final float flexibleSpaceTranslationY = -(float) scrollY
                * mFlexibleSpaceHeight
                / mFlexibleSpaceTouchCardHeight;
        mFlexibleSpaceView.setTranslationY(ScrollUtils.getFloat(
                flexibleSpaceTranslationY, -mFlexibleSpaceHeight, 0));

        ViewCompat.setElevation(actionbar, scrollY < mFlexibleSpaceTouchCardHeight ? 0 : mAppBarElevation);

        final int visiableFlexibleSpaceHeight = (int)(mFlexibleSpaceHeight + actionbar.getHeight()
                + flexibleSpaceTranslationY);
        final int adjustedScrollY = (int) ScrollUtils.getFloat(scrollY, 0, visiableFlexibleSpaceHeight);
        updateFlexibleSpaceProfile(visiableFlexibleSpaceHeight, adjustedScrollY);
    }

    private void updateFlexibleSpaceProfile(final int visiableFlexibleSpaceHeight,
                                         final int adjustedScrollY) {
        final float maxScale = (mUserSize - mUserSizeAb) / mUserSizeAb;
        final float scale = maxScale *
                ((float) visiableFlexibleSpaceHeight - adjustedScrollY)
                / visiableFlexibleSpaceHeight;

        mProfile.setPivotX(0);
        mProfile.setPivotY(actionbar.getHeight() / 2);
        mProfile.setScaleX(1 + scale);
        mProfile.setScaleY(1 + scale);

        final int maxTranslationY = actionbar.getHeight() + mFlexibleSpaceTouchCardHeight
                - (int) (actionbar.getHeight() * (1 + scale));
        final int translationY = (int) (maxTranslationY
                * ((float) visiableFlexibleSpaceHeight - adjustedScrollY)
                / (mFlexibleSpaceHeight + actionbar.getHeight()));
        mProfile.setTranslationY(translationY);

        final float maxTranslationX = mToolbarMarginStart - mBasicMarginStart;
        final float translationX =  (int) (maxTranslationX
                * ((float) visiableFlexibleSpaceHeight - adjustedScrollY)
                / (mFlexibleSpaceHeight + actionbar.getHeight()));
        mFlexibleSpaceLayout.setTranslationX(maxTranslationX - translationX);
    }

    private void updateCardPosition(final int scrollY) {
        mCardview.setTranslationY(ScrollUtils.getFloat(
                -scrollY, -mCardview.getMeasuredHeight() - mFlexibleSpaceHeight, 0));
    }

    @Override
    public long getActivatedAccountId() {
        if (currentPlurk == null) return -1;
        return currentPlurk.getAccountId();
    }

    @Override
    public long getCurrentPlurkId() {
        if (currentPlurk == null) return -1;
        return currentPlurk.getPlurkId();
    }
}
