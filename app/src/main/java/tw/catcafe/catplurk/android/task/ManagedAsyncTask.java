package tw.catcafe.catplurk.android.task;

import android.content.Context;
import android.os.AsyncTask;

import tw.catcafe.catplurk.android.Constants;
import tw.catcafe.catplurk.android.util.AsyncTaskManager;

/**
 * @author Davy
 */
public abstract class ManagedAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
        implements Constants {
    private final AsyncTaskManager manager;
    private final Context context;
    private final String tag;

    public ManagedAsyncTask(final Context context, final AsyncTaskManager manager) {
        this(context, manager, null);
    }

    public ManagedAsyncTask(final Context context, final AsyncTaskManager manager, final String tag) {
        this.manager = manager;
        this.context = context;
        this.tag = tag;
    }

    public Context getContext() {
        return context;
    }

    public String getTag() {
        return tag;
    }
}
