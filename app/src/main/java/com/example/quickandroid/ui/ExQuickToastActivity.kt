package com.example.quickandroid.ui

import android.view.Gravity
import com.example.quickandroid.R
import kotlinx.android.synthetic.main.activity_ex_quick_toast.*
import org.jetbrains.anko.longToast
import org.quick.async.Async
import org.quick.async.callback.OnASyncListener
import org.quick.base.SPHelper
import org.quick.base.Toast
import org.quick.core.base.BaseActivity

/**
 * 快速弹出Toast，支持子线程
 */
class ExQuickToastActivity : BaseActivity() {

    var index = 1
    override fun onResultLayoutResId(): Int = R.layout.activity_ex_quick_toast

    override fun onInit() {

    }

    override fun onInitLayout() {

    }

    override fun onBindListener() {
        actionTv.setOnClickListener {
            toast()
        }
        actionAsyncTv.setOnClickListener {
            Async.action(object : OnASyncListener<Boolean> {
                override fun onASync(): Boolean {/*子线程*/
                    toast()
                    return true
                }

                override fun onAccept(value: Boolean) {

                }

            })
        }
    }

    override fun onAction() {

    }

    private fun toast() {
        Toast.Builder()
            .gravity(Gravity.BOTTOM, 0, 200)
            .duration(1)
            .show("这是内容：$index")
        index++
    }
}