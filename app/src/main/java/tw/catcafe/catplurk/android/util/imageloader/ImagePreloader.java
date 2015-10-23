package tw.catcafe.catplurk.android.util.imageloader;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import tw.catcafe.catplurk.android.Constants;

import static tw.catcafe.catplurk.android.util.NetworkUtil.isOnWifi;

/**
 * @author Davy
 */
public class ImagePreloader implements Constants {
    public static final String LOGTAG = "ImagePreloader";

    private final Context mContext;
    private final SharedPreferences mPreferences;
    private final DiskCache mDiskCache;
    private final ImageLoader mImageLoader;

    public ImagePreloader(final Context context, final ImageLoader loader) {
        mContext = context;
        mPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        mImageLoader = loader;
        mDiskCache = loader.getDiskCache();
    }

    public File getCachedImageFile(final String url) {
        if (url == null) return null;
        final File cache = mDiskCache.get(url);
        if (ImageValidator.isValid(ImageValidator.checkImageValidity(cache)))
            return cache;
        else {
            preloadImage(url);
        }
        return null;
    }

    public void preloadImage(final String url) {
        if (TextUtils.isEmpty(url)) return;
        // TODO: Configurable if only wifi
//        if (!isOnWifi(mContext) && mPreferences.getBoolean(KEY_PRELOAD_WIFI_ONLY, true)) return;
        if (!isOnWifi(mContext)) return;
        mImageLoader.loadImage(url, null);
    }

}
