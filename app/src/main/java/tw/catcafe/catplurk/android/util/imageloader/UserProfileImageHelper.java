package tw.catcafe.catplurk.android.util.imageloader;

import android.content.ContentValues;

import tw.catcafe.catplurk.android.model.Account;
import tw.catcafe.catplurk.android.plurkapi.model.User;
import tw.catcafe.catplurk.android.provider.CatPlurkDataStore;

/**
 * @author Davy
 */
public class UserProfileImageHelper {
    public static String getUrl(User user) {
        if (user == null) return getUrl(-1, false, -1);
        return getUrl(user.getUserId(), user.hasProfileImage(), user.getAvatar());
    }

    public static String getUrl(Account account) {
        if (account == null) return getUrl(-1, false, -1);
        return getUrl(account.accountId, account.hasProfileImage, account.avatar);
    }

    public static String getUrl(final ContentValues value) {
        if (value == null) return null;
        if (value.getAsLong(CatPlurkDataStore.Users.ID) == null) return null;
        return getUrl(value.getAsLong(CatPlurkDataStore.Users.ID),
                value.getAsBoolean(CatPlurkDataStore.Users.HAS_PROFILE_IMAGE),
                value.getAsInteger(CatPlurkDataStore.Users.AVATAR));
    }

    public static String getUrl(long id, boolean hasProfileImage, long avatar) {
        if (hasProfileImage)
            if (avatar > 0)
                return "https://avatars.plurk.com/" + id + "-big" + avatar + ".jpg";
            else
                return "https://avatars.plurk.com/" + id + "-big.jpg";
        else
            return "https://www.plurk.com/static/default_big.gif";
    }
}
