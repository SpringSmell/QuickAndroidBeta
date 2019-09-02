package com.example.quickandroid.ui

import android.app.Activity
import android.content.Intent
import com.example.quickandroid.R
import kotlinx.android.synthetic.main.activity_ex_startactivity.*
import org.quick.core.base.BaseActivity
import org.quick.startactivity.StartActivity

class ExQuickStartActivity : BaseActivity() {
    override fun onResultLayoutResId(): Int = R.layout.activity_ex_startactivity

    override fun onInit() {

    }

    override fun onInitLayout() {

    }

    override fun onBindListener() {
        actionTv.setOnClickListener {
            /*推荐以静态方式跳转*/
            ExQuickStartActivity2.action(this, "这是标题", "id", "张三")
                .action { resultCode, data ->
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        val key1 = getParams(data, "key1", "")
                        val key2 = getParams(data, "key2", -1)
                        toast("收到参数 key1:$key1 key2:$key2")
                    }
                }
//            startActivity(ExQuickStartActivity2::class.java)
//                .addParams(TITLE, title)
//                .addParams(ID, "id")
//                .addParams("name", "张三")
//                .action { resultCode, data ->
//                    if (resultCode == Activity.RESULT_OK && data != null) {
//                        val key1 = getParams(data, "key1", "")
//                        val key2 = getParams(data, "key2", -1)
//                        toast("收到参数 key1:$key1 key2:$key2")
//                    }
//                }
        }
    }

    override fun onAction() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /*在基类中添加配置*/
        StartActivity.onActivityResult(requestCode, resultCode, data)
    }
}