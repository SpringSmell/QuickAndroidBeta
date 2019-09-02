package com.example.quickandroid.ui

import com.example.quickandroid.R
import kotlinx.android.synthetic.main.activity_ex_broadcast.*
import org.quick.base.QuickBroadcast
import org.quick.core.base.BaseActivity

/**
 * 广播示例
 */
class ExBroadcastActivity : BaseActivity() {
    override fun onResultLayoutResId(): Int = R.layout.activity_ex_broadcast

    override fun onInit() {

    }

    override fun onInitLayout() {

    }

    override fun onBindListener() {
        registerTv.setOnClickListener {
            QuickBroadcast.addListener(this, { action, intent ->
                toast("收到广播")
            }, ExBroadcastActivity::class.java.simpleName)
        }
        actionTv.setOnClickListener {
            startActivity(ExBroadcastActivity2::class.java)
                .addParams(TITLE, "广播示例")
                .action()
        }
    }

    override fun onAction() {

    }
}