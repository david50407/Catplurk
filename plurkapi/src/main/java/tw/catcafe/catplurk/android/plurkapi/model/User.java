package tw.catcafe.catplurk.android.plurkapi.model;

import java.util.Date;

/**
 * @author Davy
 */
public interface User extends Comparable<User>, PlurkApiResponse {
    long getAccountId();
    long getUserId();
//    boolean isVerifiedAccount();
//    long fansCount();
    String getLocation();
    String getDefaultLang();
//    String pageTitle();
    long getAvatar();
    boolean hasProfileImage();
//    long getResponseCount();
    String getFullname();
    String getNameColor();
    Date getDateOfBirth();
    BirthdayPrivacy getBdayPrivacy();
    Gender getGender();
    double getKarma();
    long getRecruited();
//    String getTimezone();
//    long getProfileViewedCount();
//    String getAbout();
    String getDisplayName();
    Relationship getRelationship();
//    Privacy getPrivacy();
    String getNickname();

    enum Relationship {
        NOT_SAYING,
        SINGLE,
        MARRIED,
        DIVORCED,
        ENGAGED,
        IN_RELATIONSHIP,
        COMPLICATED,
        WIDOWED,
        UNSTABLE_RELATIONSHIP,
        OPEN_RELATIONSHIP,
        UNKNOWN;

        public static Relationship parse(final String v) {
            return Enum.valueOf(Relationship.class, v.toUpperCase());
        }
    }

    enum Privacy {
        WORLD,
        FRIENDS_ONLY
    }

    enum Gender {
        FEMALE,
        MALE,
        OTHER
    }

    enum BirthdayPrivacy {
        HIDE,
        DATE_ONLY,
        ALL,
        NULL
    }

//    enum NameColor {
//        BLACK,
//        RED,
//        BLUE,
//        GREEN
//    }
/* {
    "gender": 0,
    "plurks_count": 8202,
    "friends_count": 175,
    "post_anonymous_plurk": false,
    "avatar": 14,
    "setup_twitter_sync": false,
    "date_of_birth": "Wed, 21 Dec 1994 00:01:00 GMT",
    "location": "Taiping, Taiwan",
    "avatar_medium": "http://avatars.plurk.com/7805915-medium14.gif",
    "accept_private_plurk_from": "all",
    "recruited": 2,
    "bday_privacy": 2,
    "karma": 130.79
} */

//    boolean isLinkedFacebook();
//    boolean isSetupedFacebookSync();
//    boolean isSetupedWeiboSync();
}
