package tw.catcafe.catplurk.android.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

import org.w3c.dom.CDATASection;

import java.util.ArrayList;
import java.util.Date;

import tw.catcafe.catplurk.android.plurkapi.model.Plurk;
import tw.catcafe.catplurk.android.provider.CatPlurkDataStore;
import tw.catcafe.catplurk.android.support.util.ObjectCursor;
import tw.catcafe.catplurk.android.util.ArrayUtils;

/**
 * Created by Davy on 2015/7/16.
 */
@ParcelablePlease(ignorePrivateFields = true)
@JsonObject(fieldNamingPolicy = JsonObject.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
public class ParcelableResponse implements Parcelable, tw.catcafe.catplurk.android.plurkapi.model.Response {
    @JsonField
    public long accountId;

    @JsonField
    public long userId;
    @JsonField
    public long plurkId;
    @JsonField
    public long id;

    @JsonField
    public String lang;
    @JsonField
    public String qualifier;
    @JsonField
    public String qualifierTranslated;
    @JsonField
    public Date posted;
    @JsonField
    public String content;
    @JsonField
    public String contentRaw;

    @JsonField
    public String handle;
    @JsonField
    public boolean myAnonymous;

    @JsonField
    public boolean isGap;

    //region Getter
    public long getAccountId() {
        return accountId;
    }

    public long getUserId() {
        return userId;
    }

    public long getPlurkId() {
        return plurkId;
    }

    public long getResponseId() {
        return id;
    }

    public String getLang() {
        return lang;
    }

    public String getContent() {
        return content;
    }

    public String getContentRaw() {
        return contentRaw;
    }

    public String getQualifier() {
        return qualifier;
    }

    public String getQualifierTranslated() {
        return qualifierTranslated;
    }

    public Date getPosted() {
        return posted;
    }

    public String getHandle() {
        return handle;
    }

    public boolean getMyAnonymous() {
        return myAnonymous;
    }
    //endregion Getter

    //region Compartor
    @Override
    public int compareTo(@NonNull final tw.catcafe.catplurk.android.plurkapi.model.Response another) {
        final long diff = another.getResponseId() - id;
        if (diff > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (diff < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        return (int) diff;
    }
    //endregion Compartor

    //region ParcelablePlease
    public static final Creator<ParcelableResponse> CREATOR = new Creator<ParcelableResponse>() {
        @Override
        public ParcelableResponse createFromParcel(Parcel source) {
            ParcelableResponse parcelableResponse = new ParcelableResponse();
            ParcelableResponseParcelablePlease.readFromParcel(parcelableResponse, source);
            return parcelableResponse;
        }

        @Override
        public ParcelableResponse[] newArray(int size) {
            return new ParcelableResponse[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelableResponseParcelablePlease.writeToParcel(this, dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    //endregion ParcelablePlease

    // TODO: Use annotation to auto complete
    //region Cursor Indices
    public static final class CursorIndices extends ObjectCursor.CursorIndices<ParcelableResponse> {
        public final int _id, account_id, user_id, plurk_id, response_id, lang,
                qualifier, qualifier_translated, posted, content, content_raw, handle, my_anonymous;
        public final int is_gap;

        public CursorIndices(final Cursor cursor) {
            super(cursor);
            _id = cursor.getColumnIndex(CatPlurkDataStore.Responses._ID);
            account_id = cursor.getColumnIndex(CatPlurkDataStore.Responses.ACCOUNT_ID);
            user_id = cursor.getColumnIndex(CatPlurkDataStore.Responses.USER_ID);
            plurk_id = cursor.getColumnIndex(CatPlurkDataStore.Responses.PLURK_ID);
            response_id = cursor.getColumnIndex(CatPlurkDataStore.Responses.RESPONSE_ID);
            lang = cursor.getColumnIndex(CatPlurkDataStore.Responses.LANG);
            qualifier = cursor.getColumnIndex(CatPlurkDataStore.Responses.QUALIFIER);
            qualifier_translated = cursor.getColumnIndex(CatPlurkDataStore.Responses.QUALIFIER_TRANSLATED);
            posted = cursor.getColumnIndex(CatPlurkDataStore.Responses.POSTED);
            content = cursor.getColumnIndex(CatPlurkDataStore.Responses.CONTENT);
            content_raw = cursor.getColumnIndex(CatPlurkDataStore.Responses.CONTENT_RAW);
            handle = cursor.getColumnIndex(CatPlurkDataStore.Responses.HANDLE);
            my_anonymous = cursor.getColumnIndex(CatPlurkDataStore.Responses.MY_ANONYMOUS);

            is_gap = cursor.getColumnIndex(CatPlurkDataStore.Responses.IS_GAP);
        }

        @Override
        public ParcelableResponse newObject(Cursor cursor) {
            final ParcelableResponse response = new ParcelableResponse();
            response.accountId = cursor.getLong(account_id);
            response.plurkId = cursor.getLong(plurk_id);
            response.id = cursor.getLong(response_id);
            response.userId = cursor.getLong(user_id);
            response.lang = cursor.getString(lang);
            response.qualifier = cursor.getString(qualifier);
            response.qualifierTranslated = cursor.getString(qualifier_translated);
            response.posted = new Date(cursor.getLong(posted));
            response.content = cursor.getString(content);
            response.contentRaw = cursor.getString(content_raw);
            response.handle = cursor.getString(handle);
            response.myAnonymous = cursor.getLong(my_anonymous) == 1;

            response.isGap = cursor.getLong(is_gap) == 1;
            return response;
        }

        @Override
        public String toString() {
            // TODO
            return "CursorIndices{}";
        }
    }
    //endregion Cursor Indices
}
