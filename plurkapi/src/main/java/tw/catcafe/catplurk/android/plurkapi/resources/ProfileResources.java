package tw.catcafe.catplurk.android.plurkapi.resources;

import org.mariotaku.restfu.annotation.method.GET;
import org.mariotaku.restfu.annotation.param.Query;

import tw.catcafe.catplurk.android.plurkapi.PlurkException;
import tw.catcafe.catplurk.android.plurkapi.model.PlurkResponse;
import tw.catcafe.catplurk.android.plurkapi.model.ProfileResponse;
import tw.catcafe.catplurk.android.plurkapi.model.User;

/**
 * @author Davy
 */
public interface ProfileResources {
    @GET("/Profile/getPublicProfile")
    ProfileResponse getPublicProfile(@Query("user_id") long user_id, @Query boolean minimal_data,
                                      @Query boolean include_plurks) throws PlurkException;
}
