package tw.catcafe.catplurk.android;

import android.os.Bundle;

import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import tw.catcafe.catplurk.android.constant.IntentConstant;
import tw.catcafe.catplurk.android.model.ParcelablePlurk;
import tw.catcafe.catplurk.android.plurkapi.model.User;
import tw.catcafe.catplurk.android.util.QualifierUtil;
import tw.catcafe.catplurk.android.util.imageloader.UserProfileImageHelper;

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
        implements IntentConstant, ObservableScrollViewCallbacks {

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
    @Bind(R.id.scroll)
    ObservableScrollView scrollView;

    @Bind(R.id.plurk_content)
    TextView mPlurkContent;

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
    ParcelablePlurk currentPlurk = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getExtras() != null) {
            currentPlurk = getIntent().getExtras().getParcelable(EXTRA_PLURK);
        }
        if (currentPlurk != null) {
            final int qualifierTheme = QualifierUtil.getQualifierThemeResource(currentPlurk.qualifier);
            setTheme(qualifierTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plurk_detail);
        ButterKnife.bind(this);
        setSupportActionBar(actionbar);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putLong(PlurkDetailFragment.ARG_ITEM_ID,
                    getIntent().getLongExtra(PlurkDetailFragment.ARG_ITEM_ID, -1));
            plurkDetailFragment = new PlurkDetailFragment();
            plurkDetailFragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.plurk_detail_container, plurkDetailFragment)
                    .commit();
        }

        if (currentPlurk != null) {
            final User owner = findUserInDatabase(this,
                    currentPlurk.getAccountId(), currentPlurk.getOwnerId());
            if (owner != null) {
                mTitleView.setText(owner.getDisplayName());
                Application.getInstance(this).getImageLoaderWrapper().displayImage(mProfileImage,
                        UserProfileImageHelper.getUrl(owner));
            }
            mQualifierView.setText(currentPlurk.qualifierTranslated);
            mPlurkContent.setText(currentPlurk.contentRaw);
        }

        scrollView.setScrollViewCallbacks(this);

        ScrollUtils.addOnGlobalLayoutListener(scrollView, new Runnable() {
            @Override
            public void run() {
                updateFlexibleSpace(scrollView.getCurrentScrollY());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
//            navigateUpTo(new Intent(this, PlurkListActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        updateFlexibleSpace(scrollY);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
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
}
