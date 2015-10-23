package tw.catcafe.catplurk.android.plurkapi.model;

import java.util.Date;

/**
 * @author Davy
 */
public interface Plurk extends Comparable<Plurk>, PlurkApiResponse {
    long getAccountId();
    long getPlurkId();
    String getQualifier();
    String getQualifierTranslated();
    ReadState isUnread();
//    long getPlurkType();
//    long getUserId();
    long getOwnerId();
    Date getPosted();
    Commentable getNoComments();
    String getContent();
    String getContentRaw();
    long getResponseCount();
    long getResponsesSeen();
    long[] getLimitedTo();
    boolean getFavorite();
    long getFavoriteCount();
    long[] getFavorers();
    boolean getReplurkable();
    boolean getReplurked();
    long getReplurkerId();
    long getReplurkersCount();
    long[] getReplurkers();

    enum ReadState {
        READ,
        UNREAD,
        MUTED
    }

    enum Commentable {
        ENABLE,
        DISABLE,
        FRIENDS_ONLY
    }
}
