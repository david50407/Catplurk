package tw.catcafe.catplurk.android.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

import java.util.Comparator;
import java.util.Date;

import tw.catcafe.catplurk.android.provider.CatPlurkDataStore.Plurks;
import tw.catcafe.catplurk.android.support.util.ObjectCursor;
import tw.catcafe.catplurk.android.util.ArrayUtils;

/**
 * Created by Davy on 2015/7/16.
 */
@ParcelablePlease(ignorePrivateFields = true)
@JsonObject(fieldNamingPolicy = JsonObject.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
public class ParcelablePlurk implements Parcelable, tw.catcafe.catplurk.android.plurkapi.model.Plurk {
    @JsonField
    public long accountId;

    @JsonField
    public long plurkId;

    @JsonField
    public String qualifier;
    @JsonField
    public String qualifierTranslated;
    @JsonField
    public ReadState isUnread;
    @JsonField
    public long ownerId;
    @JsonField
    public Date posted;

    @JsonField
    public Commentable noComments;
    @JsonField
    public String content;
    @JsonField
    public String contentRaw;

    @JsonField
    public long responseCount;
    @JsonField
    public long responsesSeen;

    @JsonField
    public long[] limitedTo;

    @JsonField
    public boolean favorite;
    @JsonField
    public long favoriteCount;
    @JsonField
    public long[] favorers;

    @JsonField
    public boolean replurkable;
    @JsonField
    public boolean replurked;
    @JsonField
    public long replurkerId;
    @JsonField
    public long replurkersCount;
    @JsonField
    public long[] replurkers;

    @JsonField
    public boolean isGap;

    //region Getter
    public long getAccountId() {
        return accountId;
    }
    public long getPlurkId() {
        return plurkId;
    }
    public String getQualifier() {
        return qualifier;
    }
    public String getQualifierTranslated() {
        return qualifierTranslated;
    }
    public ReadState isUnread() {
        return isUnread;
    }
    public long getOwnerId() {
        return ownerId;
    }
    public Date getPosted() {
        return posted;
    }
    public Commentable getNoComments() {
        return noComments;
    }
    public String getContent() {
        return content;
    }
    public String getContentRaw() {
        return contentRaw;
    }
    public long getResponseCount() {
        return responseCount;
    }
    public long getResponsesSeen() {
        return responsesSeen;
    }
    public long[] getLimitedTo() {
        if (limitedTo == null) return new long[0];
        return limitedTo;
    }
    public boolean getFavorite() {
        return favorite;
    }
    public long getFavoriteCount() {
        return favoriteCount;
    }
    public long[] getFavorers() {
        if (favorers == null) return new long[0];
        return favorers;
    }
    public boolean getReplurkable() {
        return replurkable;
    }
    public boolean getReplurked() {
        return replurked;
    }
    public long getReplurkerId() {
        return replurkerId;
    }
    public long getReplurkersCount() {
        return replurkersCount;
    }
    public long[] getReplurkers() {
        if (replurkers == null) return new long[0];
        return replurkers;
    }
    //endregion Getter

    //region Comparator
    @Override
    public int compareTo(@NonNull final tw.catcafe.catplurk.android.plurkapi.model.Plurk another) {
        final long diff = another.getPlurkId() - plurkId;
        if (diff > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (diff < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        return (int) diff;
    }

    public static final Comparator<tw.catcafe.catplurk.android.plurkapi.model.Plurk> REVERSE_ID_COMPARATOR = (lhs, rhs) -> {
        final long diff = lhs.getPlurkId() - rhs.getPlurkId();
        if (diff > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (diff < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        return (int) diff;
    };
    public static final Comparator<tw.catcafe.catplurk.android.plurkapi.model.Plurk> TIMESTAMP_COMPARATOR = (lhs, rhs) -> {
        final long diff = rhs.getPosted().getTime() - lhs.getPosted().getTime();
        if (diff > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (diff < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        return (int) diff;
    };
    //endregion Comparator

    //region ParcelablePlease
    public static final Creator<ParcelablePlurk> CREATOR = new Creator<ParcelablePlurk>() {
        @Override
        public ParcelablePlurk createFromParcel(Parcel source) {
            ParcelablePlurk parcelablePlurk = new ParcelablePlurk();
            ParcelablePlurkParcelablePlease.readFromParcel(parcelablePlurk, source);
            return parcelablePlurk;
        }

        @Override
        public ParcelablePlurk[] newArray(int size) {
            return new ParcelablePlurk[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelablePlurkParcelablePlease.writeToParcel(this, dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    //endregion ParcelablePlease

    // TODO: Use annotation to auto complete
    //region Cursor Indices
    public static final class CursorIndices extends ObjectCursor.CursorIndices<ParcelablePlurk> {
        public final int _id, account_id, plurk_id, qualifier, qualifier_translated,
                is_unread, owner_id, posted, no_comments, content, content_raw,
                response_count, responses_seen, limited_to, favorite, favorite_count,
                favorers, replurkable, replurked, replurker_id, replurkers_count,
                replurkers;
        public final int is_gap;
        
        public CursorIndices(final Cursor cursor) {
            super(cursor);
            _id = cursor.getColumnIndex(Plurks._ID);
            account_id = cursor.getColumnIndex(Plurks.ACCOUNT_ID);
            plurk_id = cursor.getColumnIndex(Plurks.PLURK_ID);
            qualifier = cursor.getColumnIndex(Plurks.QUALIFIER);
            qualifier_translated = cursor.getColumnIndex(Plurks.QUALIFIER_TRANSLATED);
            is_unread = cursor.getColumnIndex(Plurks.IS_UNREAD);
            owner_id = cursor.getColumnIndex(Plurks.OWNER_ID);
            posted = cursor.getColumnIndex(Plurks.POSTED);
            no_comments = cursor.getColumnIndex(Plurks.NO_COMMENTS);
            content = cursor.getColumnIndex(Plurks.CONTENT);
            content_raw = cursor.getColumnIndex(Plurks.CONTENT_RAW);
            response_count = cursor.getColumnIndex(Plurks.RESPONSE_COUNT);
            responses_seen = cursor.getColumnIndex(Plurks.RESPONSES_SEEN);
            limited_to = cursor.getColumnIndex(Plurks.LIMITED_TO);
            favorite = cursor.getColumnIndex(Plurks.FAVORITE);
            favorite_count = cursor.getColumnIndex(Plurks.FAVORITE_COUNT);
            favorers = cursor.getColumnIndex(Plurks.FAVORERS);
            replurkable = cursor.getColumnIndex(Plurks.REPLURKABLE);
            replurked = cursor.getColumnIndex(Plurks.REPLURKED);
            replurker_id = cursor.getColumnIndex(Plurks.REPLURKER_ID);
            replurkers_count = cursor.getColumnIndex(Plurks.REPLURKERS_COUNT);
            replurkers = cursor.getColumnIndex(Plurks.REPLURKERS);

            is_gap = cursor.getColumnIndex(Plurks.IS_GAP);
        }

        @Override
        public ParcelablePlurk newObject(Cursor cursor) {
            final ParcelablePlurk plurk = new ParcelablePlurk();
            plurk.plurkId = cursor.getLong(plurk_id);
            plurk.accountId = cursor.getLong(account_id);
            plurk.qualifier = cursor.getString(qualifier);
            plurk.qualifierTranslated = cursor.getString(qualifier_translated);
            plurk.isUnread = ReadState.values()[cursor.getInt(is_unread)];
            plurk.ownerId = cursor.getLong(owner_id);
            plurk.posted = new Date(cursor.getLong(posted));
            plurk.noComments = Commentable.values()[cursor.getInt(no_comments)];
            plurk.content = cursor.getString(content);
            plurk.contentRaw = cursor.getString(content_raw);
            plurk.responseCount = cursor.getLong(response_count);
            plurk.responsesSeen = cursor.getLong(responses_seen);
            plurk.limitedTo = ArrayUtils.parseLongArray(cursor.getString(limited_to), ',');
            plurk.favorite = cursor.getLong(favorite) == 1;
            plurk.favoriteCount = cursor.getLong(favorite_count);
            plurk.favorers = ArrayUtils.parseLongArray(cursor.getString(favorers), ',');
            plurk.replurkable = cursor.getLong(replurkable) == 1;
            plurk.replurked = cursor.getLong(replurked) == 1;
            plurk.replurkerId = cursor.getLong(replurker_id);
            plurk.replurkersCount = cursor.getLong(replurkers_count);
            plurk.replurkers = ArrayUtils.parseLongArray(cursor.getString(replurkers), ',');

            plurk.isGap = cursor.getLong(is_gap) == 1;
            return plurk;
            // return new Plurk(cursor, this);
        }

        @Override
        public String toString() {
            // TODO
            return "CursorIndices{}";
        }
    }
    //endregion Cursor Indices
}
