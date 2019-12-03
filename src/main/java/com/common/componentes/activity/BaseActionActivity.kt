package com.common.componentes.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Point
import android.os.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.appcompat.app.AppCompatActivity
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration
import com.common.componentes.WeakHandler


/**
 * Created by KeriGan on 2017/6/25.
 */
const val KEY_VIEW_BUNDLE = "key_view_bundle"

abstract class BaseActionActivity : AppCompatActivity(), WeakHandler.IHandleMessage {

    private var mLocalBroadcastManger: androidx.localbroadcastmanager.content.LocalBroadcastManager? = null

    private lateinit var mIntentFilter: IntentFilter

    private lateinit var mBroadcastReceiver: SimpleReceiver

    private var mSimpleHandler: SimpleHandler = SimpleHandler(this)

    private var handler: Handler = Handler()

    protected lateinit var stateBundle: Bundle

    companion object {
        const val NAVIGATION_BAR_HEIGHT = "navigation_bar_height"
        const val STATUS_BAR_HEIGHT = "status_bar_height"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mSimpleHandler = SimpleHandler(this)
        super.onCreate(savedInstanceState)
        mLocalBroadcastManger = androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this)
        if (isRegisterActions()) {
            mIntentFilter = IntentFilter()
            mBroadcastReceiver = SimpleReceiver()
            registerActions(mIntentFilter)
            mLocalBroadcastManger?.registerReceiver(mBroadcastReceiver, mIntentFilter)
        }

        stateBundle = Bundle()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBundle(KEY_VIEW_BUNDLE, stateBundle)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        stateBundle = savedInstanceState?.getBundle(KEY_VIEW_BUNDLE) ?: stateBundle
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRegisterActions()) {
            mLocalBroadcastManger?.unregisterReceiver(mBroadcastReceiver)
        }
    }

    inner class SimpleReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            this@BaseActionActivity.handleActions(intent?.action, intent)
        }
    }

    open fun registerActions(intentFilter: IntentFilter?) {
        //to register action by override
    }

    open fun handleActions(action: String?, intent: Intent?) {
        //override
    }

    open fun unregisterActions() {
        mLocalBroadcastManger?.unregisterReceiver(mBroadcastReceiver)
    }

    open fun getIntentFilter(): IntentFilter? {
        return mIntentFilter
    }

    open fun registerActions(array: Array<String>, intentFilter: IntentFilter) {
        for (action in array) {
            intentFilter.addAction(action)
        }
        mLocalBroadcastManger?.unregisterReceiver(mBroadcastReceiver)
        mLocalBroadcastManger?.registerReceiver(mBroadcastReceiver, intentFilter)
        mIntentFilter = intentFilter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun getHandler(): Handler {
        if (handler == null) {
            // crash in here?
            handler = SimpleHandler(this)
        }
        return handler
    }

    override fun handleMessage(msg: Message) {
        //do nothing
    }

    class SimpleHandler(host: BaseActionActivity) :
            WeakHandler<BaseActionActivity>(host)

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

    fun getNavigationBarHeight(activity: Activity): Int {
        if (!isNavigationBarShow(activity)) {
            return 0
        }
        val resources = activity.resources
        val resourceId = resources.getIdentifier(NAVIGATION_BAR_HEIGHT, "dimen", "android")
        //获取NavigationBar的高度
        return resources.getDimensionPixelSize(resourceId)
    }


    fun getScreenHeight(activity: Activity): Int {
        return activity.windowManager.defaultDisplay.height + getNavigationBarHeight(activity)
    }

    fun getStatusBarHeight(): Int {
        val resources = getResources()
        val resourceId = resources.getIdentifier(STATUS_BAR_HEIGHT, "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    open protected fun isRegisterActions(): Boolean {
        return false
    }
}
