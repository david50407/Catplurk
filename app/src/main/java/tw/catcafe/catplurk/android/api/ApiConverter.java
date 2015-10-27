package tw.catcafe.catplurk.android.api;

import tw.catcafe.catplurk.android.model.ParcelablePlurk;
import tw.catcafe.catplurk.android.model.ParcelableResponse;
import tw.catcafe.catplurk.android.model.ParcelableUser;
import tw.catcafe.catplurk.android.plurkapi.PlurkConverter;
import tw.catcafe.catplurk.android.plurkapi.TypeConverterMapper;
import tw.catcafe.catplurk.android.plurkapi.model.Plurk;
import tw.catcafe.catplurk.android.plurkapi.model.Response;
import tw.catcafe.catplurk.android.plurkapi.model.User;

/**
 * @author Davy
 */
public class ApiConverter extends PlurkConverter {
    static {
        TypeConverterMapper.register(Plurk.class, ParcelablePlurk.class);
        TypeConverterMapper.register(User.class, ParcelableUser.class);
        TypeConverterMapper.register(Response.class, ParcelableResponse.class);
    }
}
