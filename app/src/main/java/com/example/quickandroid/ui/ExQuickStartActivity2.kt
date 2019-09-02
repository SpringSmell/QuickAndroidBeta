package com.example.quickandroid.ui

import android.app.Activity
import android.content.Context
import com.example.quickandroid.R
import kotlinx.android.synthetic.main.activity_ex_startactivity.*
import org.quick.core.base.BaseActivity
import org.quick.startactivity.StartActivity

class ExQuickStartActivity2 : BaseActivity() {
    override fun onResultLayoutResId(): Int = R.layout.activity_ex_startactivity2

    override fun onInit() {

    }

    override fun onInitLayout() {
        val id = getParams(ID, "")
        val name = getParams("name", "")
        toast("收到参数 id:$id name:$name")
    }

    override fun onBindListener() {
        actionTv.setOnClickListener {
            setResult(
                Activity.RESULT_OK,
                StartActivity.Builder().addParams("key1", "内容1").addParams("key2", 2).build()
            )
            finish()
        }
    }

    override fun onAction() {

    }

    companion object {
        fun action(
            context: Context,
            title: String,
            id: String,
            name: String
        ): StartActivity.Builder {
            return StartActivity.Builder(context, ExQuickStartActivity2::class.java)
                .addParams(TITLE, title)
                .addParams(ID, id)
                .addParams("name", name)
        }
    }
}