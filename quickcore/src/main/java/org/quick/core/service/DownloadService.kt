package org.quick.library.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import org.quick.base.Notify
import org.quick.base.Toast
import org.quick.core.R
import org.quick.core.base.activities.ThemeActivity.Companion.DATA
import org.quick.http.HttpService
import org.quick.http.callback.OnDownloadListener
import org.quick.utils.DateUtils
import org.quick.utils.DevicesUtils
import org.quick.utils.FormatUtils
import org.quick.utils.check.CheckUtils
import java.io.File
import java.io.Serializable
import java.util.*
import kotlin.math.abs

/**
 * Created by work on 2017/7/26.
 * 多任务下载服务
 *
 * @author chris zou
 * @mail chrisSpringSmell@gmail.com
 */

class DownloadService : Service() {
    private var taskList: MutableList<Builder> = ArrayList()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val model = intent.getSerializableExtra(DATA) as Builder
        if (!CheckUtils.isEmpty(model.apkUrl)) {
            if ((0 until taskList.size).map { taskList[it] }.any { it.apkUrl == model.apkUrl }) {
                Toast.show("该任务已建立，请等待")
                return super.onStartCommand(intent, flags, startId)
            }
            download(model)
            taskList.add(model)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("DefaultLocale", "RestrictedApi")
    private fun download(model: Builder) {
        Toast.show("已建立下载任务，请查看通知栏")
        var lastMillisecond = 0L
        var lastProgress = 0.0

        HttpService.Builder(model.apkUrl).tag(model.notificationId.toString())
            .enqueue(object : OnDownloadListener() {
                override fun onFailure(e: Throwable, isNetworkError: Boolean) {
                    e.printStackTrace()
                    Toast.show("下载失败")
                    cancel(model)
                }

                override fun onResponse(value: File?) {
                    model.tempFile = value
                    lastProgress = 0.0

                    Notify.Builder(model.notificationId)
                        .content(model.cover, model.title, "下载完成，点击安装")
                        .addParams(ACTION, ACTION_CLICK)
                        .addParams(DATA, model)
                        .action { context, intent ->
                            when (intent.getStringExtra(ACTION)) {
                                //点击
                                ACTION_CLICK -> installAPK(model)
                                //取消
                                ACTION_CANCEL -> cancel(model)
                            }
                        }
                    installAPK(model)
                }

                override fun onLoading(
                    key: String,
                    bytesRead: Long,
                    totalCount: Long,
                    isDone: Boolean
                ) {
                    val progress = bytesRead * 1.0 / totalCount
                    if (DateUtils.timeInMillis() - lastMillisecond > 1000) {
                        lastMillisecond = DateUtils.timeInMillis()
                        val speed = (progress - lastProgress) * totalCount
                        Notify.Builder(model.notificationId)
                            .defaults(0)
                            .ongoing(true)
                            .content(
                                model.cover, model.title, String.format(
                                    "下载速度：%s",
                                    getHint(totalCount, progress * totalCount, speed)
                                )
                            )
                            .progress(100, (progress * 100).toInt(), false)
                            .action()
                        lastProgress = progress
                    }
                }
            })
    }

    private fun getHint(total: Long, progress: Double, speed: Double): String {
        return String.format(
            "%s %s/s",
            FormatUtils.flow(progress * 8),
            FormatUtils.flow(speed * 8)
        )
    }

    fun installAPK(model: Builder) {
        DevicesUtils.installAPK(this@DownloadService, model.tempFile!!)
        cancel(model)
    }

    fun cancel(model: Builder) {
        HttpService.cancelTask(model.notificationId.toString())
        (0 until taskList.size)
            .map { taskList[it] }
            .filter { it.notificationId == model.notificationId }
            .forEach { taskList.remove(it) }
        Notify.cancel(model.notificationId)
    }

    class Builder(var apkUrl: String) : Serializable {
        internal var title: String = "下载应用"
        internal var notificationId: Int = abs(apkUrl.hashCode())
        internal var cover: Int = R.drawable.ic_cloud_gray_shallow_24dp

        internal var tempFile: File? = null

        fun cover(title: String, cover: Int = R.drawable.ic_cloud_gray_shallow_24dp): Builder {
            this.title = title
            this.cover = cover
            return this
        }

        fun action(context: Context) {
            val intent = Intent(context, DownloadService::class.java)
            intent.putExtra(DATA, this)
            context.startService(intent)
        }
    }

    companion object {

        val TAG = DownloadService::class.java.simpleName
        const val ACTION = "action"
        const val ACTION_CLICK = "actionClick"
        const val ACTION_CANCEL = "actionCancel"

        fun action(context: Context, model: Builder) {
            val intent = Intent(context, DownloadService::class.java)
            intent.putExtra(DATA, model)
            context.startService(intent)
        }
    }
}
