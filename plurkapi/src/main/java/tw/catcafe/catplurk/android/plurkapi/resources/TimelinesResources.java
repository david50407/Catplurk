package tw.catcafe.catplurk.android.plurkapi.resources;

import org.mariotaku.restfu.annotation.method.GET;
import org.mariotaku.restfu.annotation.param.Query;

import tw.catcafe.catplurk.android.plurkapi.PlurkException;
import tw.catcafe.catplurk.android.plurkapi.model.Paging;
import tw.catcafe.catplurk.android.plurkapi.model.TimelinePlurksResponse;

/**
 * @author Davy
 */
public interface TimelinesResources {
    @GET("/Timeline/getPlurks")
    TimelinePlurksResponse getTimelinePlurks(@Query Paging paging) throws PlurkException;
}
