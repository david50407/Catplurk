package tw.catcafe.catplurk.android.plurkapi.model;

import java.util.Date;

/**
 * @author Davy
 */
public interface Response extends Comparable<Response>, PlurkApiResponse {
    long getAccountId();
    long getUserId();
    long getPlurkId();
    long getResponseId();
    String getLang();
    String getContent();
    String getContentRaw();
    String getQualifier();
    String getQualifierTranslated();
    Date getPosted();
    String getHandle();
    boolean getMyAnonymous();
}
