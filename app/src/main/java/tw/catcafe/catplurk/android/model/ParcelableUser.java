package tw.catcafe.catplurk.android.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

import java.util.Date;

import tw.catcafe.catplurk.android.provider.CatPlurkDataStore.Users;
import tw.catcafe.catplurk.android.support.util.ObjectCursor;

/**
 * Created by Davy on 2015/7/16.
 */
@ParcelablePlease(allFields = false)
@JsonObject(fieldNamingPolicy = JsonObject.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
public class ParcelableUser implements Parcelable, tw.catcafe.catplurk.android.plurkapi.model.User {
    public static final Creator<ParcelableUser> CREATOR = new Creator<ParcelableUser>() {
        @Override
        public ParcelableUser createFromParcel(Parcel source) {
            ParcelableUser parcelableUser = new ParcelableUser();
            ParcelableUserParcelablePlease.readFromParcel(parcelableUser, source);
            return parcelableUser;
        }

        @Override
        public ParcelableUser[] newArray(int size) {
            return new ParcelableUser[size];
        }
    };

    @JsonField
    @ParcelableThisPlease
    public long accountId;
    @JsonField
    @ParcelableThisPlease
    public boolean detailed;

    @JsonField
    @ParcelableThisPlease
    public long id;
    @JsonField
    @ParcelableThisPlease
    public boolean verifiedAccount;
    @JsonField
    @ParcelableThisPlease
    public String nickName;
    @JsonField
    @ParcelableThisPlease
    public String displayName;
    @JsonField
    @ParcelableThisPlease
    public String fullName;

    @JsonField
    @ParcelableThisPlease
    public boolean hasProfileImage;
    @JsonField
    @ParcelableThisPlease
    public long avatar = -1;
    @JsonField
    @ParcelableThisPlease
    public String nameColor;

    @JsonField
    @ParcelableThisPlease
    public String location;

    @JsonField
    @ParcelableThisPlease
    public String defaultLang;

    @JsonField
    @ParcelableThisPlease
    public Date dateOfBirth;
    @JsonField
    @ParcelableThisPlease
    public BirthdayPrivacy bdayPrivacy = BirthdayPrivacy.NULL;

    @JsonField
    @ParcelableThisPlease
    public Gender gender;

    @JsonField
    @ParcelableThisPlease
    public double karma;

    @JsonField
    @ParcelableThisPlease
    public Relationship relationship = Relationship.UNKNOWN;

    @JsonField
    @ParcelableThisPlease
    public long recruited;
    @JsonField
    @ParcelableThisPlease
    public long friendsCount;
    @JsonField
    @ParcelableThisPlease
    public long fansCount;

    @JsonField
    @ParcelableThisPlease
    public boolean areFriends;
    @JsonField
    @ParcelableThisPlease
    public boolean isFan;
    @JsonField
    @ParcelableThisPlease
    public boolean isFollowing;
    @JsonField
    @ParcelableThisPlease
    public boolean hasReadPermission;

    @JsonField
    @ParcelableThisPlease
    public long privacy;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelableUserParcelablePlease.writeToParcel(this, dest, flags);
    }

    @Override
    public int compareTo(@NonNull final tw.catcafe.catplurk.android.plurkapi.model.User that) {
        return (int) (id - that.getUserId());
    }

    // region interface
    @Override
    public long getAccountId() {
        return accountId;
    }

    @Override
    public long getUserId() {
        return id;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public String getDefaultLang() {
        return defaultLang;
    }

    @Override
    public String getNickname() {
        return nickName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public Relationship getRelationship() {
        return relationship;
    }

    @Override
    public String getFullname() { return fullName; }

    @Override
    public long getAvatar() {
        return avatar;
    }

    @Override
    public boolean hasProfileImage() {
        return hasProfileImage;
    }

//    @Override
//    public NameColor getNameColor() {
//        switch (nameColor) {
//            case "E41227":
//                return NameColor.RED;
//            case "2264D6":
//                return NameColor.BLUE;
//            case "0A9C17":
//                return NameColor.GREEN;
//            default:
//                return NameColor.BLACK;
//        }
//    }

    @Override
    public String getNameColor() {
        return nameColor;
    }

    @Override
    public Date getDateOfBirth() {
        if (dateOfBirth == null) return new Date(0);
        return dateOfBirth;
    }

    @Override
    public BirthdayPrivacy getBdayPrivacy() {
        return bdayPrivacy;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public double getKarma() {
        return karma;
    }

    @Override
    public long getRecruited() {
        return recruited;
    }
    // endregion interface

    //region Cursor Indices
    public static class CursorIndices extends ObjectCursor.CursorIndices<ParcelableUser> {
        public final int _id, id, account_id, nick_name, display_name, has_profile_image, avatar,
                location, default_lang, date_of_birth, bday_privacy, full_name, gender, karma,
                recurited, relationship, name_color;

        public CursorIndices(final Cursor cursor) {
            super(cursor);
            _id = cursor.getColumnIndex(Users._ID);
            account_id = cursor.getColumnIndex(Users.ACCOUNT_ID);
            id = cursor.getColumnIndex(Users.ID);
            nick_name = cursor.getColumnIndex(Users.NICK_NAME);
            display_name = cursor.getColumnIndex(Users.DISPLAY_NAME);
            has_profile_image = cursor.getColumnIndex(Users.HAS_PROFILE_IMAGE);
            avatar = cursor.getColumnIndex(Users.AVATAR);
            location = cursor.getColumnIndex(Users.LOCATION);
            default_lang = cursor.getColumnIndex(Users.DEFAULT_LANG);
            date_of_birth = cursor.getColumnIndex(Users.DATE_OF_BIRTH);
            bday_privacy = cursor.getColumnIndex(Users.BDAY_PRIVACY);
            full_name = cursor.getColumnIndex(Users.FULL_NAME);
            gender = cursor.getColumnIndex(Users.GENDER);
            karma = cursor.getColumnIndex(Users.KARMA);
            recurited = cursor.getColumnIndex(Users.RECRUITED);
            relationship = cursor.getColumnIndex(Users.RELATIONSHIP);
            name_color = cursor.getColumnIndex(Users.NAME_COLOR);
        }

        @Override
        public ParcelableUser newObject(Cursor cursor) {
            final ParcelableUser user = new ParcelableUser();
            user.accountId = cursor.getLong(account_id);
            user.id = cursor.getLong(id);
            user.nickName = cursor.getString(nick_name);
            user.displayName = cursor.getString(display_name);
            user.hasProfileImage = cursor.getInt(has_profile_image) == 1;
            user.avatar = cursor.getLong(avatar);
            user.location = cursor.getString(location);
            user.defaultLang = cursor.getString(default_lang);
            user.dateOfBirth = new Date(cursor.getLong(date_of_birth));
            user.bdayPrivacy = BirthdayPrivacy.values()[cursor.getInt(bday_privacy)];
            user.fullName = cursor.getString(full_name);
            user.gender = Gender.values()[cursor.getInt(gender)];
            user.karma = cursor.getDouble(karma);
            user.recruited = cursor.getLong(recurited);
            user.relationship = Relationship.values()[cursor.getInt(relationship)];
            user.nameColor = cursor.getString(name_color);
            return user;
        }

        @Override
        public String toString() {
            // TODO
            return "CursorIndices{}";
        }
    }
    //endregion Cursor indices
}
