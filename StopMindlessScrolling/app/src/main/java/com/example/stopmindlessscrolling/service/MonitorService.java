package com.example.stopmindlessscrolling.service;
import com.example.stopmindlessscrolling.R;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.List;
import java.util.Set;

public class MonitorService extends Service {
    IBinder mBinder = new LocalBinder();
    private Context mContext;
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public class LocalBinder extends Binder {
        public MonitorService getServerInstance() {
            return MonitorService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(mContext, MonitorService.class));
        } else {
            startService(new Intent(mContext, MonitorService.class));
        }
        
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * To run foreground service above v8 devices
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String channelID = "com.example.stopmindlessscrolling";
        String channelName = "StopMindlessScrolling Background Service";
        NotificationChannel chan = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        RemoteViews remoteViews = new RemoteViews("com.example.stopmindlessscrolling",R.layout.custom_notify);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContent(remoteViews)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }


    public String getProcess() throws Exception {
        if (Build.VERSION.SDK_INT >= 21) {
            return getProcessNew();
        } else {
            return getProcessOld();
        }
    }

    /**
     * To check current running apps packgae name in below v21
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    private String getProcessOld() {
        String topPackageName = null;
        ActivityManager activity = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTask = activity.getRunningTasks(1);
        if (runningTask != null) {
            ActivityManager.RunningTaskInfo taskTop = runningTask.get(0);
            ComponentName componentTop = taskTop.topActivity;
            topPackageName = componentTop.getPackageName();
        }
        return topPackageName;
    }

    /**
     * To check current running apps package name
     *
     * @return
     */
    private String getProcessNew() {
        String topPackageName = null;
        UsageStatsManager mUsageStatsManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mUsageStatsManager = (UsageStatsManager) getSystemService("usagestats");
            try {
                long currentTimeMillis = System.currentTimeMillis();
                UsageEvents queryEvents = mUsageStatsManager.queryEvents(currentTimeMillis - 1000 * 10, currentTimeMillis);
                UsageEvents.Event event = new UsageEvents.Event();
                while (queryEvents.hasNextEvent()) {
                    queryEvents.getNextEvent(event);
                    if (event.getEventType() == 1) {
                        topPackageName = event.getPackageName();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return topPackageName;
    }





}
