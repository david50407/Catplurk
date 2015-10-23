package tw.catcafe.catplurk.android.util;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Davy
 */
public class AsyncTaskUtils
{
    public static final Executor DEFAULT_EXECUTOR = Executors.newFixedThreadPool(2);

    @SafeVarargs
    public static <T extends AsyncTask<Parameter, ?, ?>, Parameter> T executeTask(T task, Parameter... params) {
        task.executeOnExecutor(DEFAULT_EXECUTOR, params);
        return task;
    }

    public static boolean isTaskRunning(@Nullable AsyncTask task) {
        return task != null && task.getStatus() == AsyncTask.Status.RUNNING;
    }
}
