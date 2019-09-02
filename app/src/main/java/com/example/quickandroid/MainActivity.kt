package com.example.quickandroid

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.quickandroid.model.KotlinModel
import com.example.quickandroid.ui.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.nestedScrollView
import org.quick.async.Async
import org.quick.base.QuickBroadcast
import org.quick.core.base.activities.ThemeActivity.Companion.DATA
import org.quick.core.base.activities.ThemeActivity.Companion.TITLE
import org.quick.startactivity.StartActivity
import org.quick.utils.ViewUtils

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nestedScrollView {
            backgroundColor = Color.WHITE
            //            ForegroundLinearLayout
            verticalLayout {
                toolbar {
                    backgroundColor = theme.color(R.attr.colorPrimary)
                    title = getString(R.string.app_name)
                    setTitleTextColor(Color.WHITE)
                }.lparams(
                    matchParent, ViewUtils.getSystemAttrValue(
                        this@MainActivity,
                        android.R.attr.actionBarSize
                    ).toInt()
                )
                textView {
                    text = "BaseActivity"
                    id = text.hashCode()
                    gravity = Gravity.CENTER
                    textSize = sp(7).toFloat()
                    textColor = Color.BLACK
                    backgroundResource = ViewUtils.getSystemAttrTypeValue(
                        this@MainActivity,
                        android.R.attr.selectableItemBackground
                    ).resourceId
                    onClick {
                        StartActivity.Builder(
                            this@MainActivity,
                            ExBaseActivity::class.java
                        )
                            .addParams(TITLE, "示例")
                            .addParams("cover", "this is dataList")
                            .addParams(DATA, KotlinModel())
                            .action()
                    }
                    height = dip(40)
                }.lparams(matchParent, dip(60))
                view {
                    backgroundColor = Color.parseColor("#dfdfdf")
                }.lparams(matchParent, 2) {
                    leftMargin = dip(20)
                    rightMargin = dip(20)
                }
                textView {
                    text = "QuickAdapterActivity"
                    id = text.hashCode()
                    gravity = Gravity.CENTER
                    textSize = sp(7).toFloat()
                    textColor = Color.BLACK
                    backgroundResource = ViewUtils.getSystemAttrTypeValue(
                        this@MainActivity,
                        android.R.attr.selectableItemBackground
                    ).resourceId
                    onClick {
                        StartActivity.Builder(this@MainActivity, ExAdapterActivity::class.java)
                            .addParams(TITLE, "适配器示例")
                            .action()
                    }
                }.lparams(matchParent, dip(60))
                view {
                    backgroundColor = Color.parseColor("#dfdfdf")
                }.lparams(matchParent, 2) {
                    leftMargin = dip(20)
                    rightMargin = dip(20)
                }
                textView {
                    text = "QuickListActivity"
                    id = text.hashCode()
                    gravity = Gravity.CENTER
                    textSize = sp(7).toFloat()
                    textColor = Color.BLACK
                    backgroundResource = ViewUtils.getSystemAttrTypeValue(
                        this@MainActivity,
                        android.R.attr.selectableItemBackground
                    ).resourceId
                    onClick {
                        StartActivity.Builder(this@MainActivity, ExQuickListActivity::class.java)
                            .addParams(TITLE, "示例")
                            .action()
                    }
                }.lparams(matchParent, dip(60))

                view {
                    backgroundColor = Color.parseColor("#dfdfdf")
                }.lparams(matchParent, 2) {
                    leftMargin = dip(20)
                    rightMargin = dip(20)
                }
                textView {
                    text = "HttpService"
                    id = text.hashCode()
                    gravity = Gravity.CENTER
                    textSize = sp(7).toFloat()
                    textColor = Color.BLACK
                    backgroundResource = ViewUtils.getSystemAttrTypeValue(
                        this@MainActivity,
                        android.R.attr.selectableItemBackground
                    ).resourceId
                    onClick {
                        StartActivity.Builder(this@MainActivity, ExHttpServiceActivity::class.java)
                            .addParams(TITLE, "示例")
                            .action()
                    }
                }.lparams(matchParent, dip(60))
                view {
                    backgroundColor = Color.parseColor("#dfdfdf")
                }.lparams(matchParent, 2) {
                    leftMargin = dip(20)
                    rightMargin = dip(20)
                }
                textView {
                    text = "QuickBroadcast"
                    id = text.hashCode()
                    gravity = Gravity.CENTER
                    textSize = sp(7).toFloat()
                    textColor = Color.BLACK
                    backgroundResource = ViewUtils.getSystemAttrTypeValue(
                        this@MainActivity,
                        android.R.attr.selectableItemBackground
                    ).resourceId
                    onClick {
                        StartActivity.Builder(this@MainActivity, ExBroadcastActivity::class.java)
                            .addParams(TITLE, "广播示例")
                            .action()
                    }
                }.lparams(matchParent, dip(60))

                view {
                    backgroundColor = Color.parseColor("#dfdfdf")
                }.lparams(matchParent, 2) {
                    leftMargin = dip(20)
                    rightMargin = dip(20)
                }
                textView {
                    text = "QuickStartActivity"
                    id = text.hashCode()
                    gravity = Gravity.CENTER
                    textSize = sp(7).toFloat()
                    textColor = Color.BLACK
                    backgroundResource = ViewUtils.getSystemAttrTypeValue(
                        this@MainActivity,
                        android.R.attr.selectableItemBackground
                    ).resourceId
                    onClick {
                        StartActivity.Builder(this@MainActivity, ExQuickStartActivity::class.java)
                            .addParams(TITLE, "启动示例")
                            .action()
                    }
                }.lparams(matchParent, dip(60))

                view {
                    backgroundColor = Color.parseColor("#dfdfdf")
                }.lparams(matchParent, 2) {
                    leftMargin = dip(20)
                    rightMargin = dip(20)
                }
                textView {
                    text = "QuickDialog"
                    id = text.hashCode()
                    gravity = Gravity.CENTER
                    textSize = sp(7).toFloat()
                    textColor = Color.BLACK
                    backgroundResource = ViewUtils.getSystemAttrTypeValue(
                        this@MainActivity,
                        android.R.attr.selectableItemBackground
                    ).resourceId
                    onClick {
                        StartActivity.Builder(this@MainActivity, ExQuickDialogActivity::class.java)
                            .addParams(TITLE, "弹框示例")
                            .action()
                    }
                }.lparams(matchParent, dip(60))

                view {
                    backgroundColor = Color.parseColor("#dfdfdf")
                }.lparams(matchParent, 2) {
                    leftMargin = dip(20)
                    rightMargin = dip(20)
                }
                textView {
                    text = "QuickToast"
                    id = text.hashCode()
                    gravity = Gravity.CENTER
                    textSize = sp(7).toFloat()
                    textColor = Color.BLACK
                    backgroundResource = ViewUtils.getSystemAttrTypeValue(
                        this@MainActivity,
                        android.R.attr.selectableItemBackground
                    ).resourceId
                    onClick {
                        StartActivity.Builder(this@MainActivity, ExQuickToastActivity::class.java)
                            .addParams(TITLE, "Toast示例")
                            .action()
                    }
                }.lparams(matchParent, dip(60))

                view {
                    backgroundColor = Color.parseColor("#dfdfdf")
                }.lparams(matchParent, 2) {
                    leftMargin = dip(20)
                    rightMargin = dip(20)
                }
                textView {
                    text = "DownloadService"
                    id = text.hashCode()
                    gravity = Gravity.CENTER
                    textSize = sp(7).toFloat()
                    textColor = Color.BLACK
                    backgroundResource = ViewUtils.getSystemAttrTypeValue(
                        this@MainActivity,
                        android.R.attr.selectableItemBackground
                    ).resourceId
                    onClick {
                        StartActivity.Builder(
                            this@MainActivity,
                            ExDownloadServiceActivity::class.java
                        )
                            .addParams(TITLE, "下载示例")
                            .action()
                    }
                }.lparams(matchParent, dip(60))
            }
        }

        QuickBroadcast.addListener(this, { action, intent ->
            Async.delay({
                /*延迟1秒*/
                toast("MainActivity：收到广播消息")
            }, 1000)

        }, MainActivity::class.java.simpleName)
    }
}
