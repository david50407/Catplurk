package tw.catcafe.catplurk.android.util.imageloader;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;

/**
 * @author Davy
 */
public class URLFileNameGenerator implements FileNameGenerator {

    private final Md5FileNameGenerator mGenerator;

    public URLFileNameGenerator() {
        mGenerator = new Md5FileNameGenerator();
    }

    @Override
    public String generate(final String imageUri) {
        if (imageUri == null) return null;
        return mGenerator.generate(imageUri.replaceFirst("https?://", ""));
    }

}

