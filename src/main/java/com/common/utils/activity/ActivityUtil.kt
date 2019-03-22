package com.common.utils.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.app.ActivityManager
import android.content.ComponentName
import android.graphics.Point
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration


/**
 * Created by Ethan_Xiang on 2017/8/10.
 */
object ActivityUtil {
    //Settings.ACTION_APPLICATION_DETAIL_SETTING
    fun getAppDetailSettingIntent(context: Context): Intent {
        var localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null))
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW)
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName())
        }
        return localIntent
    }

    fun getHotspotSettingIntent(context: Context): Intent {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        //打开网络共享与热点设置页面
        intent.component = ComponentName("com.android.settings", "com.android.settings.Settings\$TetherSettingsActivity")
        return intent
    }

    /**
     * 程序是否在前台运行
     *
     */
    fun isAppOnForeground(context: Context): Boolean {
        val activityManager = context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val packageName = context.getApplicationContext().getPackageName()
        /**
         * 获取Android设备中所有正在运行的App
         */
        val appProcesses = activityManager
                .runningAppProcesses ?: return false
        for (appProcess in appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName == packageName && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true
            }
        }
        return false
    }

    /**
     * 跳转到应用市场
     *
     * @param appPkg
     * ：上传到应用市场上app的包名,不是本项目的包名
     * @param marketPkg
     * ：应用市场的包名
     */
    @Throws(Exception::class)
    fun jumpToMarket(context: Context, appPkg: String, marketPkg: String? = null) {
        val uri = Uri.parse("market://details?id=$appPkg")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (marketPkg != null) {// 如果没给市场的包名，则系统会弹出市场的列表让你进行选择。
            intent.`package` = marketPkg
        }
        context.startActivity(intent)
    }

    @Throws(Exception::class)
    fun openUrlByBrowser(context: Context, url: String) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val uri = Uri.parse(url)
        intent.data = uri
        context.startActivity(intent)
    }

    @Throws(Exception::class)
    fun openUrlByTargetBrowser(context: Context, url: String, packageName: String) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val uri = Uri.parse(url)
        intent.data = uri
        intent.setPackage(packageName)
        context.startActivity(intent)
    }

    @JvmStatic
    fun getStatusBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    fun isNavigationBarShow(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val display = activity.windowManager.defaultDisplay
            val size = Point()
            val realSize = Point()
            display.getSize(size)
            display.getRealSize(realSize)
            return realSize.y != size.y
        } else {
            val menu = ViewConfiguration.get(activity).hasPermanentMenuKey()
            val back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
            return !(menu || back)
        }
    }

    @JvmStatic
    fun getNavigationBarHeight(activity: Activity): Int {
        if (!isNavigationBarShow(activity)) {
            return 0
        }
        val resources = activity.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        //获取NavigationBar的高度
        return resources.getDimensionPixelSize(resourceId)
    }

    @JvmStatic
    fun getScreenHeight(activity: Activity): Int {
        return activity.windowManager.defaultDisplay.height + getNavigationBarHeight(activity)
    }
}