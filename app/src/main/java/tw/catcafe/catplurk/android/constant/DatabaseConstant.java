package tw.catcafe.catplurk.android.constant;

/**
 * @author Davy
 */
public interface DatabaseConstant {
    String DATABASE_NAME = "catplurk.sqlite";
    int DATABASE_VERSION = 2;

    //region Table Ids
    int TABLE_ID_ACCOUNTS = 1;
    int TABLE_ID_PLURKS = 2;
    int TABLE_ID_USERS = 3;

    int VIRTUAL_TABLE_ID_DATABASE_READY = 101;
    //endregion Table Ids
}
