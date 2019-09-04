package com.common.utils;

import android.app.ActivityManager;
import android.content.Context;

public class ProcessUtil {

    /**
     * 判断是不是UI主进程，因为有些东西只能在UI主进程初始化
     */
    public static boolean isAppMainProcess(Context context, String packageName) {
        int pid = android.os.Process.myPid();
        String process = getAppNameByPID(context, pid);
        return packageName.equals(process);
    }

    /**
     * 根据Pid得到进程名
     */
    public static String getAppNameByPID(Context context, int pid) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null || manager.getRunningAppProcesses() == null) {
            return "";
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return "";
    }
}
