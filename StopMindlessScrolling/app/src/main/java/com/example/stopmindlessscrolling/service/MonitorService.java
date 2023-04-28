package com.example.stopmindlessscrolling.service;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import com.example.stopmindlessscrolling.R;
import com.example.stopmindlessscrolling.utility.AppConstants;
import com.example.stopmindlessscrolling.utility.HomeWatcher;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MonitorService extends Service implements View.OnClickListener {
    private HomeWatcher mHomeWatcher;
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
    public Boolean timerStart=false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public int notify = 1000;  //interval between two services(Here Service run every 5 seconds)
    int count = 0;  //number of times service is display
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling
    private Handler handlerAppTimer;
    private Intent intent;
    private TextView activityTxt1;
    private TextView activityTxt2;
    private TextView activityTxt3;
    private TextView activityTxt4;



    public class LocalBinder extends Binder {
        public MonitorService getServerInstance() {
            return MonitorService.this;
        }
    }
    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            mHandler.post(() -> {
                Log.e("TAG", "Service is running: " );

                try {
                    Log.e("TAG", "topPackageName: ~~~~~~" +getProcess());

                    //Store top package name
                    String toppackageName=getProcess();
                    if (toppackageName!=null){
                        editor.putString(AppConstants.TOPPACKAGENAME,getProcess());
                        editor.apply();
                    }

                    //For initiate timer for selected app
//                    try {
//                        Set<String> selectedAppsSet=sharedPreferences.getStringSet(AppConstants.SELECTEDAPPS,new HashSet<>());
//                        String topPackageName=sharedPreferences.getString(AppConstants.TOPPACKAGENAME,"");
//                        if (selectedAppsSet.contains(topPackageName)){
//                           timerStart=false;
//                        }
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }

                    //Handler for App Timer
                    if (!timerStart){

                        int timer=sharedPreferences.getInt(AppConstants.APPTIMELIMITVALUE,0);
                        handlerAppTimer = new Handler();
                        int delay = timer*1000;

                        handler.postDelayed(new Runnable() {
                            public void run() {

                                handler.postDelayed(this, delay);
                                try {
                                    Set<String> selectedAppsSet=sharedPreferences.getStringSet(AppConstants.SELECTEDAPPS,new HashSet<>());
                                    String topPackageName=sharedPreferences.getString(AppConstants.TOPPACKAGENAME,"");
                                    if (selectedAppsSet.contains(topPackageName)){
                                        if (!isShown){
                                            editor.putString(AppConstants.VIEWSHOWEDAPP,topPackageName);
                                            editor.apply();
                                            showPopUp(topPackageName);
                                        }

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, delay);

                        timerStart=true;
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
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
        detectRecentAndHome();

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
        if (mHomeWatcher != null) {
            mHomeWatcher.stopWatch();
        }
    }

    /**
     * To detect home and  recent tabs clicks
     */
    private void detectRecentAndHome() {

        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                Log.e("mHomeWatcher", "onHomePressed: ");
                removeview();

            }

            @Override
            public void onBackPressed() {
                Log.e("mHomeWatcher", "onBackPressed: ");
                removeview();

            }

            @Override
            public void onHomeLongPressed() {
                Log.e("mHomeWatcher", "onHomeLongPressed: ");
                removeview();

            }

        });
        mHomeWatcher.startWatch();


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

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activityTxt1:
                clickEvent(activityTxt1.getText().toString());
                break;
            case R.id.activityTxt2:
                clickEvent(activityTxt2.getText().toString());
                break;
            case R.id.activityTxt3:
                clickEvent(activityTxt3.getText().toString());
                break;
            case R.id.activityTxt4:
                clickEvent(activityTxt4.getText().toString());
                break;

        }
    }



    private void clickEvent(String activity) {
        removeview();
        if (activity.contains("Quiz")){
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(SearchManager.QUERY, "Quiz");
            mContext.startActivity(intent);

        } else if (activity.contains("Explore Motivational Quotes")) {
            try {
                intent=new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.unfoldlabs.unfoldquotes"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } catch (android.content.ActivityNotFoundException anfe) {
                intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.unfoldlabs.unfoldquotes" ));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }

        } else if (activity.contains("Explore Articles")) {
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(SearchManager.QUERY, "Explore Articles");
            mContext.startActivity(intent);

        }else if (activity.contains("Yoga")) {
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(SearchManager.QUERY, "Yoga");
            mContext.startActivity(intent);

        }

    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }
    // slide the view from below itself to the current position
    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }
    @SuppressLint("SetTextI18n")
    private void showPopUp(String packageName) {


        mLinear = new LinearLayout(getApplicationContext());
        mView = new LinearLayout(getApplicationContext());
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.windowmanager_view, mLinear);
        ConstraintLayout constraintLayout=mView.findViewById(R.id.constraintLayout);
        Button continueButton=mView.findViewById(R.id.continueButton);
        Button dismissButton=mView.findViewById(R.id.dismissButton);
        activityTxt1=mView.findViewById(R.id.activityTxt1);
        activityTxt2=mView.findViewById(R.id.activityTxt2);
        activityTxt3=mView.findViewById(R.id.activityTxt3);
        activityTxt4=mView.findViewById(R.id.activityTxt4);



        TextView timerTxt=mView.findViewById(R.id.timerTxt);


        Set<String> activities=sharedPreferences.getStringSet(AppConstants.ACTIVITIES,new HashSet<>());

        String[] strings= activities.toArray(new String[0]);



        if (strings.length==1){
            activityTxt1.setText("1. "+strings[0]);
        }else if (strings.length==2){
            activityTxt1.setText("1. "+strings[0]);
            activityTxt2.setText("2. "+strings[1]);
        }else if (strings.length==3){
            activityTxt1.setText("1. "+strings[0]);
            activityTxt2.setText("2. "+strings[1]);
            activityTxt3.setText("3. "+strings[2]);
        }else if (strings.length==4){
            activityTxt1.setText("1. "+strings[0]);
            activityTxt2.setText("2. "+strings[1]);
            activityTxt3.setText("3. "+strings[2]);
            activityTxt4.setText("4. "+strings[3]);
        }

        activityTxt1.setOnClickListener(this);
        activityTxt2.setOnClickListener(this);
        activityTxt3.setOnClickListener(this);
        activityTxt4.setOnClickListener(this);



//        titleTxt.setText(activities.toString());

//        final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
//        final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
//
//        constraintLayout.setAnimation(slide_up);

//        activityTxt1.setOnClickListener(v -> {
//            removeview();
////            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("yoga"));
////            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            mContext.startActivity(browserIntent);
//            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra(SearchManager.QUERY, "its time to do yoga"); // query contains search string
//            mContext.startActivity(intent);
//        });

        final int[] counter = {30};
        new CountDownTimer(30000, 1000){
            public void onTick(long millisUntilFinished){
                timerTxt.setText(String.valueOf(counter[0]));
                counter[0]--;
            }
            public  void onFinish(){
                removeview();
            }
        }.start();

        continueButton.setText("Continue on "+getApplicationName(mContext, packageName));
        dismissButton.setText("I don't want to open "+getApplicationName(mContext, packageName));
//        titleTxt.setText(sharedPreferences.getString(AppConstants.QUIZQUESTION,""));
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeview();
            }
        });
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeview();
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
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
            mParams.windowAnimations = android.R.style.Animation_Toast;

            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            if (mWindowManager != null && !mView.isShown()) {
                Log.e("addView", "~~~~~~~~~~: ");
                mWindowManager.addView(mView, mParams);
                isShown=true;
                Set<String> recentlyAppsSet=new LinkedHashSet<>();
                recentlyAppsSet.add(packageName);
                editor.putStringSet(AppConstants.RECENTLYOPENAPPS,recentlyAppsSet);
                editor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void removeview() {
        try {
            Log.e("~~~~~", "removeview: ");
            if (mWindowManager != null && mView != null) {

                mWindowManager.removeView(mView);
                mWindowManager = null;
                mView = null;
                isShown=false;
                handler.removeCallbacksAndMessages(null);
                Log.e("TAG", "app timer finished: view removed ");
                timerStart=false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public  static String getApplicationName(Context context,String packageName) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return (String) packageManager.getApplicationLabel(applicationInfo);
    }

}
