package org.quick.core.base

import android.content.Context
import android.os.StrictMode
import android.util.Log
import androidx.multidex.MultiDex
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.beta.interfaces.BetaPatchListener
import com.tencent.bugly.beta.upgrade.UpgradeListener
import com.tencent.bugly.beta.upgrade.UpgradeStateListener
import org.litepal.LitePal
import org.quick.base.QuickBroadcast
import org.quick.base.SPHelper
import org.quick.base.Toast
import org.quick.core.base.application.ThemeApp
import org.quick.core.config.Constant
import org.quick.core.config.UrlPath
import org.quick.core.mvp.BaseModel
import org.quick.http.HttpService
import org.quick.http.JsonUtils
import org.quick.http.callback.OnRequestStatusCallback
import org.quick.utils.DateUtils
import java.util.*

open class BaseApp : ThemeApp() {
    override fun onCreate() {
        setStrictMode()
        super.onCreate()
        initBuggly()
        initDB()
        initHttpService()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
//        Beta.installTinker()
    }

    private fun initHttpService() {
        HttpService.Config.addParams("source", "android")
            .addParams("token", SPHelper.getValue(Constant.APP_TOKEN, ""))
            .method(false)
            .baseUrl(UrlPath.baseUrl)
            .onRequestStatus(object : OnRequestStatusCallback {
                override fun onFailure(e: Throwable, isNetworkError: Boolean) {
                    if (isNetworkError) Toast.show("网络异常")
                    else Toast.show("服务器异常")
                }

                override fun onErrorParse(data: String) {
                    val baseModel = JsonUtils.parseFromJson<BaseModel>(data)
                    if (baseModel != null) {
                        when (baseModel.code) {
                            Constant.APP_ERROR_NO_LOGIN -> {
                                Toast.showLong("请登录后再操作")
                            }
//                            else ->
//                                Toast.show("服务器异常")
                        }
                    } else Toast.show("数据异常")
                }
            })
    }

    /**
     * 初始化Bugly
     */
    private fun initBuggly() {
        // 设置是否开启热更新能力，默认为true
        Beta.enableHotfix = true
        // 设置是否自动下载补丁
        Beta.canAutoDownloadPatch = true
        // 设置是否提示用户重启
        Beta.canNotifyUserRestart = true
        // 设置是否自动合成补丁
        Beta.canAutoPatch = true
//        Beta.upgradeCheckPeriod = DateUtils.getInstance.getMINUTE();
        Beta.initDelay = DateUtils.SECOND
        Beta.autoDownloadOnWifi = true

        Beta.upgradeListener =
            UpgradeListener { _, upgradeInfo, _, _ ->
                if (upgradeInfo != null) {
                    QuickBroadcast.send(Constant.APP_UPGRADE)
                }
            }
        /*
           全量升级状态回调
         */
        Beta.upgradeStateListener = object : UpgradeStateListener {
            override fun onUpgradeFailed(b: Boolean) {

            }

            override fun onUpgradeSuccess(b: Boolean) {

            }

            override fun onUpgradeNoVersion(b: Boolean) {
                Toast.show("最新版本")
            }

            override fun onUpgrading(b: Boolean) {
                Toast.show("开始检查更新")
            }

            override fun onDownloadCompleted(b: Boolean) {

            }
        }
        /**
         * 补丁回调接口，可以监听补丁接收、下载、合成的回调
         */
        Beta.betaPatchListener = object : BetaPatchListener {
            override fun onPatchReceived(patchFileUrl: String) {
                Toast.show(patchFileUrl)
            }

            override fun onDownloadReceived(savedLength: Long, totalLength: Long) {
                Toast.show(String.format(
                    Locale.getDefault(),
                    "%s %d%%",
                    Beta.strNotificationDownloading,
                    (if (totalLength == 0L) 0 else savedLength * 100 / totalLength).toInt()))
            }

            override fun onDownloadSuccess(patchFilePath: String) {
                Toast.show(patchFilePath)
                //                Beta.applyDownloadedPatch();
            }

            override fun onDownloadFailure(msg: String) {
                Toast.show(msg)
            }

            override fun onApplySuccess(msg: String) {
                Toast.show(msg)
            }

            override fun onApplyFailure(msg: String) {
                Toast.show(msg)
            }

            override fun onPatchRollback() {
                Toast.show("onPatchRollback")
            }
        }

        val start = System.currentTimeMillis()
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId,调试时将第三个参数设置为true
        Bugly.init(this, onResultBugglyAppId(), SPHelper.getValue("isDebug", false))
        val end = System.currentTimeMillis()
        Log.e("init time--->", (end - start).toString() + "ms")
    }

    private fun onResultBugglyAppId()="c4f6d9153c"

    private fun initDB() {
        LitePal.initialize(this)
    }

    private fun setStrictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())
    }

}