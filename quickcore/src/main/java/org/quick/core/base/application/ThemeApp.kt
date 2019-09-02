package org.quick.core.base.application

import android.app.Activity
import android.app.Application
import androidx.annotation.CallSuper
import org.quick.base.QuickAndroid
import java.util.*
import kotlin.system.exitProcess

/**
 * Created by chris on 2015/12/13.
 */
open class ThemeApp : Application() {

    private val Tag = "ThemeApp"
    private val activityList = LinkedList<Activity?>()

    @CallSuper
    override fun onCreate() {
        instance = this
        QuickAndroid.init(this)
        super.onCreate()
    }

    fun addActivity(activity: Activity?) {
        this.activityList.add(activity)
    }

    fun removeActivity(activity: Activity?) {

        if (activity != null) {
            for (mActivity in this.activityList) {
                if (mActivity === activity) {
                    this.activityList.remove(activity)
                    if (!mActivity.isFinishing)
                        mActivity.finish()
                    break
                }
            }
        }
    }

    @JvmOverloads
    fun clearAllActivity(exceptionActivity: String = "") {
        for (activity in activityList) {
            if (activity != null && !activity.isFinishing && activity.javaClass.simpleName != exceptionActivity)
                activity.finish()
        }
    }


    fun exit() {
        try {
            clearAllActivity()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            exitProcess(0)
        }
    }

    companion object {
        lateinit var instance: ThemeApp
    }
}
