package tw.catcafe.catplurk.android.util.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author Davy
 */
public class ImageLoaderWrapper {
    private final ImageLoader mImageLoader;
    private final DisplayImageOptions mDisplayImageOptions;

    public ImageLoaderWrapper(final ImageLoader imageLoader) {
        mImageLoader = imageLoader;
        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .build();
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void clearFileCache() {
        mImageLoader.clearDiskCache();
    }

    public void clearMemoryCache() {
        mImageLoader.clearMemoryCache();
    }

    public void displayPreviewImage(final String uri, final ImageView view) {
        mImageLoader.displayImage(uri, view, mDisplayImageOptions);
    }

    public void displayImage(final ImageView view, final String url, DisplayImageOptions options) {
        mImageLoader.displayImage(url, view, options);
    }

    public void displayImage(final ImageView view, final String url) {
        mImageLoader.displayImage(url, view, mDisplayImageOptions);
    }
}
