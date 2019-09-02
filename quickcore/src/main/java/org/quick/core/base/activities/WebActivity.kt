package org.quick.core.base.activities

import android.content.Context
import android.content.Intent
import android.view.View
import org.quick.core.R
import org.quick.core.base.fragments.WebFragment


/**
 * @author Chris zou
 * @Date 2016/11/3
 * @modifyInfo1 Zuo-2016/11/3
 * @modifyContent
 */

class WebActivity : ThemeActivity() {
    override fun onInit() {
        webViewFragment = supportFragmentManager.findFragmentById(R.id.WebFragment) as WebFragment
    }

    override fun onInitLayout() {
        back(R.drawable.ic_close_white_24dp, View.OnClickListener { finish() })
    }

    override fun onBindListener() {

    }

    override fun onAction() {
        webViewFragment.start(intent.getStringExtra("url"))
    }

    lateinit var webViewFragment: WebFragment

    override fun onResultLayoutResId(): Int {
        return R.layout.app_activity_web
    }

    override fun onBackPressed() {
        if (!webViewFragment.onBackPressed()) {
            super.onBackPressed()
        }
    }

    companion object {

        fun startAction(context: Context?, title: String, url: String?) {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra(TITLE, title)
            intent.putExtra("url", url)
            context?.startActivity(intent)
        }
    }
}
