package tw.catcafe.catplurk.android.util;

import android.content.ContentValues;

import tw.catcafe.catplurk.android.Constants;
import tw.catcafe.catplurk.android.plurkapi.model.Plurk;
import tw.catcafe.catplurk.android.plurkapi.model.User;
import tw.catcafe.catplurk.android.provider.CatPlurkDataStore.*;

/**
 * @author Davy
 */
// TODO: Use annotation to auto complete
public class ContentValueCreator implements Constants {
    public static ContentValues createPlurk(Plurk plurk, final long accountId) {
        final ContentValues values = new ContentValues();
        values.put(Plurks.ACCOUNT_ID, accountId);
        values.put(Plurks.PLURK_ID, plurk.getPlurkId());
        values.put(Plurks.QUALIFIER, plurk.getQualifier());
        values.put(Plurks.QUALIFIER_TRANSLATED, plurk.getQualifierTranslated());
        values.put(Plurks.IS_UNREAD, plurk.isUnread().ordinal());
        values.put(Plurks.OWNER_ID, plurk.getOwnerId());
        values.put(Plurks.POSTED, plurk.getPosted().getTime());
        values.put(Plurks.NO_COMMENTS, plurk.getNoComments().ordinal());
        values.put(Plurks.CONTENT, plurk.getContent());
        values.put(Plurks.CONTENT_RAW, plurk.getContentRaw());
        values.put(Plurks.RESPONSE_COUNT, plurk.getResponseCount());
        values.put(Plurks.RESPONSES_SEEN, plurk.getResponsesSeen());
        values.put(Plurks.LIMITED_TO, ArrayUtils.toString(plurk.getLimitedTo(), ',', false));
        values.put(Plurks.FAVORITE, plurk.getFavorite());
        values.put(Plurks.FAVORITE_COUNT, plurk.getFavoriteCount());
        values.put(Plurks.FAVORERS, ArrayUtils.toString(plurk.getFavorers(), ',', false));
        values.put(Plurks.REPLURKABLE, plurk.getReplurkable());
        values.put(Plurks.REPLURKED, plurk.getReplurked());
        values.put(Plurks.REPLURKER_ID, plurk.getReplurkerId());
        values.put(Plurks.REPLURKERS_COUNT, plurk.getReplurkersCount());
        values.put(Plurks.REPLURKERS, ArrayUtils.toString(plurk.getReplurkers(), ',', false));
        return values;
    }


    public static ContentValues createUser(User user, final long accountId) {
        final ContentValues values = new ContentValues();
        values.put(Users.ACCOUNT_ID, accountId);
        values.put(Users.ID, user.getUserId());
        values.put(Users.NICK_NAME, user.getNickname());
        values.put(Users.DISPLAY_NAME, user.getDisplayName());
        values.put(Users.HAS_PROFILE_IMAGE, user.hasProfileImage() ? 1 : 0);
        values.put(Users.AVATAR, user.getAvatar());
        values.put(Users.LOCATION, user.getLocation());
        values.put(Users.DEFAULT_LANG, user.getDefaultLang());
        values.put(Users.DATE_OF_BIRTH, user.getDateOfBirth().getTime());
        values.put(Users.BDAY_PRIVACY, user.getBdayPrivacy().ordinal());
        values.put(Users.FULL_NAME, user.getFullname());
        values.put(Users.GENDER, user.getGender().ordinal());
        values.put(Users.KARMA, user.getKarma());
        values.put(Users.RECRUITED, user.getRecruited());
        values.put(Users.RELATIONSHIP, user.getRelationship().ordinal());
        values.put(Users.NAME_COLOR, user.getNameColor());
        return values;
    }
}
