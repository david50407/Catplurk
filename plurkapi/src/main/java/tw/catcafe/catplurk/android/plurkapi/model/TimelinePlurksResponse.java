package tw.catcafe.catplurk.android.plurkapi.model;

import java.util.List;
import java.util.Map;

/**
 * @author Davy
 */
public interface TimelinePlurksResponse extends PlurkApiResponse {
    List<Plurk> getPlurks();
    Map<String, User> getPlurkUsers();
}
