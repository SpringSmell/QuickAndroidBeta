package com.example.quickandroid.ui

import com.example.quickandroid.R
import kotlinx.android.synthetic.main.activity_ex_quick_toast.*
import org.quick.core.base.BaseActivity
import org.quick.library.service.DownloadService

/**
 *下载示例
 */
class ExDownloadServiceActivity:BaseActivity() {

    override fun onResultLayoutResId(): Int = R.layout.activity_ex_download_service

    override fun onInit() {

    }

    override fun onInitLayout() {

    }

    override fun onBindListener() {
        val apkUrl="https://dldir1.qq.com/weixin/android/weixin673android1360.apk"
        val apkUrl2="https://dldir1.qq.com/weixin/android/weixin706android1480.apk"
        actionTv.setOnClickListener {
            DownloadService.Builder(apkUrl).cover("下载微信").action(this)
            DownloadService.Builder(apkUrl2).cover("下载微信2").action(this)
        }
    }

    override fun onAction() {

    }
}