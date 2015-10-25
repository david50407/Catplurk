package tw.catcafe.catplurk.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.squareup.otto.Bus;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import tw.catcafe.catplurk.android.activity.AccountIdentifiable;
import tw.catcafe.catplurk.android.activity.AccountLoginActivity;
import tw.catcafe.catplurk.android.model.Account;
import tw.catcafe.catplurk.android.util.AccountManager;
import tw.catcafe.catplurk.android.util.OpenIntentUtil;
import tw.catcafe.catplurk.android.util.ToastMessageUtil;
import tw.catcafe.catplurk.android.util.imageloader.UserProfileImageHelper;
import tw.catcafe.catplurk.android.util.message.AccountChangedEvent;

/**
 * @author Davy
 */
public class TimelineActivity extends AppCompatActivity
        implements Constants, AccountIdentifiable {
    final static int PROFILE_IDENTIFIER_ADD_ACCOUNT = -2;
    final static int PROFILE_IDENTIFIER_MANAGE_ACCOUNT = -3;

    @Bind(R.id.actionbar)
    Toolbar actionbar;
    @Bind(R.id.fab)
    android.support.design.widget.FloatingActionButton mFab;
    Drawer navigation;

    protected long mActivatedAccountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AccountManager accountManager = ((Application)getApplicationContext()).getAccountManager();
        if (!accountManager.hasAccount()) {
            final Intent intent = new Intent(INTENT_ACTION_ACCOUNT_LOGIN);
            intent.setClass(this, AccountLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        setSupportActionBar(actionbar);


        final AccountHeader accountHeader = buildAccountHeader(savedInstanceState);
        navigation = buildDrawer(accountHeader);
        mActivatedAccountId = accountHeader.getActiveProfile().getIdentifier();
        updateTitle(navigation.getDrawerItems().get(navigation.getCurrentSelection()));
    }

    private AccountHeader buildAccountHeader(final Bundle savedInstanceState) {
        final ArrayList<IProfile> profiles = new ArrayList<>(Observable.from(Account.getAccountsList(this))
                .map(account -> new ProfileDrawerItem()
                        .withIdentifier((int) account.accountId)
                        .withName(account.getDisplayName())
                        .withNameShown(true)
                        .withEmail("@" + account.getNickName())
                        .withIcon(UserProfileImageHelper.getUrl(account)))
                .toList().toBlocking().single());

        profiles.add(new ProfileSettingDrawerItem()
                .withIdentifier(PROFILE_IDENTIFIER_ADD_ACCOUNT)
                .withName(getString(R.string.add_account))
                .withDescription(getString(R.string.add_account_description))
                .withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_add)
                        .actionBar()
                        .paddingDp(5)
                        .colorRes(R.color.material_drawer_primary_text)));

        profiles.add(new ProfileSettingDrawerItem()
                .withIdentifier(PROFILE_IDENTIFIER_MANAGE_ACCOUNT)
                .withName(getString(R.string.manage_account))
                .withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_settings)));

        return new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withProfiles(profiles)
                .withOnAccountHeaderListener((view, profile, currentProfile) -> {
                    final int identifier = profile.getIdentifier();
                    switch (identifier) {
                        case PROFILE_IDENTIFIER_ADD_ACCOUNT: {
                            OpenIntentUtil.openAccountLogin(this);
                            return true;
                        }
                        case PROFILE_IDENTIFIER_MANAGE_ACCOUNT: {
                            ToastMessageUtil.showInfoMessage(this, R.string.not_implement_yet, false);
                            return true;
                        }
                        default: {
                            mActivatedAccountId = identifier;
                            final Bus bus = Application.getInstance(TimelineActivity.this).getMessageBus();
                            assert bus != null;
                            bus.post(new AccountChangedEvent(mActivatedAccountId));
                            return false;
                        }
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
    }

    private Drawer buildDrawer(AccountHeader accountHeader) {
        return new DrawerBuilder()
                .withActivity(this)
                .withToolbar(actionbar)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(accountHeader)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_all)
                                .withIcon(FontAwesome.Icon.faw_home)
                                .withIdentifier(NAVIGATE_ALL),
//                        new PrimaryDrawerItem()
//                                .withName(R.string.drawer_item_unread)
//                                .withIcon(GoogleMaterial.Icon.gmd_wb_iridescent)
//                                .withIdentifier(NAVIGATE_UNREAD),
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_my)
                                .withIcon(FontAwesome.Icon.faw_user)
                                .withIdentifier(NAVIGATE_MY),
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_responded)
                                .withIcon(FontAwesome.Icon.faw_reply)
                                .withIdentifier(NAVIGATE_RESPONDED),
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_private)
                                .withIcon(FontAwesome.Icon.faw_lock)
                                .withIdentifier(NAVIGATE_PRIVATE),
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_liked_and_replurked)
                                .withIcon(FontAwesome.Icon.faw_heart)
                                .withIdentifier(NAVIGATE_LIKE_AND_REPLURK),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem()
                                .withName(R.string.drawer_item_settings)
                                .withIcon(GoogleMaterial.Icon.gmd_settings)
                )
                .withOnDrawerItemClickListener((parent, view, drawerItem) -> {
                    switch (drawerItem.getIdentifier()) {
                        case NAVIGATE_ALL:
                            TimelineActivity.this.updateTitle(drawerItem);
                            break;
                        case NAVIGATE_UNREAD:
                        case NAVIGATE_MY:
                        case NAVIGATE_RESPONDED:
                        case NAVIGATE_PRIVATE:
                        case NAVIGATE_LIKE_AND_REPLURK: {
                            ToastMessageUtil.showInfoMessage(this, R.string.not_implement_yet, false);
                            return true;
                        }
                        default: {
                            ToastMessageUtil.showInfoMessage(this, R.string.not_implement_yet, false);
                            return true;
                        }
                    }
                    return false;
                })
                .withShowDrawerOnFirstLaunch(true)
                .build();
    }

    private void updateTitle(IDrawerItem item) {
        switch (item.getIdentifier()) {
            case NAVIGATE_ALL:
            case NAVIGATE_UNREAD:
            case NAVIGATE_MY:
            case NAVIGATE_RESPONDED:
            case NAVIGATE_PRIVATE:
            case NAVIGATE_LIKE_AND_REPLURK:
                setTitle(((Nameable) item).getName().getText(this));
                break;
        }
    }

    @Override
    public long getActivatedAccountId() {
        return mActivatedAccountId;
    }

    @OnClick(R.id.fab)
    void onFabClick() {
    }
}
