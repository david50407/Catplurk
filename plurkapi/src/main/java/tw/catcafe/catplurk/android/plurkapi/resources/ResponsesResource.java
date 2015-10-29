package tw.catcafe.catplurk.android.plurkapi.resources;

import org.mariotaku.restfu.annotation.method.GET;
import org.mariotaku.restfu.annotation.param.Query;

import tw.catcafe.catplurk.android.plurkapi.PlurkException;
import tw.catcafe.catplurk.android.plurkapi.model.ResponsesResponse;

/**
 * @author Davy
 */
public interface ResponsesResource {
    @GET("/Responses/get")
    ResponsesResponse getResponses(@Query("plurk_id") long plurkId,
                                   @Query("from_response") long fromResponse,
                                   @Query("count") long count) throws PlurkException;
}
