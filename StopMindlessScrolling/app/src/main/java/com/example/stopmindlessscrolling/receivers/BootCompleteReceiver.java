package com.example.stopmindlessscrolling.receivers;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.stopmindlessscrolling.service.MonitorService;


/**
 * BootCompleteReceiver is to start the location service after device rebooted
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        runSeviceMethod(context);


    }
    private void runSeviceMethod(Context context) {


        try {


            if (!isMyServiceRunning(context)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Utility.scheduleJob(this);

                    Intent intentService = new Intent(context, MonitorService.class);
                    intentService.setAction(MonitorService.ACTION_START_FOREGROUND_SERVICE);
                    context.startForegroundService(intentService);


                } else {
                    Log.e("ypp", "run stop");
                    Intent intent = new Intent(context, MonitorService.class);
                    context.startService(intent);

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isMyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MonitorService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
