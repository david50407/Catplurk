package tw.catcafe.catplurk.android.util;

import android.os.AsyncTask;
import android.os.Handler;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tw.catcafe.catplurk.android.task.ManagedAsyncTask;

/**
 * @author Davy
 */
public class AsyncTaskManager {
    private final CopyOnWriteArrayList<ManagedAsyncTask<?, ?, ?>> mTasks = new CopyOnWriteArrayList<>();
    private final Handler mHandler;
    private final ExecutorService mExecutor;
    private static AsyncTaskManager sInstance;

    AsyncTaskManager() {
        this(new Handler());
    }

    AsyncTaskManager(final Handler handler) {
        mHandler = handler;
        mExecutor = Executors.newCachedThreadPool();
    }

    @SafeVarargs
    public final <T> int add(final ManagedAsyncTask<T, ?, ?> task, final boolean exec, final T... params) {
        final int hashCode = task.hashCode();
        mTasks.add(task);
        if (exec) {
            execute(hashCode, params);
        }
        return hashCode;
    }

    public boolean cancel(final int hashCode) {
        return cancel(hashCode, true);
    }

    public boolean cancel(final int hashCode, final boolean mayInterruptIfRunning) {
        final ManagedAsyncTask<?, ?, ?> task = findTask(hashCode);
        if (task != null) {
            task.cancel(mayInterruptIfRunning);
            mTasks.remove(task);
            return true;
        }
        return false;
    }

    /**
     * Cancel all tasks added, then clear all tasks.
     */
    public void cancelAll() {
        for (final ManagedAsyncTask<?, ?, ?> task : getTaskSpecList()) {
            task.cancel(true);
        }
        mTasks.clear();
    }

    @SuppressWarnings("unchecked")
    public final <T> boolean execute(final int hashCode, final T... params) {
        final ManagedAsyncTask<T, ?, ?> task = (ManagedAsyncTask<T, ?, ?>) findTask(hashCode);
        if (task != null) {
            task.executeOnExecutor(mExecutor, params);
            return true;
        }
        return false;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public ArrayList<ManagedAsyncTask<?, ?, ?>> getTaskSpecList() {
        return new ArrayList<>(mTasks);
    }

    public boolean hasRunningTask() {
        for (final ManagedAsyncTask<?, ?, ?> task : getTaskSpecList()) {
            if (task.getStatus() == ManagedAsyncTask.Status.RUNNING) return true;
        }
        return false;
    }

    public boolean hasRunningTasksForTag(final String tag) {
        if (tag == null) return false;
        for (final ManagedAsyncTask<?, ?, ?> task : getTaskSpecList()) {
            if (task.getStatus() == AsyncTask.Status.RUNNING && tag.equals(task.getTag()))
                return true;
        }
        return false;
    }

    public boolean isExecuting(final int hashCode) {
        final ManagedAsyncTask<?, ?, ?> task = findTask(hashCode);
        return task != null && task.getStatus() == AsyncTask.Status.RUNNING;
    }

    public void remove(final int hashCode) {
        try {
            mTasks.remove(findTask(hashCode));
        } catch (final ConcurrentModificationException e) {
            // Ignore.
        }
    }

    private ManagedAsyncTask<?, ?, ?> findTask(final int hashCode) {
        for (final ManagedAsyncTask<?, ?, ?> task : getTaskSpecList()) {
            if (hashCode == task.hashCode()) return task;
        }
        return null;
    }

    public static AsyncTaskManager getInstance() {
        if (sInstance == null) {
            sInstance = new AsyncTaskManager();
        }
        return sInstance;
    }
}
