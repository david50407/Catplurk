package tw.catcafe.catplurk.android;

import tw.catcafe.catplurk.android.constant.AccountConstant;
import tw.catcafe.catplurk.android.constant.CatPlurkConstant;
import tw.catcafe.catplurk.android.constant.IntentConstant;

/**
 * Constants mixed-in all constants and must be mix-in into all application
 *
 * @author Davy
 */
public interface Constants extends CatPlurkConstant {
    // region Navigation ids
    int NAVIGATE_ALL                = 0;
    int NAVIGATE_UNREAD             = 1;
    int NAVIGATE_MY                 = 2;
    int NAVIGATE_RESPONDED          = 3;
    int NAVIGATE_PRIVATE            = 4;
    int NAVIGATE_LIKE_AND_REPLURK   = 5;
    // endregion Navigation ids

    //region Dir
    String DIR_NAME_IMAGE_CACHE = "image_cache";
    //endregion Dir
}
