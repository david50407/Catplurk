package tw.catcafe.catplurk.android.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.mariotaku.sqliteqb.library.Expression;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tw.catcafe.catplurk.android.constant.AccountConstant;
import tw.catcafe.catplurk.android.plurkapi.auth.OAuthToken;
import tw.catcafe.catplurk.android.plurkapi.model.User;
import tw.catcafe.catplurk.android.provider.CatPlurkDataStore.Accounts;
import tw.catcafe.catplurk.android.support.util.ObjectCursor;
import tw.catcafe.catplurk.android.util.ContentResolverUtils;

/**
 * @author Davy
 */
public class Account implements AccountConstant {
    public String oauthToken;
    public String oauthTokenSecret;
    public long accountId;
    public String nickName;
    public String displayName;
    public String fullName;
    public long avatar;
    public String nameColor;
    public boolean hasProfileImage;

    public Account() {
        super();
    }

    public Account(User user, OAuthToken oAuthToken) {
        super();
        this.accountId = user.getUserId();
        this.nickName = user.getNickname();
        this.displayName = user.getDisplayName();
        this.fullName = user.getFullname();
        this.avatar = user.getAvatar();
        this.nameColor = user.getNameColor();
        this.hasProfileImage = user.hasProfileImage();
        this.oauthToken = oAuthToken.getOauthToken();
        this.oauthTokenSecret = oAuthToken.getOauthTokenSecret();
    }

    public ContentValues toContentValues() {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(Accounts.OAUTH_TOKEN, oauthToken);
        contentValues.put(Accounts.OAUTH_TOKEN_SECRET, oauthTokenSecret);
        contentValues.put(Accounts.ACCOUNT_ID, accountId);
        contentValues.put(Accounts.NICK_NAME, nickName);
        contentValues.put(Accounts.DISPLAY_NAME, displayName);
        contentValues.put(Accounts.FULL_NAME, fullName);
        contentValues.put(Accounts.AVATAR, avatar);
        contentValues.put(Accounts.NAME_COLOR, nameColor);
        contentValues.put(Accounts.HAS_PROFILE_IMAGE, hasProfileImage ? 1 : 0);
        return contentValues;
    }

    //region Fields getter
    public String getNickName() {
        return nickName;
    }

    public String getDisplayName() {
        if (displayName == null) return nickName;
        return displayName;
    }
    //endregion Fields getter

    //region Cursor Indices
    public static class CursorIndices extends ObjectCursor.CursorIndices<Account> {
        public final int _id, nick_name, account_id, oauth_token, oauth_token_secret, display_name,
                avatar, has_profile_image;

        public CursorIndices(final Cursor cursor) {
            super(cursor);
            _id = cursor.getColumnIndex(Accounts._ID);
            nick_name = cursor.getColumnIndex(Accounts.NICK_NAME);
            account_id = cursor.getColumnIndex(Accounts.ACCOUNT_ID);
            oauth_token = cursor.getColumnIndex(Accounts.OAUTH_TOKEN);
            oauth_token_secret = cursor.getColumnIndex(Accounts.OAUTH_TOKEN_SECRET);
            display_name = cursor.getColumnIndex(Accounts.DISPLAY_NAME);
            avatar = cursor.getColumnIndex(Accounts.AVATAR);
            has_profile_image = cursor.getColumnIndex(Accounts.HAS_PROFILE_IMAGE);
        }

        @Override
        public Account newObject(Cursor cursor) {
            final Account account = new Account();
            account.accountId = cursor.getLong(account_id);
            account.nickName = cursor.getString(nick_name);
            account.oauthToken = cursor.getString(oauth_token);
            account.oauthTokenSecret = cursor.getString(oauth_token_secret);
            account.displayName = cursor.getString(display_name);
            account.avatar = cursor.getLong(avatar);
            account.hasProfileImage = cursor.getInt(has_profile_image) == 1;
            return account;
        }

        @Override
        public String toString() {
            // TODO
            return "CursorIndices{}";
        }
    }
    //endregion Cursor indices

    public static Account getAccount(final Context context, final long account_id) {
        if (context == null) return null;
        final Cursor cursor = ContentResolverUtils.query(context.getContentResolver(),
                Accounts.CONTENT_URI, Accounts.COLUMNS,
                Expression.equals(Accounts.ACCOUNT_ID, account_id).getSQL(), null, null);
        if (cursor != null) {
            try {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    return new CursorIndices(cursor).newObject(cursor);
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public static List<Account> getAccountsList(final Context context) {
        if (context == null) return Collections.emptyList();
        final ArrayList<Account> accounts = new ArrayList<>();
        final Cursor cur = ContentResolverUtils.query(context.getContentResolver(),
                Accounts.CONTENT_URI, Accounts.COLUMNS, null, null, null);
        if (cur == null) return accounts;
        final CursorIndices indices = new CursorIndices(cur);
        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            accounts.add(indices.newObject(cur));
            cur.moveToNext();
        }
        cur.close();
        return accounts;
    }
}
