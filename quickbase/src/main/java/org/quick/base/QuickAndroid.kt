package org.quick.base

import android.annotation.SuppressLint
import android.content.Context
import org.quick.dialog.QuickDialog
import org.quick.startactivity.StartActivity

@SuppressLint("StaticFieldLeak")
/**
 * @describe
 * @author ChrisZou
 * @date 2018/7/6-10:30
 * @email chrisSpringSmell@gmail.com
 */
object QuickAndroid {

    /**
     * app的基础名称，用于配置SharedPreferences
     */
    var appBaseName = System.currentTimeMillis().toString()
    lateinit var applicationContext: Context

    fun init(applicationContext: Context) {
        this.applicationContext = applicationContext.applicationContext
        appBaseName = applicationContext.packageName+System.currentTimeMillis().toString()
    }

    /**
     * 设置组件是否输出日志
     */
    fun setDebug(isDebug: Boolean) {

    }

    fun resetInternal() {
        SPHelper.clearAll()
        QuickBroadcast.resetInternal()
        StartActivity.resetInternal()
        QuickDialog.resetInternal()
    }
}