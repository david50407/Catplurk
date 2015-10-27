package tw.catcafe.catplurk.android.plurkapi.model.impl;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.catcafe.catplurk.android.plurkapi.model.Plurk;
import tw.catcafe.catplurk.android.plurkapi.model.Response;
import tw.catcafe.catplurk.android.plurkapi.model.User;

/**
 * @author Davy
 */
@JsonObject(fieldNamingPolicy = JsonObject.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
public class ResponsesResponseImpl implements tw.catcafe.catplurk.android.plurkapi.model.ResponsesResponse {
    @JsonField
    ArrayList<Response> responses;
    @JsonField
    HashMap<String, User> friends;
    @JsonField
    long responseCount;
    @JsonField
    long responsesSeen;

    public Map<String, User> getFriends() {
        return friends;
    }

    public long getResponseCount() {
        return responseCount;
    }

    public long getResponsesSeen() {
        return responsesSeen;
    }

    public List<Response> getResponses() {
        return responses;
    }
}
