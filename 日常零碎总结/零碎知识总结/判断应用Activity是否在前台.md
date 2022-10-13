# 判断应用的Activity是否在前台

```java
package com.ts.voiceactuatorservice.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

/**
 * ClassName AppOnForegroundUtil
 * Description TODO
 *
 * @Author liu wei
 * @Date 2022/10/13 下午4:18
 * @Version 1.0
 **/
public class AppOnForegroundUtil {

    private final ActivityManager mActivityManager;
    private final Context mContext;

    public AppOnForegroundUtil(Context context) {
        mContext = context;
        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public boolean isAppOnForeground(String packageName) {
        LogUtil.debug("isAppOnForeground ： packageName: " + packageName);
        //Get all running apps in Android device
        List<ActivityManager.RunningAppProcessInfo> appProcesses = mActivityManager
                .getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance
                    == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && isActivityToForeground(packageName)) {
                LogUtil.debug("isAppOnForeground ： return true");
                return true;
            }
        }
        return false;
    }

    private boolean isActivityToForeground(String packageName) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(packageName)) {
                LogUtil.debug("isApplicationBroughtToBackground ：packageName: " + packageName);
                return true;
            }
        }
        return false;
    }
}

```