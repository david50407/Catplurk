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
import tw.catcafe.catplurk.android.plurkapi.model.Response;
import tw.catcafe.catplurk.android.plurkapi.model.ResponsesResponse;
import tw.catcafe.catplurk.android.plurkapi.model.TimelinePlurksResponse;
import tw.catcafe.catplurk.android.plurkapi.model.User;
import tw.catcafe.catplurk.android.provider.CatPlurkDataStore.*;
import tw.catcafe.catplurk.android.task.ManagedAsyncTask;
import tw.catcafe.catplurk.android.util.message.GetPlurksTaskEvent;
import tw.catcafe.catplurk.android.util.message.GetResponsesTaskEvent;
import tw.catcafe.catplurk.android.util.message.PlurkUserUpdatedEvent;

/**
 * @author Davy
 */
public class AsyncPlurkWrapper extends PlurkWrapper {
    private final Context mContext;
    private final AsyncTaskManager mAsyncTaskManager;
    private final ContentResolver mResolver;

    private int mGetTimelineTaskId;
    private int mGetResponseTaskId;

    public AsyncPlurkWrapper(final Context context) {
        mContext = context;
        final Application app = Application.getInstance(context);
        mAsyncTaskManager = app.getAsyncTaskManager();
        mResolver = context.getContentResolver();
    }

    //region APIs
    public boolean getTimelineAsync(final long accountId, final long offset, final long limit,
                                    final long latestId) {
        mAsyncTaskManager.cancel(mGetTimelineTaskId);
        final GetHomeTimelineTask task = new GetHomeTimelineTask(accountId, offset, limit,
                latestId);
        mGetTimelineTaskId = mAsyncTaskManager.add(task, true);
        return true;
    }

    public boolean getResponseAsync(final long accountId, final long plurkId, final long offset,
                                    final long limit) {
        mAsyncTaskManager.cancel(mGetResponseTaskId);
        final GetResponseTask task = new GetResponseTask(accountId, plurkId, offset, limit);
        mGetResponseTaskId = mAsyncTaskManager.add(task, true);
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

    public boolean isResponseRefreshing() {
        return mAsyncTaskManager.hasRunningTasksForTag(TASK_TAG_GET_RESPONSE);
    }

    class GetHomeTimelineTask extends GetPlurksTask {

        public GetHomeTimelineTask(final long accountId, final long offset, final long limit,
                                   final long latestId) {
            super(accountId, offset, limit, latestId, TASK_TAG_GET_TIMELINE);
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

        private final long mAccountId, mOffset, mLimit, mLatestId;

        public GetPlurksTask(final long account_id, final long offset, final long limit,
                             final long latestId, final String tag) {
            super(mContext, mAsyncTaskManager, tag);
            mAccountId = account_id;
            mLatestId = latestId;
            mOffset = offset;
            mLimit = limit;
        }

        public abstract TimelinePlurksResponse getPlurks(PlurkApi plurkApi, Paging paging)
                throws PlurkException;

        @NonNull
        protected abstract Uri getDatabaseUri();

        private void storePlurk(final long accountId, final List<Plurk> plurks,
                                final boolean notify) {
            if (plurks == null || plurks.isEmpty() || accountId <= 0) {
                return;
            }
            final Uri uri = getDatabaseUri();
            final boolean noItemsBefore = DatabaseUtils.getPlurkCountInDatabase(mContext, uri, accountId) <= 0;
            final ContentValues[] values = new ContentValues[plurks.size()];
            final long[] plurkIds = new long[plurks.size()];
            // turn plurk into ContentValue and find out oldest id
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
            // But this will be done by notify receiver in SQLite.
            final Expression accountWhere = Expression.equals(Plurks.ACCOUNT_ID, accountId);
            final Expression statusWhere = Expression.in(new Columns.Column(Plurks.PLURK_ID), new RawItemArray(plurkIds));
            final String countWhere = Expression.and(accountWhere, statusWhere).getSQL();
            final String[] projection = {SQLFunctions.COUNT()};
            final Cursor countCur = mResolver.query(uri, projection, countWhere, null, null);
            final int rowsDeleted = countCur.moveToFirst() ? countCur.getInt(0) : 0;
            countCur.close();

            // Find minId in Database to check if gap is needed.
            boolean needGap = true;
            if (minId > 0) {
                final Cursor minCur = mResolver.query(uri, projection,
                        Expression.and(accountWhere, Expression.and(
                                        Expression.equals(Plurks.PLURK_ID, minId),
                                        Expression.equals(Plurks.IS_GAP, 0))).getSQL(),
                        null, null);
                needGap = !minCur.moveToFirst() || minCur.getInt(0) > 0;
                minCur.close();
            }

            // Insert a gap.
            final boolean noRowsDeleted = rowsDeleted == 0;
            final boolean insertGap = minId > 0 && noRowsDeleted && !noItemsBefore &&
                    plurks.size() > 1 && needGap;
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
            final List<Plurk> valueList = Observable.from(values)
                    .map(SingleResponse::getData)
                    .flatMap(Observable::from)
                    .toList()
                    .toBlocking()
                    .single();
            AsyncTaskUtils.executeTask(new GetReplurkerTask(mAccountId), valueList);
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
                plurks.addAll(apiResponse.getPlurks());
                plurkUsers.addAll(apiResponse.getPlurkUsers().values());
                storePlurk(mAccountId, plurks, true);
                storeUser(mAccountId, plurkUsers, true);
                publishProgress(new PlurkListResponse(mAccountId, plurks));
            } catch (final PlurkException e) {
                Log.w(LOGTAG, e);
                result.add(new PlurkListResponse(mAccountId, e));
            }
            return result;
        }
    }

    class GetReplurkerTask extends ManagedAsyncTask<List<Plurk>, PlurkApiListResponse<Plurk>, List<User>> {

        private final long mAccountId;

        public GetReplurkerTask(final long account_id) {
            super(mContext, mAsyncTaskManager, null);
            mAccountId = account_id;
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
        }

        @Override
        protected void onPostExecute(List<User> result) {
            super.onPostExecute(result);
            final Bus bus = Application.getInstance(mContext).getMessageBus();
            assert bus != null;
            bus.post(new PlurkUserUpdatedEvent());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final Bus bus = Application.getInstance(mContext).getMessageBus();
            assert bus != null;
            bus.post(new PlurkUserUpdatedEvent());
        }

        @Override
        protected List<User> doInBackground(final List<Plurk>... params) {
            if (mAccountId == -1) return null;
            final PlurkApi plurkApi = PlurkAPIFactory.getPlurkApiInstance(mContext, mAccountId);
            if (plurkApi == null) return null;
            final List<User> result = new ArrayList<>();
            for (List<Plurk> plurks : params) {
                final List<Long> replurkerIds = Observable.from(plurks)
                        .filter(plurk -> plurk.getReplurkerId() > 0)
                        .map(Plurk::getReplurkerId)
                        .distinct()
                        .toList()
                        .toBlocking()
                        .single();
                for (long id : replurkerIds) {
                    try {
                        final User user = plurkApi.getPublicProfile(id, true, false).getUserInfo();
                        if (user == null) continue;
                        result.add(user);
                    } catch (final PlurkException e) {
                        Log.w(LOGTAG, e);
                    }
                }
                storeUser(mAccountId, result, true);
            }
            return result;
        }
    }

    class GetResponseTask extends ManagedAsyncTask<Object, PlurkApiListResponse<Response>, List<ResponseListResponse>> {
        private final long mAccountId, mPlurkId, mOffset, mLimit;

        public GetResponseTask(final long account_id, final long plurkId, final long offset,
                               final long limit) {
            super(mContext, mAsyncTaskManager, TASK_TAG_GET_RESPONSE);
            mAccountId = account_id;
            mPlurkId = plurkId;
            mOffset = offset;
            mLimit = limit;
        }

        @NonNull
        protected Uri getDatabaseUri() {
            return Responses.CONTENT_URI;
        }

        private void updatePlurkResponseCount(final long accountId, final long plurkId,
                                              final long count) {
            final ContentValues updateValue = new ContentValues();
            updateValue.put(Plurks.RESPONSE_COUNT, count);
            mResolver.update(Plurks.CONTENT_URI, updateValue,
                    Expression.and(Expression.equals(Plurks.ACCOUNT_ID, accountId),
                            Expression.equals(Plurks.PLURK_ID, plurkId)
                    ).getSQL(), null);
        }

        private void updatePlurkResponseSeen(final long accountId, final long plurkId,
                                              final long seen) {
            final ContentValues updateValue = new ContentValues();
            updateValue.put(Plurks.RESPONSES_SEEN, seen);
            updateValue.put(Plurks.IS_UNREAD, Plurk.ReadState.READ.ordinal());
            mResolver.update(Plurks.CONTENT_URI, updateValue,
                    Expression.and(Expression.equals(Plurks.ACCOUNT_ID, accountId),
                            Expression.equals(Plurks.PLURK_ID, plurkId)
                    ).getSQL(), null);
        }

        private void storeResponse(final long accountId, final long plurkId,
                                   final List<Response> responses, final boolean notify) {
            if (responses == null || responses.isEmpty() || plurkId <= 0 || accountId <= 0)
                return;
            final Uri uri = getDatabaseUri();
//            final boolean noItemsBefore =
//                    DatabaseUtils.getResponseCountInDatabase(mContext, uri, accountId, plurkId) <= 0;
            // turn responses into ContentValue
            ContentValues[] values = new ContentValues[responses.size()];
            values = Observable.from(responses)
                    .map(response -> ContentValueCreator.createResponse(response, accountId))
                    .toList()
                    .toBlocking()
                    .single()
                    .toArray(values);
//            final long[] responseIds = new long[responses.size()];
//            final ContentValues[] values = new ContentValues[responses.size()];
//            long minId = -1;
//            int minIdx = -1;
//            {
//                int i = 0;
//                for (final Response response : responses) {
//                    values[i] = ContentValueCreator.createResponse(response, accountId);
//                    final long id = response.getPlurkId();
//                    if (minId == -1 || id < minId) {
//                        minId = id;
//                        minIdx = i;
//                    }
//                    responseIds[i] = id;
//                    ++i;
//                }
//            }
            // Delete all rows conflicting before new data inserted.
            // But this will be done by notify receiver in SQLite.
//            final Expression accountWhere = Expression.equals(Plurks.ACCOUNT_ID, accountId);
//            final Expression statusWhere = Expression.in(new Columns.Column(Plurks.PLURK_ID), new RawItemArray(responseIds));
//            final String countWhere = Expression.and(accountWhere, statusWhere).getSQL();
//            final String[] projection = {SQLFunctions.COUNT()};
//            final Cursor countCur = mResolver.query(uri, projection, countWhere, null, null);
//            final int rowsDeleted = countCur.moveToFirst() ? countCur.getInt(0) : 0;
//            countCur.close();

            // Find minId in Database to check if gap is needed.
//            boolean needGap = true;
//            if (minId > 0) {
//                final Cursor minCur = mResolver.query(uri, projection,
//                        Expression.and(accountWhere, Expression.and(
//                                Expression.equals(Plurks.PLURK_ID, minId),
//                                Expression.equals(Plurks.IS_GAP, 0))).getSQL(),
//                        null, null);
//                needGap = !minCur.moveToFirst() || minCur.getInt(0) > 0;
//                minCur.close();
//            }

            // Insert a gap.
//            final boolean noRowsDeleted = rowsDeleted == 0;
//            final boolean insertGap = minId > 0 && noRowsDeleted && !noItemsBefore &&
//                    responses.size() > 1 && needGap;
//            if (insertGap && minIdx != -1) {
//                values[minIdx].put(Plurks.IS_GAP, true);
//            }
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

        @Override
        protected void onPostExecute(List<ResponseListResponse> result) {
            super.onPostExecute(result);
            final Bus bus = Application.getInstance(mContext).getMessageBus();
            assert bus != null;
            bus.post(new GetResponsesTaskEvent(getDatabaseUri(), false, getException(result)));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final Bus bus = Application.getInstance(mContext).getMessageBus();
            assert bus != null;
            bus.post(new GetResponsesTaskEvent(getDatabaseUri(), true, null));
        }

        @Override
        protected List<ResponseListResponse> doInBackground(final Object... params) {
            final List<ResponseListResponse> result = new ArrayList<>();
            if (mAccountId == -1) return result;
            // TODO
//            final int loadItemLimit = mPreferences.getInt(KEY_LOAD_ITEM_LIMIT, DEFAULT_LOAD_ITEM_LIMIT);
            final int loadItemLimit = 20;
            final PlurkApi plurkApi = PlurkAPIFactory.getPlurkApiInstance(mContext, mAccountId);
            if (plurkApi == null) return result;
            try {
                final long from_response = mOffset >= 0 ? mOffset : 0;
                final long limit = mLimit > 0 ? mLimit : 20;
                final List<Response> responses = new ArrayList<>();
                final List<User> friends = new ArrayList<>();
                final ResponsesResponse apiResponse = plurkApi.getResponses(mPlurkId,
                        from_response, limit);
                responses.addAll(apiResponse.getResponses());
                // Why Plurk Api response an empty array for empty friends,
                // WTF!? Doesn't this should be a hash?
                if (apiResponse.getFriends() != null)
                    friends.addAll(apiResponse.getFriends().values());
                updatePlurkResponseCount(mAccountId, mPlurkId, apiResponse.getResponseCount());
                updatePlurkResponseSeen(mAccountId, mPlurkId, apiResponse.getResponsesSeen());
                storeResponse(mAccountId, mPlurkId, responses, true);
                storeUser(mAccountId, friends, true);
                publishProgress(new ResponseListResponse(mAccountId, responses));
            } catch (final PlurkException e) {
                Log.w(LOGTAG, e);
                result.add(new ResponseListResponse(mAccountId, e));
            }
            return result;
        }
    }
    //endregion Tasks
}
