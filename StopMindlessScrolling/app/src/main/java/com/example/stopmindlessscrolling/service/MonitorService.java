package com.example.stopmindlessscrolling.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.stopmindlessscrolling.R;
import com.example.stopmindlessscrolling.utility.AppConstants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MonitorService extends Service {

    public static final String ACTION_START_FOREGROUND_SERVICE = "Start";
    private LinearLayout mLinear;
    private LayoutInflater inflater;
    private WindowManager.LayoutParams mParams;
    private Handler handler = new Handler();
    private View mView;
    private WindowManager mWindowManager;
    IBinder mBinder = new LocalBinder();
    private Context mContext;
    public Boolean isShown=false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final int notify = 1000;  //interval between two services(Here Service run every 5 seconds)
    int count = 0;  //number of times service is display
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public class LocalBinder extends Binder {
        public MonitorService getServerInstance() {
            return MonitorService.this;
        }
    }
    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // display toast
                    Log.e("TAG", "Service is running: " );
                    try {
                        Set<String> selectedAppsSet=sharedPreferences.getStringSet(AppConstants.SELECTEDAPPS,new HashSet<>());

                        Log.e("TAG", "ToppackageName: ~~~~~~" +getProcess());
                        Log.e("TAG", "isShown: ~~~~~~" +isShown);
                        Log.e("TAG", "selectedAppsSet: ~~~~~~" +selectedAppsSet.contains(getProcess()));


                        if (getProcess()!=null &&
                                selectedAppsSet.contains(getProcess())){
                            if (!isShown){
                                editor.putString(AppConstants.VIEWSHOWEDAPP,getProcess());
                                editor.apply();
                                showPopUp();
                            }

                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        }

    }
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        sharedPreferences= getSharedPreferences(AppConstants.SHAREDPREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   //recreate new
        mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);
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

        removeview();
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
    @SuppressLint({"ObsoleteSdkInt", "InlinedApi"})
    private String getProcessNew() {
        String topPackageName = null;
        UsageStatsManager mUsageStatsManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
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


    private void showPopUp() {


        mLinear = new LinearLayout(getApplicationContext());
        mView = new LinearLayout(getApplicationContext());
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.windowmanager_view, mLinear);
        Button button=mView.findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeview();
            }
        });

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG =  WindowManager.LayoutParams.TYPE_PHONE;
            ;
        }
        try {
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, 10, 200,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_SPLIT_TOUCH |
                            WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                    PixelFormat.TRANSLUCENT);


            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            if (mWindowManager != null && !mView.isShown()) {
                Log.e("addView", "~~~~~~~~~~: ");
                mWindowManager.addView(mView, mParams);
                isShown=true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void removeview() {
        try {
            Log.e("~~~~~", "removeview: ");
            Log.e("~~~~~", "removeview: "+mView.isShown());

            if (mWindowManager != null && mView != null) {

                mWindowManager.removeView(mView);
                mWindowManager = null;
                mView = null;
                isShown=false;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
