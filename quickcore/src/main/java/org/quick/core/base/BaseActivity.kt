package org.quick.core.base

import android.content.Intent
import org.quick.core.base.activities.ThemeActivity
import org.quick.core.common.IsOkDialog
import org.quick.core.common.LoadingDialog
import org.quick.core.config.Constant
import org.quick.startactivity.StartActivity


/**
 * Created by zoulx on 2017/11/13.
 */
abstract class BaseActivity : ThemeActivity() {
    companion object {
        val SUCCESS = Constant.APP_SUCCESS_TAG
    }
    val isOkDialog: IsOkDialog by lazy { return@lazy IsOkDialog(activity) }
    val loadingDialog: LoadingDialog by lazy { return@lazy LoadingDialog(activity) }

    open fun getId() = getParams(ID, "")

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        StartActivity.onActivityResult(requestCode, resultCode, data)
    }

    fun startActivity(cls:Class<*>):StartActivity.Builder=StartActivity.Builder(this,cls)
}