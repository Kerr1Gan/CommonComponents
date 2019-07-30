package com.common.utils.activity

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.DownloadManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration
import android.widget.Toast
import java.io.File


/**
 * Created by Ethan_Xiang on 2017/8/10.
 */
object ActivityUtil {
    //Settings.ACTION_APPLICATION_DETAIL_SETTING
    @JvmStatic
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

    @JvmStatic
    fun go2AppDetailSettingIntent(context: Context, packageName: String): Intent {
        var localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            localIntent.setData(Uri.fromParts("package", packageName, null))
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW)
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
            localIntent.putExtra("com.android.settings.ApplicationPkgName", packageName)
        }
        return localIntent
    }

    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
    @Throws(Exception::class)
    fun jumpToMarket(context: Context, appPkg: String, marketPkg: String? = null) {
        val uri = if (appPkg.startsWith("http")) {
            Uri.parse(appPkg)
        } else {
            Uri.parse("market://details?id=$appPkg")
        }
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (marketPkg != null) {// 如果没给市场的包名，则系统会弹出市场的列表让你进行选择。
            intent.`package` = marketPkg
        }
        context.startActivity(intent)
    }

    @JvmStatic
    @Throws(Exception::class)
    fun jumpToMarketByUrl(context: Context, url: String, marketPkg: String? = null) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (marketPkg != null) {// 如果没给市场的包名，则系统会弹出市场的列表让你进行选择。
            intent.`package` = marketPkg
        }
        context.startActivity(intent)
    }

    @JvmStatic
    @Throws(Exception::class)
    fun openUrlByBrowser(context: Context, url: String) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val uri = Uri.parse(url)
        intent.data = uri
        context.startActivity(intent)
    }

    @JvmStatic
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

    @JvmStatic
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

    @JvmStatic
    fun download(context: Context, url: String): String? {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (context is Activity) {
                ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 100)
            }
            Toast.makeText(context, "unable to access write external storage", Toast.LENGTH_SHORT).show()
            return null
        }
        var path: String? = null
        try {
            //创建下载任务,downloadUrl就是下载链接
            val request = DownloadManager.Request(Uri.parse(url))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            //指定下载路径和下载文件名
            val name = url.substring(url.lastIndexOf("/") + 1)
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.separator + name
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setVisibleInDownloadsUi(true)
            //大于11版本手机允许扫描
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                //表示允许MediaScanner扫描到这个文件，默认不允许。
                request.allowScanningByMediaScanner()
            }

            // 设置一些基本显示信息
            request.setTitle(name)
            request.setDescription("下载完后请点击更新")
            request.setMimeType("application/vnd.android.package-archive")
            //获取下载管理器
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            //将下载任务加入下载队列，否则不会进行下载
            downloadManager.enqueue(request)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return path
    }

    // 安装Apk
    @JvmStatic
    fun installApk(context: Context, path: String) {
        try {
            val i = Intent(Intent.ACTION_VIEW)
            i.setDataAndType(Uri.parse("file://$path"), "application/vnd.android.package-archive")
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        } catch (e: Exception) {
            Log.i("CheckUpdateDialogHelper", "安装失败")
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun startInstallPermissionSettingActivity(context: Context, packageName: String, requestCode: Int = 0x33) {
        val intent = Intent()
        //获取当前apk包URI，并设置到intent中（这一步设置，可让“未知应用权限设置界面”只显示当前应用的设置项）
        val packageURI = Uri.parse("package:$packageName")
        intent.data = packageURI
        //设置不同版本跳转未知应用的动作
        if (Build.VERSION.SDK_INT >= 26) {
            //intent = new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageURI);
            intent.action = android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
        } else {
            intent.action = android.provider.Settings.ACTION_SECURITY_SETTINGS
        }
        (context as Activity).startActivityForResult(intent, requestCode)
    }

    @JvmStatic
    fun hasIntallPermission(context: Context): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && context.packageManager.canRequestPackageInstalls()
    }

    @JvmStatic
    fun openApp(context: Context, packageName: String): Boolean {
        try {
            val packageManager = context.packageManager
            val pi = packageManager.getPackageInfo(packageName, 0)
            val resolveIntent = Intent(Intent.ACTION_MAIN, null)
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            resolveIntent.setPackage(pi.packageName)

            val apps = packageManager.queryIntentActivities(resolveIntent, 0)

            val ri = apps.iterator().next()
            if (ri != null) {
                val className = ri.activityInfo.name
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)

                val cn = ComponentName(packageName, className)
                intent.component = cn
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    @JvmStatic
    fun uninstallApp(ctx: Context, packageName: String): Boolean {
        try {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:$packageName")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(intent)
            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return false
    }

    @JvmStatic
    fun checkAppInstalled(context: Context, pkgName: String): Boolean {
        val packageManager = context.packageManager
        val info = packageManager.getInstalledPackages(0)
        if (info == null || info.isEmpty())
            return false

        for (i in 0 until info.size) {
            if (pkgName.equals(info.get(i).packageName)) {
                return true
            }
        }
        return false
    }
}