package tw.catcafe.catplurk.android.util.imageloader;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.BaseDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Davy
 */
public class ReadOnlyDiskLRUNameCache extends BaseDiskCache {
    public ReadOnlyDiskLRUNameCache(File cacheDir) {
        super(cacheDir);
    }

    public ReadOnlyDiskLRUNameCache(File cacheDir, File reserveCacheDir) {
        super(cacheDir, reserveCacheDir);
    }

    @Override
    public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException {
        return false;
    }

    @Override
    public boolean save(String imageUri, Bitmap bitmap) throws IOException {
        return false;
    }

    @Override
    public boolean remove(String imageUri) {
        return false;
    }

    @Override
    public void clear() {
        // No-op
    }

    @Override
    protected File getFile(String imageUri) {
        String fileName = fileNameGenerator.generate(imageUri) + ".0";
        File dir = cacheDir;
        if ((!cacheDir.exists()) && (!cacheDir.mkdirs()) &&
                (reserveCacheDir != null) && ((reserveCacheDir.exists()) || (reserveCacheDir.mkdirs()))) {
            dir = reserveCacheDir;
        }
        return new File(dir, fileName);
    }

    public ReadOnlyDiskLRUNameCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator) {
        super(cacheDir, reserveCacheDir, fileNameGenerator);
    }
}