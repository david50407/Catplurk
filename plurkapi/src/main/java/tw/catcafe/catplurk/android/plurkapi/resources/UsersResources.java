package tw.catcafe.catplurk.android.plurkapi.resources;

import org.mariotaku.restfu.annotation.method.GET;

import tw.catcafe.catplurk.android.plurkapi.PlurkException;
import tw.catcafe.catplurk.android.plurkapi.model.User;

/**
 * @author Davy
 */
public interface UsersResources {
    @GET("/Users/me")
    User getMyInfomation() throws PlurkException;
}
