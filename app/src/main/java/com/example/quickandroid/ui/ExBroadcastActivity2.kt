package com.example.quickandroid.ui

import com.example.quickandroid.MainActivity
import com.example.quickandroid.R

import kotlinx.android.synthetic.main.activity_ex_broadcast2.*
import org.quick.base.QuickBroadcast
import org.quick.core.base.BaseActivity

/**
 * 广播示例
 */
class ExBroadcastActivity2 : BaseActivity() {

    override fun onResultLayoutResId(): Int = R.layout.activity_ex_broadcast2

    override fun onInit() {

    }

    override fun onInitLayout() {

    }

    override fun onBindListener() {
        sendTv.setOnClickListener {
            QuickBroadcast.send(ExBroadcastActivity::class.java.simpleName)
        }
        sendMoreTv.setOnClickListener {
            QuickBroadcast.send(
                MainActivity::class.java.simpleName,
                ExBroadcastActivity::class.java.simpleName
            )
        }
    }

    override fun onAction() {

    }
}