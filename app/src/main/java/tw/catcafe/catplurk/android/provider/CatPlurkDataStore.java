package tw.catcafe.catplurk.android.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Davy
 */
public interface CatPlurkDataStore {
    String AUTHORITY = "catplurk";

    String TYPE_PRIMARY_KEY = "INTEGER PRIMARY KEY AUTOINCREMENT";
    String TYPE_INT = "INTEGER";
    String TYPE_INT_UNIQUE = "INTEGER UNIQUE";
    String TYPE_BOOLEAN = "INTEGER(1)";
    String TYPE_BOOLEAN_DEFAULT_TRUE = "INTEGER(1) DEFAULT 1";
    String TYPE_BOOLEAN_DEFAULT_FALSE = "INTEGER(1) DEFAULT 0";
    String TYPE_TEXT = "TEXT";
    String TYPE_DOUBLE_NOT_NULL = "DOUBLE NOT NULL";
    String TYPE_TEXT_NOT_NULL = "TEXT NOT NULL";
    String TYPE_TEXT_NOT_NULL_UNIQUE = "TEXT NOT NULL UNIQUE";

    String CONTENT_PATH_NULL = "null_content";

    String CONTENT_PATH_DATABASE_READY = "database_ready";

    Uri BASE_CONTENT_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
            .authority(AUTHORITY).build();

    Uri CONTENT_URI_NULL = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH_NULL);

    Uri CONTENT_URI_DATABASE_READY = Uri.withAppendedPath(BASE_CONTENT_URI,
            CONTENT_PATH_DATABASE_READY);

    interface Accounts extends BaseColumns {
        String TABLE_NAME = "accounts";
        String CONTENT_PATH = TABLE_NAME;
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);

        //region Column name
        /**
         * Login name of the account<br />
         * Type: TEXT NOT NULL
         */
        String NICK_NAME = "nick_name";

        /**
         * Unique ID of the account.<br />
         * Type: INTEGER (long)
         */
        String ACCOUNT_ID = "account_id";

        /**
         * OAuth Token of the account.<br />
         * Type: TEXT
         */
        String OAUTH_TOKEN = "oauth_token";

        /**
         * Token Secret of the account.<br />
         * Type: TEXT
         */
        String OAUTH_TOKEN_SECRET = "oauth_token_secret";

        /**
         * Display name of the account<br />
         * Type: TEXT NOT NULL
         */
        String DISPLAY_NAME = "display_name";

        /**
         * User's avatar version.<br />
         * Type: INTEGER (long)
         */
        String AVATAR = "avatar";

        /**
         * If user has profile image.<br />
         * Type: BOOLEAN
         */
        String HAS_PROFILE_IMAGE = "has_profile_image";

        /**
         * User's name color.<br />
         * Type: TEXT
         */
        String NAME_COLOR = "name_color";

        /**
         * The user's full name.<br />
         * Type: TEXT
         */
        String FULL_NAME = "full_name";
        //endregion Column name

        String[] COLUMNS = {_ID             , NICK_NAME                , ACCOUNT_ID     ,
                OAUTH_TOKEN, OAUTH_TOKEN_SECRET, DISPLAY_NAME, AVATAR  , HAS_PROFILE_IMAGE,
                NAME_COLOR, FULL_NAME};

        String[] TYPES   = {TYPE_PRIMARY_KEY, TYPE_TEXT_NOT_NULL_UNIQUE, TYPE_INT_UNIQUE,
                TYPE_TEXT  , TYPE_TEXT         , TYPE_TEXT   , TYPE_INT, TYPE_BOOLEAN     ,
                TYPE_TEXT , TYPE_TEXT};
    }

    interface Plurks extends BaseColumns {
        String TABLE_NAME = "plurks";
        String CONTENT_PATH = TABLE_NAME;
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);

        //region Column name
        /**
         * Account ID of the Plurk.<br />
         * Type: INTEGER (long)
         */
        String ACCOUNT_ID = "account_id";

        /**
         * ID of the Plurk.<br />
         * Type: INTEGER (long)
         */
        String PLURK_ID = "plurk_id";

        /**
         * The English qualifier.<br />
         * Type: TEXT NOT NULL
         */
        String QUALIFIER = "qualifier";

        /**
         * The translated qualifier.<br />
         * Only set if the language is not English.<br />
         * Type: TEXT
         */
        String QUALIFIER_TRANSLATED = "qualifier_translated";

        /**
         * Specifies if the plurk is read, unread or muted.<br />
         * Type: INTEGER (boolean)
         */
        String IS_UNREAD = "is_unread";

        /**
         * Type of plurk it is and if the plurk has been responded by the user.<br />
         * Type: INTEGER
         */
        // String PLURK_TYPE = "plurk_type";

        /**
         * Which timeline does this Plurk belong to.<br />
         * If there's replurkers, this will be owner_id, or not will be account_id.<br />
         * Type: INTEGER (long)
         */
        //String USER_ID = "user_id";

        /**
         * The plurkId of the owner/poster of this plurk.<br />
         * For anonymous plurk, this will be 99999 (anonymous).<br />
         * Type: INTERGER (long)
         */
        String OWNER_ID = "owner_id";

        /**
         * The date this plurk was posted.<br />
         * Type: INTEGER (long)
         */
        String POSTED = "posted";

        /**
         * Specifies if this plurk can be responsed.<br />
         * If set to 1, then responses are disabled for this plurk.<br />
         * If set to 2, then only friends can respond to this plurk.<br />
         * Type: INTEGER
         */
        String NO_COMMENTS = "noComments";

        /**
         * The formatted content, emoticons and images will be turned into HTML tags.<br />
         * Type: TEXT NOT NULL
         */
        String CONTENT = "content";

        /**
         * The raw content as user entered it.<br />
         * Type: TEXT NOT NULL
         */
        String CONTENT_RAW = "content_raw";

        /**
         * How many responses does the plurk have.<br />
         * Type: INTEGER
         */
        String RESPONSE_COUNT = "response_count";

        /**
         * How many of the responses have the user read.<br />
         * This is automatically updated when fetching responses or marking a plurk as read.<br />
         * Type: INTEGER
         */
        String RESPONSES_SEEN = "responses_seen";

        /**
         * Specifies the plurk is public or limited to friends.<br />
         * If null, then this plurk is public.<br />
         * [0] for all friends, and [ids, ids, ...] for the limited ids.<br />
         * Type: TEXT
         */
        String LIMITED_TO = "limited_to";

        /**
         * Specifies if current user has liked given plurk.<br />
         * Type: INTEGER (boolean)
         */
        String FAVORITE = "favorite";

        /**
         * Count of users who liked this plurk.<br />
         * Type: INTEGER
         */
        String FAVORITE_COUNT = "favorite_count";

        /**
         * List of ids of users who liked given plurk.<br />
         * Type: TEXT
         */
        String FAVORERS = "favorers";

        /**
         * Specifies if the plurk can be replurked.<br />
         * Type: INTEGER (boolean)
         */
        String REPLURKABLE = "replurkable";

        /**
         * Specifies if current user has replurked this plurk.<br />
         * Type: INTEGER (boolean)
         */
        String REPLURKED = "replurked";

        /**
         * Id of a user who has replurked this plurk to current user's timeline.<br />
         * Type: INTEGER (long)
         */
        String REPLURKER_ID = "replurker_id";

        /**
         * Count of users who replurked the plurk.<br />
         * Type: INTEGER
         */
        String REPLURKERS_COUNT = "replurkers_count";

        /**
         * List od ids of users who replurked the plurk.<br />
         * Type: TEXT
         */
        String REPLURKERS = "replurkers";

        /**
         * Set to 1 if the status is a gap.<br />
         * Type: INTEGER (boolean)
         */
        String IS_GAP = "is_gap";
        //endregion Column name

        String[] COLUMNS = {_ID             , ACCOUNT_ID, PLURK_ID, QUALIFIER         ,
                QUALIFIER_TRANSLATED, IS_UNREAD   , OWNER_ID, POSTED  , NO_COMMENTS,
                CONTENT            , CONTENT_RAW       , RESPONSE_COUNT, RESPONSES_SEEN, LIMITED_TO,
                FAVORITE    , FAVORITE_COUNT, FAVORERS , REPLURKABLE , REPLURKED   , REPLURKER_ID,
                REPLURKERS_COUNT, REPLURKERS, IS_GAP     };

        String[] TYPES   = {TYPE_PRIMARY_KEY, TYPE_INT  , TYPE_INT, TYPE_TEXT_NOT_NULL,
                TYPE_TEXT           , TYPE_BOOLEAN, TYPE_INT, TYPE_INT, TYPE_BOOLEAN,
                TYPE_TEXT_NOT_NULL, TYPE_TEXT_NOT_NULL, TYPE_INT      , TYPE_INT      , TYPE_TEXT ,
                TYPE_BOOLEAN, TYPE_INT      , TYPE_TEXT, TYPE_BOOLEAN, TYPE_BOOLEAN, TYPE_INT    ,
                TYPE_INT        , TYPE_TEXT, TYPE_BOOLEAN};
    }

    interface Users extends BaseColumns {
        String TABLE_NAME = "users";
        String CONTENT_PATH = TABLE_NAME;
        Uri CONETNT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);

        //region Column name
        /**
         * Account ID of the User.<br />
         * Type: INTEGER (long)
         */
        String ACCOUNT_ID = "account_id";

        /**
         * ID of the User.<br />
         * Type: INTEGER (long)
         */
        String ID = "plurkId";

        /**
         * The unique nick name of this user.<br />
         * This can be login name.
         * Type: TEXT NOT NULL
         */
        String NICK_NAME = "nick_name";

        /**
         * The display name of this user.<br />
         * If set as null, this could be nick_name.<br />
         * Type: TEXT
         */
        String DISPLAY_NAME = "display_name";

        /**
         * Specifies this user has own profile image.<br />
         * Type: INTEGER (boolean)
         */
        String HAS_PROFILE_IMAGE = "has_profile_image";

        /**
         * Specifies what the latest avatar version is.<br />
         * This can be NULL if there is no other version.<br />
         * Type: INTEGER
         */
        String AVATAR = "avatar";

        /**
         * The user's location.<br />
         * Type: TEXT
         */
        String LOCATION = "location";

        /**
         * The user's profile language.<br />
         * Type: TEXT
         */
        String DEFAULT_LANG = "default_lang";

        /**
         * The user's birthday.<br />
         * Type: INTEGER
         */
        String DATE_OF_BIRTH = "date_of_birth";

        /**
         * Birthday privacy.<br />
         * 0 for hide, 1 for show only date, 2 for show all included year.<br />
         * Type: INTEGER
         */
        String BDAY_PRIVACY = "bday_privacy";

        /**
         * The user's full name.<br />
         * Type: TEXT
         */
        String FULL_NAME = "full_name";

        /**
         * The user's gender.<br />
         * 0 for female, 1 for male, 2 is not stating or other.
         * Type: INTEGER
         */
        String GENDER = "gender";

        /**
         * User's Karma value.<br />
         * Type: DOUBLE NOT NULL
         */
        String KARMA = "karma";

        /**
         * How many friends has the user recruited.<br />
         * Type: INTEGER
         */
        String RECRUITED = "recruited";

        /**
         * User's relationship status.<br />
         * Type: INTEGER (long)
         */
        String RELATIONSHIP = "relationship";

        /**
         * User's name color.<br />
         * Type: TEXT
         */
        String NAME_COLOR = "name_color";
        //endregion Column name

        String[] COLUMNS = {_ID            , ACCOUNT_ID, ID      , NICK_NAME        , DISPLAY_NAME ,
                HAS_PROFILE_IMAGE, AVATAR  , LOCATION , DEFAULT_LANG, DATE_OF_BIRTH, BDAY_PRIVACY,
                FULL_NAME, GENDER  , KARMA               , RECRUITED, RELATIONSHIP, NAME_COLOR};

        String[] TYPES   = {TYPE_INT_UNIQUE, TYPE_INT  , TYPE_INT, TYPE_TEXT_NOT_NULL, TYPE_TEXT   ,
                TYPE_BOOLEAN     , TYPE_INT, TYPE_TEXT, TYPE_TEXT   , TYPE_INT     , TYPE_INT    ,
                TYPE_TEXT, TYPE_INT, TYPE_DOUBLE_NOT_NULL, TYPE_INT , TYPE_INT    , TYPE_TEXT };
    }
}
