package tw.catcafe.catplurk.android;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;
import com.squareup.otto.Bus;

import java.io.File;
import java.io.IOException;

import tw.catcafe.catplurk.android.util.AccountManager;
import tw.catcafe.catplurk.android.util.AsyncPlurkWrapper;
import tw.catcafe.catplurk.android.util.AsyncTaskManager;
import tw.catcafe.catplurk.android.util.content.CatPlurkSQLiteOpenHelper;
import tw.catcafe.catplurk.android.util.imageloader.ImageDownloader;
import tw.catcafe.catplurk.android.util.imageloader.ImageLoaderWrapper;
import tw.catcafe.catplurk.android.util.imageloader.ReadOnlyDiskLRUNameCache;
import tw.catcafe.catplurk.android.util.imageloader.URLFileNameGenerator;

/**
 * @author Davy
 */
public class Application extends android.app.Application implements Constants {
    private AsyncTaskManager mAsyncTaskManager;
    private Bus mMessageBus;
    private ImageDownloader mImageDownloader;
    private ImageLoader mImageLoader;
    private ImageLoaderWrapper mImageLoaderWrapper;
    private AsyncPlurkWrapper mPlurkWrapper;
    private DiskCache mDiskCache;
    private SQLiteOpenHelper mSQLiteOpenHelper;

    String[] TEST_QUALIFIER_TRANSLATED = {
            ":",
            ":",
            "愛",
            "喜歡",
            "分享",
            "給",
            "討厭",
            "想要",
            "期待",
            "需要",
            "打算",
            "希望",
            "問",
            "已經",
            "曾經",
            "好奇",
            "覺得",
            "想",
            "說",
            "正在",
            "偷偷說"
    };
    static String[] TEST_QUALIFIER = {
            ":",
            "freestyle",
            "loves",
            "likes",
            "shares",
            "gives",
            "hates",
            "wants",
            "wishes",
            "needs",
            "will",
            "hopes",
            "asks",
            "has",
            "was",
            "wonders",
            "feels",
            "thinks",
            "says",
            "is",
            "whispers"
    };

    static String[] TEST_CONTENTS = {
            "外了則教下的雖文告了跟東查綠著人教必是，紀有世動河中反！紅運以建家當盡今我過可知，消因今部認草他沒這情義在自主南的表意；麼有告，樂的立書報樹小滿特……較光小調清了立去媽老。\n" +
                    "\n" +
                    "四先弟，行明臉景過那來原好他！金說第工交這為各史。上土報種這：教民政得節也另上之重角找合顧日師大算。",
            "太物嚴專過飛專思快；智是的公國感水；花子油西為有有不超給港車！光了要長研意花；要總外手或的，因行是表了雖國，氣子了國時？收年傳文、快馬紅房，以了可投不員電計，是推方服操。意當經文造子好；方錯據地如國賣持面：國由公路；故所音作到要小他鄉事修新縣為，光求事西或到點數生西這沒水不他然事海。",
            "放樂法。\n" +
                    "\n" +
                    "道臺分分他，是市出。時機空一，學同部正民童，司元了。\n" +
                    "\n" +
                    "院家常界人以親弟意然下爸黨心還明我值給意統這是是！在子衣要充們毛步學正進；位境投公外其與年從以老的。傷自的此天足報產問工格票靜相的面樂的美求成是著；政鄉整西老旅想……著他長的信系風者林就斯的了家合相線總。",
            "結有一，兩我時裡同己來們主天，文前兩喜甚一品基分人照而個河人話後一城費本，和驚來……地子論制教半。式軍消位經你行住以關我人克，指似景也，老道走師手間教人改水何弟不才股因：父事度據中根使病己得雖些後這還。人的校：各兒收魚到後產代說整？\n" +
                    "\n" +
                    "良來界求覺有寫沒有說國基有。"
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mMessageBus = new Bus();

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                getImageLoaderWrapper().displayImage(imageView, uri.toString());
            }
        });
    }

    @NonNull
    public static Application getInstance(@NonNull Context context) {
        return (Application) context.getApplicationContext();
    }

    public AsyncTaskManager getAsyncTaskManager() {
        if (mAsyncTaskManager != null) return mAsyncTaskManager;
        return mAsyncTaskManager = AsyncTaskManager.getInstance();
    }

    @NonNull
    public AccountManager getAccountManager() {
        return new AccountManager(this);
    }

    @Nullable
    public Bus getMessageBus() {
        return mMessageBus;
    }

    @NonNull
    public ImageDownloader getImageDownloader() {
        if (mImageDownloader != null) return mImageDownloader;
        return mImageDownloader = new ImageDownloader(this);
    }

    @NonNull
    public ImageLoader getImageLoader() {
        if (mImageLoader != null) return mImageLoader;
        final ImageLoader loader = ImageLoader.getInstance();
        final ImageLoaderConfiguration.Builder cb = new ImageLoaderConfiguration.Builder(this);
        cb.threadPriority(Thread.NORM_PRIORITY - 2);
        cb.denyCacheImageMultipleSizesInMemory();
        cb.tasksProcessingOrder(QueueProcessingType.LIFO);
        // cb.memoryCache(new ImageMemoryCache(40));
        cb.diskCache(getDiskCache());
        cb.imageDownloader(getImageDownloader());
        L.writeDebugLogs(BuildConfig.DEBUG);
        loader.init(cb.build());
        return mImageLoader = loader;
    }

    @NonNull
    public ImageLoaderWrapper getImageLoaderWrapper() {
        if (mImageLoaderWrapper != null) return mImageLoaderWrapper;
        return mImageLoaderWrapper = new ImageLoaderWrapper(getImageLoader());
    }

    public DiskCache getDiskCache() {
        if (mDiskCache != null) return mDiskCache;
        return mDiskCache = createDiskCache(DIR_NAME_IMAGE_CACHE);
    }

    public SQLiteOpenHelper getSQLiteOpenHelper() {
        if (mSQLiteOpenHelper != null) return mSQLiteOpenHelper;
        return mSQLiteOpenHelper = new CatPlurkSQLiteOpenHelper(this, DATABASE_NAME, DATABASE_VERSION);
    }

    public AsyncPlurkWrapper getPlurkWrapper() {
        if (mPlurkWrapper != null) return mPlurkWrapper;
        return mPlurkWrapper = new AsyncPlurkWrapper(this);
    }

    //region Creators
    private DiskCache createDiskCache(final String dirName) {
        final File cacheDir = getBestCacheDir(this, dirName);
        final File fallbackCacheDir = getInternalCacheDir(this, dirName);
        final URLFileNameGenerator fileNameGenerator = new URLFileNameGenerator();
        final int cacheSize = 300;
        try {
            return new LruDiskCache(cacheDir, fallbackCacheDir, fileNameGenerator,
                    cacheSize * 1024 * 1024, 0);
        } catch (IOException e) {
            return new ReadOnlyDiskLRUNameCache(cacheDir, fallbackCacheDir, fileNameGenerator);
        }
    }


    //endregion Creators

    //region Helpers
    public static File getBestCacheDir(final Context context, final String cacheDirName) {
        if (context == null) throw new NullPointerException();
        final File extCacheDir;
        try {
            // Workaround for https://github.com/mariotaku/twidere/issues/138
            extCacheDir = context.getExternalCacheDir();
        } catch (final Exception e) {
            return new File(context.getCacheDir(), cacheDirName);
        }
        if (extCacheDir != null && extCacheDir.isDirectory()) {
            final File cacheDir = new File(extCacheDir, cacheDirName);
            if (cacheDir.isDirectory() || cacheDir.mkdirs()) return cacheDir;
        }
        return new File(context.getCacheDir(), cacheDirName);
    }

    public static File getInternalCacheDir(final Context context, final String cacheDirName) {
        if (context == null) throw new NullPointerException();
        final File cacheDir = new File(context.getCacheDir(), cacheDirName);
        if (cacheDir.isDirectory() || cacheDir.mkdirs()) return cacheDir;
        return new File(context.getCacheDir(), cacheDirName);
    }
    //endregion Helpers
}
