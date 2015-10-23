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
public class TimelinePlurksResponseImpl implements tw.catcafe.catplurk.android.plurkapi.model.TimelinePlurksResponse {
    @JsonField
    ArrayList<Plurk> plurks;
    @JsonField
    HashMap<String, User> plurkUsers;

    @Override
    public List<Plurk> getPlurks() {
        return plurks;
    }

    @Override
    public HashMap<String, User> getPlurkUsers() {
        return plurkUsers;
    }
}
