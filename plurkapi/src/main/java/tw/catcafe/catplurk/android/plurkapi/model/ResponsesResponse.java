package tw.catcafe.catplurk.android.plurkapi.model;

import java.util.List;
import java.util.Map;

/**
 * @author Davy
 */
public interface ResponsesResponse extends PlurkApiResponse {
    Map<String, User> getFriends();
    long getResponseCount();
    long getResponsesSeen();
    List<Response> getResponses();
}
