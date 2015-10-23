package tw.catcafe.catplurk.android.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.otto.Bus;

import org.mariotaku.sqliteqb.library.Columns;
import org.mariotaku.sqliteqb.library.Expression;
import org.mariotaku.sqliteqb.library.RawItemArray;
import org.mariotaku.sqliteqb.library.SQLFunctions;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import tw.catcafe.catplurk.android.Application;
import tw.catcafe.catplurk.android.api.SingleResponse;
import tw.catcafe.catplurk.android.plurkapi.PlurkApi;
import tw.catcafe.catplurk.android.plurkapi.PlurkException;
import tw.catcafe.catplurk.android.plurkapi.model.Paging;
import tw.catcafe.catplurk.android.plurkapi.model.Plurk;
import tw.catcafe.catplurk.android.plurkapi.model.TimelinePlurksResponse;
import tw.catcafe.catplurk.android.plurkapi.model.User;
import tw.catcafe.catplurk.android.provider.CatPlurkDataStore.*;
import tw.catcafe.catplurk.android.task.ManagedAsyncTask;
import tw.catcafe.catplurk.android.util.message.GetPlurksTaskEvent;

/**
 * @author Davy
 */
public class AsyncPlurkWrapper extends PlurkWrapper {
    private final Context mContext;
    private final AsyncTaskManager mAsyncTaskManager;
    private final ContentResolver mResolver;

    private int mGetTimelineTaskId;

    public AsyncPlurkWrapper(final Context context) {
        mContext = context;
        final Application app = Application.getInstance(context);
        mAsyncTaskManager = app.getAsyncTaskManager();
        mResolver = context.getContentResolver();
    }

    //region APIs
    public boolean getTimelineAsync(final long accountId, final long offset, final long limit,
                                    final long latestId, final long sinceId) {
        mAsyncTaskManager.cancel(mGetTimelineTaskId);
        final GetHomeTimelineTask task = new GetHomeTimelineTask(accountId, offset, limit,
                latestId, sinceId);
        mGetTimelineTaskId = mAsyncTaskManager.add(task, true);
        return true;
    }
    //endregion APIs

    private static <T extends SingleResponse<?>> Exception getException(List<T> responses) {
        for (T response : responses) {
            if (response.hasException()) return response.getException();
        }
        return null;
    }

    //region Tasks
    public boolean isTimelineRefreshing() {
        return mAsyncTaskManager.hasRunningTasksForTag(TASK_TAG_GET_TIMELINE);
    }

    class GetHomeTimelineTask extends GetPlurksTask {

        public GetHomeTimelineTask(final long accountId, final long offset, final long limit,
                                   final long latestId, final long sinceId) {
            super(accountId, offset, limit, latestId, sinceId, TASK_TAG_GET_TIMELINE);
        }

        @Override
        public TimelinePlurksResponse getPlurks(final PlurkApi plurkApi, final Paging paging)
                throws PlurkException {
            return plurkApi.getTimelinePlurks(paging);
        }

        @NonNull
        @Override
        protected Uri getDatabaseUri() {
            return Plurks.CONTENT_URI;
        }

        @Override
        protected void onPostExecute(final List<PlurkListResponse> result) {
            super.onPostExecute(result);
            mGetTimelineTaskId = -1;
        }

        @Override
        protected void onPreExecute() {
            final Intent intent = new Intent(BROADCAST_RESCHEDULE_HOME_TIMELINE_REFRESHING);
            mContext.sendBroadcast(intent);
            super.onPreExecute();
        }

    }

    abstract class GetPlurksTask extends ManagedAsyncTask<Object, PlurkApiListResponse<Plurk>, List<PlurkListResponse>> {

        private final long mAccountId, mOffset, mLimit, mLatestId, mSinceId;

        public GetPlurksTask(final long account_id, final long offset, final long limit,
                             final long latestId, final long sinceId, final String tag) {
            super(mContext, mAsyncTaskManager, tag);
            mAccountId = account_id;
            mLatestId = latestId;
            mSinceId = sinceId;
            mOffset = offset;
            mLimit = limit;
        }

        public abstract TimelinePlurksResponse getPlurks(PlurkApi plurkApi, Paging paging)
                throws PlurkException;

        @NonNull
        protected abstract Uri getDatabaseUri();

        private void storePlurk(final long accountId, final List<Plurk> plurks, final long maxId,
                                final boolean truncated, final boolean notify) {
            if (plurks == null || plurks.isEmpty() || accountId <= 0) {
                return;
            }
            final Uri uri = getDatabaseUri();
            final boolean noItemsBefore = DatabaseUtils.getPlurkCountInDatabase(mContext, uri, accountId) <= 0;
            final ContentValues[] values = new ContentValues[plurks.size()];
            final long[] plurkIds = new long[plurks.size()];
            long minId = -1;
            int minIdx = -1;
            {
                int i = 0;
                for (final Plurk plurk : plurks) {
                    values[i] = ContentValueCreator.createPlurk(plurk, accountId);
                    final long id = plurk.getPlurkId();
                    if (minId == -1 || id < minId) {
                        minId = id;
                        minIdx = i;
                    }
                    plurkIds[i] = id;
                    ++i;
                }
            }
            // Delete all rows conflicting before new data inserted.
            // This will be done by notify receiver in SQLite
            final Expression accountWhere = Expression.equals(Plurks.ACCOUNT_ID, accountId);
            final Expression statusWhere = Expression.in(new Columns.Column(Plurks.PLURK_ID), new RawItemArray(plurkIds));
            final String countWhere = Expression.and(accountWhere, statusWhere).getSQL();
            final String[] projection = {SQLFunctions.COUNT()};
            final int rowsDeleted;
            final Cursor countCur = mResolver.query(uri, projection, countWhere, null, null);
            if (countCur.moveToFirst()) {
                rowsDeleted = countCur.getInt(0);
            } else {
                rowsDeleted = 0;
            }
            countCur.close();

            // Insert a gap.
            final boolean deletedOldGap = rowsDeleted > 0 && ArrayUtils.contains(plurkIds, maxId);
            final boolean noRowsDeleted = rowsDeleted == 0;
            final boolean insertGap = minId > 0 && (noRowsDeleted || deletedOldGap) && !truncated
                    && !noItemsBefore && plurks.size() > 1;
            if (insertGap && minIdx != -1) {
                values[minIdx].put(Plurks.IS_GAP, true);
            }
            // Insert previously fetched items.
            final Uri insertUri = UriUtils.appendQueryParameters(uri, QUERY_PARAM_NOTIFY, notify);
            ContentResolverUtils.bulkInsert(mResolver, insertUri, values);
        }


        private void storeUser(final long accountId, final List<User> users, final boolean notify) {
            if (users == null || users.isEmpty() || accountId <= 0) return;
            final Uri uri = Users.CONETNT_URI;
            final ContentValues[] values = Observable.from(users)
                    .map((user) -> ContentValueCreator.createUser(user, accountId))
                    .toList()
                    .toBlocking()
                    .single()
                    .toArray(new ContentValues[users.size()]);
            final Uri insertUri = UriUtils.appendQueryParameters(uri, QUERY_PARAM_NOTIFY, notify);
            ContentResolverUtils.bulkInsert(mResolver, insertUri, values);
        }

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(PlurkApiListResponse<Plurk>... values) {
//            AsyncTaskUtils.executeTask(new CacheUsersStatusesTask(mContext), values);
        }

        @Override
        protected void onPostExecute(List<PlurkListResponse> result) {
            super.onPostExecute(result);
            final Bus bus = Application.getInstance(mContext).getMessageBus();
            assert bus != null;
            bus.post(new GetPlurksTaskEvent(getDatabaseUri(), false, getException(result)));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final Bus bus = Application.getInstance(mContext).getMessageBus();
            assert bus != null;
            bus.post(new GetPlurksTaskEvent(getDatabaseUri(), true, null));
        }

        @Override
        protected List<PlurkListResponse> doInBackground(final Object... params) {
            final List<PlurkListResponse> result = new ArrayList<>();
            if (mAccountId == -1) return result;
            // TODO
//            final int loadItemLimit = mPreferences.getInt(KEY_LOAD_ITEM_LIMIT, DEFAULT_LOAD_ITEM_LIMIT);
            final int loadItemLimit = 20;
            final PlurkApi plurkApi = PlurkAPIFactory.getPlurkApiInstance(mContext, mAccountId);
            if (plurkApi == null) return result;
            try {
                final Paging paging = new Paging();
                paging.limit(loadItemLimit);
                if (mOffset >= 0)
                    paging.offset(mOffset);
                if (mLimit > 0)
                    paging.limit(mLimit);
                final List<Plurk> plurks = new ArrayList<>();
                final List<User> plurkUsers = new ArrayList<>();
                final TimelinePlurksResponse apiResponse = getPlurks(plurkApi, paging);
                final boolean truncated = truncateApiResponse(apiResponse, plurks, plurkUsers, mSinceId);
                storePlurk(mAccountId, plurks, mLatestId, truncated, true);
                storeUser(mAccountId, plurkUsers, true);
                publishProgress(new PlurkListResponse(mAccountId, plurks));
            } catch (final PlurkException e) {
                Log.w(LOGTAG, e);
                result.add(new PlurkListResponse(mAccountId, e));
            }
            return result;
        }

        public boolean truncateApiResponse(final TimelinePlurksResponse res,
                                           final List<Plurk> plurks,
                                           final List<User> users,
                                           final long sinceId) {
            if (res == null) return false;
            if (sinceId == -1) {
                plurks.addAll(res.getPlurks());
                users.addAll(res.getPlurkUsers().values());
                return false;
            }
            plurks.addAll(Observable.from(res.getPlurks())
                    .filter(plurk -> plurk.getPlurkId() > sinceId)
                    .toList()
                    .toBlocking()
                    .single());
            if (res.getPlurks().size() != plurks.size()) {
//                users.addAll(Observable.from(plurks)
//                        .map(plurk -> res.getPlurkUsers().get(ParseUtil.parseString(plurk.getOwnerId())))
//                        .toList()
//                        .toBlocking()
//                        .single());
                users.addAll(res.getPlurkUsers().values());
            } else {
                users.addAll(res.getPlurkUsers().values());
            }
            return res.getPlurks().size() != plurks.size();
        }
    }
    //endregion Tasks
}