package tw.catcafe.catplurk.android.plurkapi.model.impl;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tw.catcafe.catplurk.android.plurkapi.model.Plurk;
import tw.catcafe.catplurk.android.plurkapi.model.User;

/**
 * @author Davy
 */
@JsonObject(fieldNamingPolicy = JsonObject.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
public class ProfileResponseImpl implements tw.catcafe.catplurk.android.plurkapi.model.ProfileResponse {
    @JsonField
    User user_info;

    @Override
    public User getUserInfo() {
        return user_info;
    }
}
