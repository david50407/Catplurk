package tw.catcafe.catplurk.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import tw.catcafe.catplurk.android.Application;
import tw.catcafe.catplurk.android.BuildConfig;
import tw.catcafe.catplurk.android.constant.CatPlurkConstant;
import tw.catcafe.catplurk.android.constant.IntentConstant;
import tw.catcafe.catplurk.android.util.AsyncPlurkWrapper;

/**
 * @author Davy
 */
public class RefreshService extends Service implements IntentConstant, CatPlurkConstant {
    private AlarmManager mAlarmManager;
    private AsyncPlurkWrapper mPlurkWrapper;
    private PendingIntent mPendingRefreshHomeTimelineIntent;

    private final BroadcastReceiver mStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (BuildConfig.DEBUG)
                Log.d(LOGTAG, String.format("Refresh service received action %s", action));

            if (BROADCAST_RESCHEDULE_HOME_TIMELINE_REFRESHING.equals(action)) {
                rescheduleHomeTimelineRefreshing();
            }
        }
    };

    //region IBinder
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }
    //endregion IBinder

    //region Events
    @Override
    public void onCreate() {
        super.onCreate();
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Application app = Application.getInstance(this);
        mPlurkWrapper = app.getPlurkWrapper();
        mPendingRefreshHomeTimelineIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(BROADCAST_REFRESH_HOME_TIMELINE), 0);
        final IntentFilter filter = new IntentFilter(BROADCAST_REFRESH_HOME_TIMELINE);
        filter.addAction(BROADCAST_RESCHEDULE_HOME_TIMELINE_REFRESHING);
        registerReceiver(mStateReceiver, filter);
        startAutoRefresh();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mStateReceiver);
        super.onDestroy();
    }
    //endregion Events

    //region Refresh
    private boolean getHomeTimeline(final long accountId, final long offset, final long limit,
                                    final long latestId) {
        return mPlurkWrapper.getTimelineAsync(accountId, offset, limit, latestId);
    }
    //endregion Refresh

    //region Re-Schedule Refershing
    private void rescheduleHomeTimelineRefreshing() {
        mAlarmManager.cancel(mPendingRefreshHomeTimelineIntent);
        // TODO: refresh interval by preferences
        // final long refreshInterval = getRefreshInterval();
        final long refreshInterval = 3 * 60 * 1000;
        mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + refreshInterval,
                refreshInterval, mPendingRefreshHomeTimelineIntent);
    }
    //endregion Re-Schedule Refershing

    //region Auto Refresh
    private boolean startAutoRefresh() {
        stopAutoRefresh();
        // TODO: refresh interval by preferences
        // final long refreshInterval = getRefreshInterval();
        // if (refreshInterval <= 0) return false;
        rescheduleHomeTimelineRefreshing();
        return true;
    }

    private void stopAutoRefresh() {
        mAlarmManager.cancel(mPendingRefreshHomeTimelineIntent);
    }
    //endregion Auto Refresh

    //region Refreshable Filter
    private static class HomeRefreshableFilter implements RefreshableAccountFilter {
        @Override
        public boolean isRefreshable() {
            // return pref.isAutoRefreshHomeTimelineEnabled();
            return false;
        }
    }

    private interface RefreshableAccountFilter {
        // boolean isRefreshable(AccountPreferences pref);
        boolean isRefreshable();
    }
    //endregion Refreshable Filter
}
